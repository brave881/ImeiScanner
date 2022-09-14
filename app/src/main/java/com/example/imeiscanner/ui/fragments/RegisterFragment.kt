package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.databinding.FragmentRegisterBinding
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.replaceFragment
import com.example.imeiscanner.utilits.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


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

        binding.registerBtnGoogle.setOnClickListener { }
        val phoneNumber = binding.registerInputPhoneNumber.text.toString()
        binding.registerBtnSign.setOnClickListener { replaceFragment(EnterCodeFragment(phoneNumber)) }
        options(phoneNumber)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted:$credential")
                AUTH.signInWithCredential(credential).addOnSuccessListener {

                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
             replaceFragment(EnterCodeFragment(phoneNumber,verificationId))
                Log.d("TAG", "onCodeSent:$verificationId")
            }
        }
        if (phoneNumber.isNotEmpty()) {
            enterCode()
        } else {
            showToast(getString(R.string.enter_code_toast_text))
        }
    }

    private fun enterCode() {

    }


    private fun options(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)                // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
            .setActivity(MAIN_ACTIVITY)                 // Activity (for callback binding)
            .setCallbacks(callbacks)                    // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}