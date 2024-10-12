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
import com.hakanoren.guvendeapp.databinding.SigninScreenBinding

class SignInActivity: AppCompatActivity() {

    private lateinit var binding: SigninScreenBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SigninScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.logInButton.setOnClickListener{
            auth.signInWithEmailAndPassword(binding.loginEmailText.getText().toString().trim(), binding.passwordLoginText.getText().toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı olursa, arayüzü kullanıcı bilgisiyle güncelle.
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Giriş başarısız olursa, kullanıcıya gösterilecek mesaj.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Giriş yapma başarısız, lütfen bilgileri doğru giriniz.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

    }
    public override fun onStart() {
        super.onStart()
        // Kullanıcının oturum açıp açmadığını (boş olmayan) kontrol edin ve kullanıcı arayüzünü buna göre güncelleyin.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }

}