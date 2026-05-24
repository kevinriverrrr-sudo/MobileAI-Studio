package com.mobileaistudio.data.local.db.entities

import androidx.room.*

@Entity(
    tableName = "chats",
    foreignKeys = [ForeignKey(
        entity = ModelEntity::class,
        parentColumns = ["id"],
        childColumns = ["modelId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("modelId")]
)
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String = "Новый чат",
    val modelId: String? = null,
    val modelName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val folderId: String? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false
)
