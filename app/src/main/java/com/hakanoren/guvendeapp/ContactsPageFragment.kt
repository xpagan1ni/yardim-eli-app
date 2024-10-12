package com.hakanoren.guvendeapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.hakanoren.guvendeapp.databinding.FragmentContactsPageBinding
import com.hakanoren.guvendeapp.models.Contact
import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class ContactsPageFragment : Fragment() {
    private var _binding: FragmentContactsPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private val contactList = mutableListOf<Contact>()

    private lateinit var viewModel: ContactViewModel

    private lateinit var auth: FirebaseAuth
    private val ARG_PARAM1 = "home_param1"
    private val ARG_PARAM2 = "home_param2"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ContactViewModel::class.java)

        // RecyclerView ayarları
        recyclerView = binding.recyclerView
        adapter = ContactAdapter(contactList) { position ->
            // Kişiyi silme işlemi
            contactList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveContacts() // Güncellenmiş kişileri kaydet
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        binding.addContact.setOnClickListener {
            // AddContactPage'e yönlendirme
            val intent = Intent(requireContext(), AddContactPage::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT)
        }
        loadContacts() // Kişileri yükle
    }


    private fun saveContacts() {
        val sharedPreferences = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Tüm verileri temizle
        contactList.forEach { contact ->
            editor.putString("${contact.name}:${contact.phoneNumber}", "")
        }
        editor.apply()
    }

    private fun loadContacts() {
        val sharedPreferences = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)
        val allContacts = sharedPreferences.all

        for (entry in allContacts) {
            val parts = entry.key.split(":")
            if (parts.size == 2) {
                val contact = Contact(parts[0], parts[1])
                contactList.add(contact)
            }
        }
        adapter.notifyDataSetChanged()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CONTACT && resultCode == RESULT_OK) {
            val newContact = data?.getParcelableExtra<Contact>("new_contact")
            newContact?.let {
                // 20 kişilik sınır kontrolü
                if (contactList.size < 20) {
                    contactList.add(it)
                    adapter.notifyItemInserted(contactList.size - 1)
                } else {
                    // Kullanıcıya bir hata mesajı göster
                    Toast.makeText(context, "En fazla 20 kişi ekleyebilirsiniz.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CODE_ADD_CONTACT = 1
    }
}
