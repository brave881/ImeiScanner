package com.example.imeiscanner.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentRegisterBinding
import com.example.imeiscanner.utilits.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mPhoneNumber: String
    private lateinit var signInRequest: BeginSignInRequest
    private val showOneTapUi = true
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.registerBtnGoogle.setOnClickListener {
            signWithGoogle()
            signIn()
        }
        binding.registerBtnSign.setOnClickListener { sendCode() }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SiGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SiGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
                Log.d("TAG", "on: ${account.idToken}")
            } catch (e: Exception) {
                showToast(e.message.toString())
                Log.d("TAG", "on 1: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        AUTH.signInWithCredential(credential).addOnSuccessListener {
            val user = AUTH.currentUser
            updateUi(user)
            restartActivity()
            Log.d("TAG", "fb: ${user}")
        }.addOnFailureListener {
            showToast(it.message.toString())
            Log.d("TAG", "fb1: ${it.message}")
            updateUi(null)
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        val uid = user?.uid
        val dataMap = mutableMapOf<String, Any>()

        dataMap[CHILD_ID] = user!!.uid
        dataMap[CHILD_EMAIL] = user.email.toString()
        dataMap[CHILD_FULLNAME] = user.displayName.toString()
        dataMap[CHILD_PHOTO_URL] = user.photoUrl.toString()

        val mapUser = mutableMapOf<String, Any>()
        mapUser["$NODE_GOOGLE_USERS/$uid"] = dataMap

        REF_DATABASE_ROOT.updateChildren(mapUser)
    }

    private fun signWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(MAIN_ACTIVITY, gso)


//        signInRequest = BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
//            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                .setSupported(true)
//                .setServerClientId(getString(R.string.default_web_client_id))
//                .setFilterByAuthorizedAccounts(true)
//                .build()
//        ).build()

    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.isNotEmpty()) {
            options()
        } else {
            showToast(getString(R.string.enter_code_toast_text))
        }
    }

    private fun getCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                AUTH.signInWithCredential(credential).addOnSuccessListener {
                    restartActivity()
                    showToast("Welcome")
                }.addOnFailureListener { showToast(it.message.toString()) }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, verificationId))
            }
        }
        return callbacks
    }

    private fun options() {
        mPhoneNumber = binding.registerInputPhoneNumber.text.toString()
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(mPhoneNumber)                // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
            .setActivity(MAIN_ACTIVITY)                 // Activity (for callback binding)
            .setCallbacks(getCallbacks())                    // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}