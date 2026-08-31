package com.harshdeep.jasnify.domain.model

import com.google.firebase.firestore.PropertyName

data class Moment(
    val id: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val isVideo: Boolean = false,
    val folderId: String = "",
    val uploaderId: String = ""
)

data class MomentFolder(
    val id: String = "",
    val name: String = "",
    val coverImageUrl: String = "",
    val itemCount: Int = 0,
    @get:PropertyName("isNew")
    @set:PropertyName("isNew")
    var isNew: Boolean = false,
    val uploaderId: String = "",
    val parentId: String = "",
    val fullPath: String = "", // Used for Cloudinary and Breadcrumbs
    val createdAt: Long = System.currentTimeMillis()
)
