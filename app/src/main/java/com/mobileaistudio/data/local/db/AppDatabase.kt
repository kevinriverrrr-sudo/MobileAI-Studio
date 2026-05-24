package com.mobileaistudio.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobileaistudio.data.local.db.dao.ChatDao
import com.mobileaistudio.data.local.db.dao.ModelDao
import com.mobileaistudio.data.local.db.dao.PresetDao
import com.mobileaistudio.data.local.db.entities.*

@Database(
    entities = [ModelEntity::class, ChatEntity::class, MessageEntity::class, PresetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao
    abstract fun chatDao(): ChatDao
    abstract fun presetDao(): PresetDao
}
