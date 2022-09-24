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
import com.example.imeiscanner.utilits.restartActivity
import com.example.imeiscanner.utilits.showToast
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment(val phoneNumber: String, val id: String) : Fragment() {
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
        val credential = PhoneAuthProvider.getCredential(id, code)

        AUTH.signInWithCredential(credential).addOnSuccessListener {
            val uid = AUTH.currentUser?.uid.toString()

            val dataMap = hashMapOf<String, Any>()
            dataMap[CHILD_PHONE] = phoneNumber
            dataMap[CHILD_ID] = uid
            dataMap[CHILD_TYPE] = PhoneAuthProvider.PROVIDER_ID

            REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(dataMap)
                .addOnFailureListener { showToast(it.message.toString()) }

            REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber)
                .setValue(dataMap)
                .addOnSuccessListener {
                    binding.enterCodeContainer.visibility = View.GONE
                    binding.enterNameContainer.visibility = View.VISIBLE
                    inputName(uid)
                }
                .addOnFailureListener {
                    showToast(it.toString())
                }
        }
    }

    private fun inputName(uid:String) {
        binding.enterNameNextBtn.setOnClickListener {
            val name = binding.registerInputName.text.toString()
            if (name.isNotEmpty()) {
//                updateUserName(name)


                USER.fullname = name

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .child(CHILD_FULLNAME)
                    .setValue(name).addOnFailureListener { showToast(it.message.toString()) }

                REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber)
                    .child(CHILD_FULLNAME).setValue(name)
                    .addOnSuccessListener {
                        showToast(getString(R.string.welcome))
                        restartActivity()
                    }
                    .addOnFailureListener { showToast(it.toString()) }
            } else showToast(getString(R.string.fullname_is_empty))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }
}