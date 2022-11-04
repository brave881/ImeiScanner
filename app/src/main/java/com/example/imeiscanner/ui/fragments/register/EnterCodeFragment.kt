package com.example.imeiscanner.ui.fragments.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.imeiscanner.R
import com.example.imeiscanner.database.AUTH
import com.example.imeiscanner.database.NODE_USERS
import com.example.imeiscanner.database.REF_DATABASE_ROOT
import com.example.imeiscanner.database.signInWithPhone
import com.example.imeiscanner.databinding.FragmentEnterCodeBinding
import com.example.imeiscanner.utilits.*
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment(
    private val phoneNumber: String,
    val id: String,
    private val token: PhoneAuthProvider.ForceResendingToken,
) : Fragment() {

    private lateinit var binding: FragmentEnterCodeBinding
    private lateinit var tvTimer: TextView

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
        initFields()
        startTimer(tvTimer).start()

        binding.resendCodeBtn.setOnClickListener {
            if (tvTimer.text.toString().isEmpty()) {
                startTimer(tvTimer).start()
                resendCode(phoneNumber, token)
            }
        }
        binding.registerInputCode.setOnCompleteListener {
            if (it.length == 6) {
                checkCode()
            }
        }
    }

    private fun initFields() {
        binding.enterCodeToolbar.setNavigationOnClickListener { MAIN_ACTIVITY.supportFragmentManager.popBackStack() }
        tvTimer = binding.textViewCountdownTime
        MAIN_ACTIVITY.title = phoneNumber
        binding.tvPhoneNumber.text = "at $phoneNumber"
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)

        AUTH.signInWithCredential(credential).addOnSuccessListener {
            val uid = AUTH.currentUser?.uid.toString()
            signAndCheckUserHasExist(uid)
        }.addOnFailureListener {
           binding.registerInputCode.triggerErrorAnimation()
        }
    }

    private fun signAndCheckUserHasExist(uid: String) {
        REF_DATABASE_ROOT.child(NODE_USERS).addListenerForSingleValueEvent(AppValueEventListener {
                if (it.hasChild(uid)) {
                    signInWithPhone(uid, phoneNumber)
                } else {
                    binding.enterCodeContainer.visibility = View.GONE
                    binding.enterNameContainer.visibility = View.VISIBLE
                    binding.enterNameNextBtn.setOnClickListener {
                        val name = binding.enterCodeName.text.toString()
                        val surname = binding.enterCodeName.text.toString()
                        if (name.isNotEmpty()) {
                            signInWithPhone(uid, phoneNumber, "$name $surname")
                        } else showToast(getString(R.string.fullname_is_empty))
                    }
                }
            })
    }
}
