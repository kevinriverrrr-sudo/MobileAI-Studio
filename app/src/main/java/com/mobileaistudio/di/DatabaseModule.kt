package com.mobileaistudio.di

import android.content.Context
import androidx.room.Room
import com.mobileaistudio.data.local.db.AppDatabase
import com.mobileaistudio.data.local.db.dao.ChatDao
import com.mobileaistudio.data.local.db.dao.ModelDao
import com.mobileaistudio.data.local.db.dao.PresetDao
import com.mobileaistudio.data.local.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "mobileai_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideModelDao(db: AppDatabase): ModelDao = db.modelDao()

    @Provides fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()

    @Provides fun providePresetDao(db: AppDatabase): PresetDao = db.presetDao()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext ctx: Context): UserPreferences =
        UserPreferences(ctx)
}
