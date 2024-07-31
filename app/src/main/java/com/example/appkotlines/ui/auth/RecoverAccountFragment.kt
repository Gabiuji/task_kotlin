package com.example.appkotlines.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.appkotlines.R
import com.example.appkotlines.databinding.FragmentRecoverAccountBinding
import com.example.appkotlines.ui.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : Fragment() {
    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()
    }
    private fun initClicks(){
        binding.btnSend.setOnClickListener{validateData()}
    }
    private fun validateData(){
        val email = binding.edtEmailRecover.text.toString().trim()

        if(email.isNotEmpty()){
            binding.progressbar.isVisible = true
            recoverAccountUser(email)
        }else{
            Toast.makeText(requireContext(), "Informe seu email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recoverAccountUser(email: String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()){task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(), "Prontinho, acabamos de enviar o link para seu email", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(
                        requireContext(),
                        FirebaseHelper.validError(task.exception?.message?:""), Toast.LENGTH_SHORT).show()
                    binding.progressbar.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}