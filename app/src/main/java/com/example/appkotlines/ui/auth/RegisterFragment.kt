package com.example.appkotlines.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.appkotlines.R
import com.example.appkotlines.databinding.FragmentRegisterBinding
import com.example.appkotlines.ui.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()
    }
    private fun initClicks(){
        binding.btnReg.setOnClickListener { validateData() }
    }

    private fun validateData(){
        val email = binding.ediEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if(email.isNotEmpty()){
            if(password.isNotEmpty()){
                binding.progressbar.isVisible = true
                registerUser(email, password)
            }else{
                Toast.makeText(requireContext(), "Informe sua senha", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Informe seu email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()){task ->
                if(task.isSuccessful){
                    findNavController().navigate(R.id.action_global_homeFragment)
                }else{
                    Toast.makeText(
                        requireContext(),
                        FirebaseHelper.validError(task.exception?.message?:""), Toast.LENGTH_SHORT).show()
                    binding.progressbar.isVisible = false
                }
            }
    }
}