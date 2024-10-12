package com.hakanoren.guvendeapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        askNotificationPermission()
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                return@OnCompleteListener
            }



        })


        val button: Button = findViewById(R.id.signUpButton)
        val signInbutton: Button = findViewById(R.id.logInScreenButton)

        button.setOnClickListener {
            // Kayıt olunduğunda HomepPage'e yönlendiren intent.
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInbutton.setOnClickListener {
            // Giriş yapıldığında HomepPage'e yönlendiren intent.
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        onStart()
        onBackPressed()

    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val userId = currentUser?.uid
            val onlineRef = FirebaseDatabase.getInstance().getReference("users/$userId/online")
            onlineRef.setValue(true)
        } else {
            // Eğer kullanıcı zaten giriş yapmışsa, HomePage'e yönlendir.
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val currentUser = auth.currentUser
        if (currentUser == null) {

        } else {
            super.onBackPressed()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            //
        } else {
            //
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    companion object {
        private const val TAG = "MainActivity" // TAG sabitini tanımlayın
    }
}