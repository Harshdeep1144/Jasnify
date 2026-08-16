package com.harshdeep.jasnify.utils

import android.content.Context
import android.provider.ContactsContract
import com.harshdeep.jasnify.domain.model.Contact

object ContactHelper {
    fun fetchContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

            while (it.moveToNext()) {
                val name = if (nameIndex >= 0) it.getString(nameIndex) ?: "" else ""
                val number = if (numberIndex >= 0) it.getString(numberIndex) ?: "" else ""
                val id = if (idIndex >= 0) it.getString(idIndex) ?: "" else ""
                val photoUri = if (photoIndex >= 0) it.getString(photoIndex) else null

                // Basic deduplication based on number
                if (number.isNotBlank() && contacts.none { c -> c.phoneNumber == number }) {
                    contacts.add(Contact(id, name, number, photoUri))
                }
            }
        }
        return contacts
    }
}
