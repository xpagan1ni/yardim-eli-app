package com.hakanoren.guvendeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.hakanoren.guvendeapp.databinding.FragmentSettingsPageBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingsPageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSettingsPageBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsPageBinding.inflate(inflater, container, false)
        setupEmailButton()
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Kullanıcı giriş yaptıysa, e-mail adresini göster.
        currentUser?.let {
            val email = it.email
            binding.emailTextView.text = email
        }

        auth = Firebase.auth
        binding.signOutButton.setOnClickListener {
            signOutUser()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Mevcut aktiviteyi temizle
            startActivity(intent)
        }

        binding.appInfoPageButton.setOnClickListener {
            val intent = Intent(requireContext(), AppInfoPage::class.java)
            startActivity(intent)
        }


    }

    private fun setupEmailButton() {
        binding.emailSendButton.setOnClickListener {
            openEmailApp()
        }
    }

    private fun openEmailApp() {
        val emailAddress = "yardimeliuygulama@gmail.com"
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        }

        if (emailIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "E-posta göndermek için bir uygulama seçin:"))
        } else {
            Toast.makeText(requireContext(), "E-posta uygulaması bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    fun signOutUser() {
        auth.signOut()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}