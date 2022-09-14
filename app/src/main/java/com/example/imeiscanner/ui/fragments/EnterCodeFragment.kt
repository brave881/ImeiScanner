package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.imeiscanner.database.AUTH

import com.example.imeiscanner.databinding.FragmentEnterCodeBinding
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.showToast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class EnterCodeFragment(val phoneNumber: String,val id:String) : Fragment() {
    private lateinit var binding: FragmentEnterCodeBinding
    override fun onStart() {
        super.onStart()
        binding.registerInputCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val code = binding.registerInputCode.text.toString()
                if (code.length == 6) {
                    checkCode()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }


    private fun checkCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id,code)
        AUTH.signInWithCredential(credential).addOnSuccessListener {
           showToast("calisiyor obe")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener(MAIN_ACTIVITY) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    val user = task.result?.user
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                }
            }
    }

}