//package com.harshdeep.jasnify.presentation.viewmodels
//
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.FirebaseException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.PhoneAuthCredential
//import com.google.firebase.auth.PhoneAuthOptions
//import com.google.firebase.auth.PhoneAuthProvider
//import com.google.firebase.database.FirebaseDatabase
//import com.harshdeep.jasnify.models.PhoneAuthUser
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import java.util.concurrent.TimeUnit
//import javax.inject.Inject
//import com.harshdeep.jasnify.presentation.viewmodels.AuthState
//
//@HiltViewModel
//class PhoneAuthViewModel @Inject constructor(
//    private val firebaseAuth: FirebaseAuth,
//    private val database: FirebaseDatabase
//): ViewModel() {
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Ideal)
//    val authState = _authState.asStateFlow()
//
//    private var verificationId: String? = null // Holds the ID needed for manual OTP verification
//
//    private val userRef = database.reference.child("users")
//
//    /**
//     * Initiates the phone number verification process with Firebase.
//     * Requires the hosting [Activity] for SMS auto-retrieval.
//     */
//    fun sendVerificationCode(phoneNumber: String, activity: Activity){
//
//        _authState.value = AuthState.Loading
//
//        val option = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
//
//            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
//                super.onCodeSent(id, token)
//                Log.d("PhoneAuth", "onCodeSent triggered. verification ID: $id")
//                verificationId = id // Store the ID
//
//                // NOTE: We don't change state to CodeSent here, but in the calling screen (LoginOrSignup)
//                // we observe this state change to trigger navigation.
//                _authState.value = AuthState.CodeSent(verificationId = id)
//            }
//
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                // Auto-verification happened - immediately sign in
//                Log.d("PhoneAuth", "Verification completed: Auto-signing in.")
//                // Pass the activity's application context to avoid leaks
//                signWithCredential(credential, context = activity.applicationContext)
//            }
//
//            override fun onVerificationFailed(exception: FirebaseException) {
//                Log.e("PhoneAuth", "Verification failed: ${exception.message}")
//                _authState.value = AuthState.Error(exception.message ?: "Verification failed")
//            }
//        }
//
//        val phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
//            .setPhoneNumber(phoneNumber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(activity)
//            .setCallbacks(option)
//            .build()
//
//        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
//    }
//
//    /**
//     * Verifies the manually entered OTP code using the stored verification ID.
//     */
//    fun verifyCode(otp: String, context: Context) = viewModelScope.launch {
//        if (verificationId == null) {
//            _authState.value = AuthState.Error("Verification ID is missing. Try sending the code again.")
//            return@launch
//        }
//
//        _authState.value = AuthState.Loading
//
//        try {
//            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
//            // Pass the application context to avoid holding a reference to a short-lived component
//            signWithCredential(credential, context.applicationContext)
//        } catch (e: Exception) {
//            _authState.value = AuthState.Error("Invalid OTP format or missing verification ID.")
//        }
//    }
//
//
//    /**
//     * Signs in the user with the given [PhoneAuthCredential].
//     * Upon success, marks the user as signed in and fetches/registers their profile.
//     */
//    private fun signWithCredential(credential: PhoneAuthCredential, context: Context){
//
//        _authState.value = AuthState.Loading
//
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    val user = firebaseAuth.currentUser
//                    val phoneAuthUser = PhoneAuthUser(
//                        userId = user?.uid?: "",
//                        phoneNumber = user?.phoneNumber?: ""
//                    )
//
//                    markUserAsSignedIn(context)
//                    fetchUserProfile(user?.uid?: "", phoneAuthUser)
//
//                }else{
//                    _authState.value = AuthState.Error(task.exception?.message?: "Sign-in failed")
//                }
//            }
//    }
//
//
//    private fun markUserAsSignedIn(context: Context){
//        // Stores a boolean flag in SharedPreferences to maintain sign-in state across app restarts.
//        val sharedPreference = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        sharedPreference.edit().putBoolean("isSignedIn", true).apply()
//    }
//
//
//    /**
//     * Checks if the user exists in the Realtime Database. If they don't, registers a new profile.
//     */
//    private fun fetchUserProfile(userId: String, defaultUser: PhoneAuthUser){
//        val userRef = userRef.child(userId)
//        userRef.get().addOnSuccessListener { snapshot ->
//
//            if (snapshot.exists()){
//                // Existing user
//                val userProfile = snapshot.getValue(PhoneAuthUser::class.java)
//                if (userProfile != null){
//                    _authState.value = AuthState.Success(userProfile)
//                } else {
//                    _authState.value = AuthState.Error("Failed to parse user profile.")
//                }
//            } else {
//                // New user - register
//                userRef.setValue(defaultUser)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            _authState.value = AuthState.Success(defaultUser)
//                        } else {
//                            _authState.value = AuthState.Error("Signed in, but failed to save profile.")
//                        }
//                    }
//            }
//        }.addOnFailureListener {
//            _authState.value = AuthState.Error("Failed to check user profile existence: ${it.message}")
//        }
//    }
//
//    /**
//     * Utility function to check if the user is already signed in via SharedPreferences.
//     */
//    fun isUserSignedIn(context: Context): Boolean {
//        val sharedPreference = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        return sharedPreference.getBoolean("isSignedIn", false)
//    }
//
//    fun resetAuthState() {
//        _authState.value = AuthState.Ideal
//    }
//}
//
///**
// * Sealed class representing the different states of the phone authentication flow.
// */
//sealed class AuthState{
//    object Ideal: AuthState()
//    object Loading: AuthState()
//    data class CodeSent(val verificationId: String): AuthState()
//    data class Success(val user: PhoneAuthUser): AuthState()
//    data class Error(val message:String): AuthState()
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
