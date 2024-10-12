package com.hakanoren.guvendeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hakanoren.guvendeapp.models.Contact
import com.hakanoren.guvendeapp.databinding.ContactCardItemBinding

class ContactAdapter(
    private val contacts: MutableList<Contact>,
    private val onDeleteClick: (Int) -> Unit // Silme olayını yönetmek için
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(
        private val binding: ContactCardItemBinding,
        private val onDeleteClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact, position: Int) {
            binding.contactNameTextView.text = contact.name
            binding.contactPhoneTextView.text = contact.phoneNumber

            // Uzun tıklama dinleyicisi
            itemView.setOnLongClickListener {
                onDeleteClick(adapterPosition) // adapterPosition kullanarak doğru pozisyonu al
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position], position)
    }

    override fun getItemCount(): Int = contacts.size
}
