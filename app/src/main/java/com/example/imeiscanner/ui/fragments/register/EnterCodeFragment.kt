package com.example.imeiscanner.ui.fragments.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.database.*
import com.example.imeiscanner.databinding.FragmentEnterCodeBinding
import com.example.imeiscanner.utilits.AppValueEventListener
import com.example.imeiscanner.utilits.MAIN_ACTIVITY
import com.example.imeiscanner.utilits.showToast
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterCodeFragment(private val phoneNumber: String, val id: String) : Fragment() {

    private lateinit var binding: FragmentEnterCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.inputCodeResendCodeBtn.setOnClickListener { resendCode() }

        MAIN_ACTIVITY.title = phoneNumber
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

    private fun resendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            MAIN_ACTIVITY,
            getCallbacks(phoneNumber)
        )
    }

    private fun checkCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)

        AUTH.signInWithCredential(credential).addOnSuccessListener {
            val uid = AUTH.currentUser?.uid.toString()
            signAndCheckUserHasExist(uid)
        }
    }

    private fun signAndCheckUserHasExist(uid: String) {
        REF_DATABASE_ROOT.child(NODE_USERS)
            .addListenerForSingleValueEvent(AppValueEventListener {
                if (it.hasChild(uid)) {
                    signInWithPhone(uid, phoneNumber)
                } else {
                    binding.enterCodeContainer.visibility = View.GONE
                    binding.enterNameContainer.visibility = View.VISIBLE
                    binding.enterNameNextBtn.setOnClickListener {
                        val name = binding.registerInputName.text.toString()
                        if (name.isNotEmpty()) {
                            signInWithPhone(uid, phoneNumber, name)
                        } else showToast(getString(R.string.fullname_is_empty))
                    }
                }
            })
    }
}