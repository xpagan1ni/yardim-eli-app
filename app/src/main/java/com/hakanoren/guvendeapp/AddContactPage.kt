package com.hakanoren.guvendeapp

import android.content.Context
import com.hakanoren.guvendeapp.models.Contact
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hakanoren.guvendeapp.databinding.AddContactActivityBinding

class AddContactPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_contact_activity)

        val nameEditText = findViewById<EditText>(R.id.contactNameText)
        val phoneEditText = findViewById<EditText>(R.id.contactPhoneNumberText)
        val addButton = findViewById<Button>(R.id.addContactButton)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phoneNumber = phoneEditText.text.toString()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                saveContact(name, phoneNumber)
                Toast.makeText(this, "Kişi eklendi", Toast.LENGTH_SHORT).show()
                finish() // Sayfayı kapat
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveContact(name: String, phoneNumber: String) {
        val sharedPreferences = getSharedPreferences("contacts", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val contact = "$name:$phoneNumber"
        editor.putString(contact, contact)
        editor.apply() // Değişiklikleri kaydet
    }
}
