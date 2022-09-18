package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            showToast("calisiyor obe")
            val uid = AUTH.currentUser?.uid.toString()

            val datemap = hashMapOf<String, Any>()
            datemap[CHILD_PHONE] = phoneNumber
            datemap[CHILD_ID] = uid

            REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber)
                .setValue(datemap)
                .addOnSuccessListener {
                    binding.enterCodeContainer.visibility = View.GONE
                    binding.enterNameContainer.visibility = View.VISIBLE
                    inputName()
                }
                .addOnFailureListener {
                    showToast(it.toString())
                }
        }
    }

    private fun inputName() {
        binding.enterNameNextBtn.setOnClickListener {
            val name = binding.registerInputName.text.toString()
            if (name.isNotEmpty())
                REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber)
                    .child(CHILD_FULLNAME).setValue(name)
                    .addOnSuccessListener { restartActivity() }
                    .addOnFailureListener { showToast(it.toString()) }
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
}