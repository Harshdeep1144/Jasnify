package com.harshdeep.jasnify.presentation.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import android.content.Context
import javax.inject.Inject
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.harshdeep.jasnify.data.remote.CloudinaryManager
import com.harshdeep.jasnify.domain.model.User
import com.harshdeep.jasnify.domain.model.UserRole
import com.harshdeep.jasnify.domain.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : AuthState()
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val preferenceManager: com.harshdeep.jasnify.data.local.prefs.PreferenceManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun hasCompletedOnboarding(): Boolean {
        return preferenceManager.hasCompletedOnboarding()
    }

    fun setCompletedOnboarding(completed: Boolean) {
        preferenceManager.setHasCompletedOnboarding(completed)
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    // --- 1. Phone Authentication Logic (Existing) ---

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _authState.value = AuthState.Loading
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Callbacks for Phone Auth
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-sign-in is generally handled internally or by user actions
            // For a simple flow, we only update state on error/code sent
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _authState.value = AuthState.Error(e.message ?: "Phone verification failed.")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            _authState.value = AuthState.CodeSent(verificationId, token)
        }
    }

    // Placeholder for OTP verification
    fun verifyOtpAndSignIn(verificationId: String, otpCode: String) {
        // ... actual implementation using PhoneAuthProvider.getCredential ...
    }


    // --- 2. Email/Password Authentication Logic ---

    fun handleEmailAuth(email: String, password: String, eventId: String? = null) {
        val cleanEmail = email.lowercase().trim()
        android.util.Log.d("AuthViewModel", "handleEmailAuth: email=$cleanEmail, eventId=$eventId")
        _authState.value = AuthState.Loading
        // Attempt to sign in first
        auth.signInWithEmailAndPassword(cleanEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        viewModelScope.launch {
                            val userEmail = firebaseUser.email ?: email
                            val hasAccess = if (eventId != null) {
                                val result = userRepository.checkUserHasAccessToEvent(eventId, userEmail, firebaseUser.uid)
                                android.util.Log.d("AuthViewModel", "Access check result for event $eventId: $result")
                                result
                            } else true

                            if (hasAccess) {
                                // 1. Create profile if it doesn't exist FIRST
                                val profile = createProfile(
                                    uid = firebaseUser.uid,
                                    email = userEmail,
                                    name = firebaseUser.displayName ?: cleanEmail.substringBefore("@"),
                                    photoUrl = firebaseUser.photoUrl?.toString()
                                )

                                // 2. REQUIREMENT: Always promote pending access to real membership on login
                                userRepository.grantAccessFromPending(eventId ?: "", userEmail, firebaseUser.uid, profile)

                                // 3. Cancel any pending account deletion if user logs in
                                userRepository.cancelAccountDeletion(firebaseUser.uid)

                                _authState.value = AuthState.Success("Successfully logged in!")
                            } else {
                                android.util.Log.w("AuthViewModel", "Access DENIED. Signing out.")
                                auth.signOut()
                                _authState.value = AuthState.Error("You don't have access to this event.")
                            }
                        }
                    }
                } else {
                    // If login fails, attempt to create a new user (Sign Up)
                    android.util.Log.d("AuthViewModel", "Login failed, attempting Sign Up")
                    signUpWithEmailAndPassword(cleanEmail, password, eventId)
                }
            }
    }

    private fun signUpWithEmailAndPassword(email: String, password: String, eventId: String? = null) {
        val cleanEmail = email.lowercase().trim()
        auth.createUserWithEmailAndPassword(cleanEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        viewModelScope.launch {
                            val userEmail = firebaseUser.email ?: cleanEmail
                            android.util.Log.d("AuthViewModel", "Sign Up successful. Checking access for $userEmail to event $eventId")
                            
                            val hasAccess = if (eventId != null) {
                                val result = userRepository.checkUserHasAccessToEvent(eventId, userEmail, firebaseUser.uid)
                                android.util.Log.d("AuthViewModel", "Access check result for event $eventId: $result")
                                result
                            } else true

                            if (hasAccess) {
                                // 1. Create profile if it doesn't exist FIRST
                                val profile = createProfile(
                                    uid = firebaseUser.uid,
                                    email = userEmail,
                                    name = firebaseUser.displayName ?: cleanEmail.substringBefore("@"),
                                    photoUrl = firebaseUser.photoUrl?.toString()
                                )

                                // 2. REQUIREMENT: Always promote pending access to real membership on signup
                                userRepository.grantAccessFromPending(eventId ?: "", userEmail, firebaseUser.uid, profile)

                                // 3. Cancel any pending account deletion if user signs up
                                userRepository.cancelAccountDeletion(firebaseUser.uid)

                                _authState.value = AuthState.Success("Account created!")
                            } else {
                                // If they signed up via ID but weren't granted access, we keep the account but don't let them join the event
                                android.util.Log.w("AuthViewModel", "User signed up via ID but no access request found for $userEmail")
                                createProfile(firebaseUser.uid, userEmail, cleanEmail.substringBefore("@"), firebaseUser.photoUrl?.toString())
                                _authState.value = AuthState.Error("Account created, but you don't have access to that event.")
                            }
                        }
                    }
                } else {
                    android.util.Log.e("AuthViewModel", "Sign up failed: ${task.exception?.message}")
                    _authState.value = AuthState.Error("Sign up failed: ${task.exception?.message}")
                }
            }
    }


    // --- 3. Google Sign-In Authentication Logic ---

    fun signInWithGoogle(account: GoogleSignInAccount, eventId: String? = null) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        viewModelScope.launch {
                            val userEmail = firebaseUser.email ?: ""
                            val hasAccess = if (eventId != null) {
                                userRepository.checkUserHasAccessToEvent(eventId, userEmail, firebaseUser.uid)
                            } else true

                            if (hasAccess) {
                                // 1. Create profile if it doesn't exist FIRST
                                val profile = createProfile(
                                    uid = firebaseUser.uid,
                                    email = userEmail,
                                    name = firebaseUser.displayName ?: "",
                                    photoUrl = firebaseUser.photoUrl?.toString()
                                )

                                // 2. REQUIREMENT: Always promote pending access to real membership on Google login
                                userRepository.grantAccessFromPending(eventId ?: "", userEmail, firebaseUser.uid, profile)

                                // 3. Cancel any pending account deletion if user logs in
                                userRepository.cancelAccountDeletion(firebaseUser.uid)

                                _authState.value = AuthState.Success("Authenticated with Google")
                            } else {
                                auth.signOut()
                                _authState.value = AuthState.Error("You don't have access to this event.")
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error( "Sign-In failed")
                }
            }
    }

    private suspend fun createProfile(uid: String, email: String, name: String, photoUrl: String? = null): User? {
        try {
            val existingProfile = userRepository.getUserProfile(uid)
            if (existingProfile == null) {
                val username = generateUsernameFromEmail(email)
                
                var cloudinaryUrl: String? = null
                if (photoUrl != null) {
                    try {
                        cloudinaryUrl = cloudinaryManager.uploadProfilePictureFromUrl(photoUrl, uid)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val newUser = User(
                    uid = uid,
                    name = name.ifBlank { username },
                    email = email.lowercase().trim(),
                    username = username,
                    role = UserRole.VIEWER,
                    profilePictureUrl = cloudinaryUrl ?: photoUrl
                )
                userRepository.createUserProfile(newUser)
                return newUser
            } else if (existingProfile.profilePictureUrl == null && photoUrl != null) {
                var cloudinaryUrl: String? = null
                try {
                    cloudinaryUrl = cloudinaryManager.uploadProfilePictureFromUrl(photoUrl, uid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val updatedProfile = existingProfile.copy(profilePictureUrl = cloudinaryUrl ?: photoUrl)
                userRepository.updateUserProfile(updatedProfile)
                return updatedProfile
            }
            return existingProfile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun generateUsernameFromEmail(email: String): String {
        val base = email.substringBefore("@").filter { it.isLetterOrDigit() }
        // Ensure a minimum length for base if possible, or just append random digits
        val cleanBase = if (base.isEmpty()) "user" else base.lowercase()
        return cleanBase + (1000..9999).random().toString()
    }


    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun updateLastActive(uid: String, isMerchant: Boolean) {
        viewModelScope.launch {
            userRepository.updateLastActive(uid, isMerchant)
        }
    }

    fun updateFcmToken(uid: String, token: String) {
        viewModelScope.launch {
            userRepository.updateFcmToken(uid, token)
        }
    }

    fun updateAppVersion(uid: String, versionCode: Int) {
        viewModelScope.launch {
            userRepository.updateAppVersion(uid, versionCode)
        }
    }

    fun logout(context: Context) {
        _authState.value = AuthState.Loading
        try {
            // 1. Firebase Sign Out (Handles Email/Password and Phone sessions)
            auth.signOut()

            // 2. Google Sign-Out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                 .requestIdToken(com.harshdeep.jasnify.BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            // Add a listener to wait for the Google sign-out to complete before setting AuthState.Success
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Only set success state after the asynchronous Google sign-out is done
                    _authState.value = AuthState.Success("Logged out!")
                } else {
                    // If Google sign-out fails, the user is still logged out of Firebase.
                    _authState.value = AuthState.Success("Logged out!")
                }
            }

        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Logout failed.")
        }
    }

    fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Loading
            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    viewModelScope.launch {
                        try {
                            val userProfile = userRepository.getUserProfile(user.uid)
                            if (userProfile != null) {
                                userRepository.updateUserProfile(
                                    userProfile.copy(lastPasswordChangeTimestamp = System.currentTimeMillis())
                                )
                            }
                            _authState.value = AuthState.Success("Password updated successfully")
                        } catch (e: Exception) {
                            // Even if profile update fails, the password is changed in Firebase Auth
                            _authState.value = AuthState.Success("Password updated successfully")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(updateTask.exception?.message ?: "Failed to update password")
                }
            }
        } else {
            _authState.value = AuthState.Error("Not logged in")
        }
    }

    fun verifyPassword(password: String, onSuccess: () -> Unit) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            _authState.value = AuthState.Loading
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    _authState.value = AuthState.Idle
                    onSuccess()
                } else {
                    _authState.value = AuthState.Error("Wrong password")
                }
            }
        } else {
            _authState.value = AuthState.Error("Not logged in or email not found")
        }
    }

    fun deleteAccount() {
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Loading
            
            viewModelScope.launch {
                val uid = user.uid
                try {
                    // 1. Delete Cloudinary Assets (Profile Folder)
                    // Do this first as it doesn't depend on Firebase Auth UID for rules (usually)
                    cloudinaryManager.deleteProfilePicture(uid)
                    
                    // 2. Delete Firestore Profile data while still authenticated
                    // This is IMPORTANT: Security rules will likely prevent this after user.delete()
                    userRepository.deleteUserProfile(uid)
                    
                    // 3. Delete Firebase Auth User
                    user.delete().await()
                    
                    _authState.value = AuthState.Success("Account deleted successfully")
                } catch (e: Exception) {
                    if (e is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                        _authState.value = AuthState.Error("Please log out and log back in to delete your account for security reasons.")
                    } else {
                        // If fire store deletion failed, we still try to delete the auth user
                        // or inform the user.
                        _authState.value = AuthState.Error(e.message ?: "Failed to delete account data.")
                    }
                }
            }
        } else {
            _authState.value = AuthState.Error("Not logged in")
        }
    }

    fun requestAccountDeletion() {
        val user = auth.currentUser
        if (user != null) {
            _authState.value = AuthState.Loading
            viewModelScope.launch {
                try {
                    userRepository.scheduleAccountDeletion(user.uid, user.email ?: "")
                    // Sign out the user to "lock" the account as per requirements
                    auth.signOut()
                    _authState.value = AuthState.Success("Account deletion requested")
                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.message ?: "Failed to request account deletion")
                }
            }
        } else {
            _authState.value = AuthState.Error("Not logged in")
        }
    }

    fun checkSessionValidity(context: Context, onInvalid: () -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                val profile = userRepository.getUserProfile(user.uid)
                if (profile?.explicitLogoutTimestamp != null) {
                    val lastSignIn = user.metadata?.lastSignInTimestamp ?: 0L
                    if (lastSignIn < profile.explicitLogoutTimestamp) {
                        logout(context)
                        onInvalid()
                    }
                }
            }
        }
    }

}
