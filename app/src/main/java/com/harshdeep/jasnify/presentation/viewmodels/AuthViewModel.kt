package com.harshdeep.jasnify.presentation.viewmodels

import com.harshdeep.jasnify.R
import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
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


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : AuthState()
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

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

    fun handleEmailAuth(email: String, password: String) {
        _authState.value = AuthState.Loading
        // Attempt to sign in first
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Successfully logged in!")
                } else {
                    // If login fails, attempt to create a new user (Sign Up)
                    signUpWithEmailAndPassword(email, password)
                }
            }
    }

    private fun signUpWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Changed message to clearly indicate sign-up/creation
                    _authState.value = AuthState.Success("Account successfully created!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign up failed")
                }
            }
    }


    // --- 3. Google Sign-In Authentication Logic ---

    fun signInWithGoogle(account: GoogleSignInAccount) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Successfully logged in with Google!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Google Sign-In failed")
                }
            }
    }


    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }


    fun logout(context: Context) {
        _authState.value = AuthState.Loading
        try {
            // 1. Firebase Sign Out (Handles Email/Password and Phone sessions)
            auth.signOut()

            // 2. Google Sign-Out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                 .requestIdToken(context.getString(R.string.default_web_client_id))
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            // Add a listener to wait for the Google sign-out to complete before setting AuthState.Success
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Only set success state after the asynchronous Google sign-out is done
                    _authState.value = AuthState.Success("Logged out successfully!")
                } else {
                    // If Google sign-out fails, the user is still logged out of Firebase.
                    _authState.value = AuthState.Success("Logged out successfully! (Google client clear warning: ${task.exception?.message})")
                }
            }

        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Logout failed.")
        }
    }

}





