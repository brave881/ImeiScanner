package com.example.imeiscanner.ui.fragments.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.firebaseAuthWithGoogle
import com.example.imeiscanner.database.getCallbacks
import com.example.imeiscanner.databinding.FragmentRegisterBinding
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.RC_SiGN_IN
import com.example.imeiscanner.utilits.restartActivity
import com.example.imeiscanner.utilits.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mPhoneNumber: String
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var countryCodePicker: CountryCodePicker

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
        countryCodePicker = binding.textViewCountryName
        countryCodePicker.defaultCountryCode
        binding.registerBtnGoogleContainer.setOnClickListener {
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
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
    }

    private fun signWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(MAIN_ACTIVITY, gso)
    }

    private fun sendCode() {
        if (binding.registerInputPhoneNumber.text.isNotEmpty()) {
            options()
        } else {
            showToast(getString(R.string.enter_code_toast_text))
        }
    }

    private fun options() {
        mPhoneNumber =
            "${countryCodePicker.textView_selectedCountry.text}${binding.registerInputPhoneNumber.text}"

        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(mPhoneNumber)                // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
            .setActivity(MAIN_ACTIVITY)                 // Activity (for callback binding)
            .setCallbacks(getCallbacks(mPhoneNumber) {
                showToast(MAIN_ACTIVITY.getString(R.string.welcome))
                restartActivity()
            })                    // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


}