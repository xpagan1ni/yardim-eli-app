package com.hakanoren.guvendeapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.hakanoren.guvendeapp.databinding.SignupScreenBinding

class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: SignupScreenBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signUpButton.setOnClickListener{
            auth.createUserWithEmailAndPassword(binding.emailText.getText().toString().trim(),
                binding.passwordText.getText().toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                    } else {

                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Giriş yapma başarısız.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }

}