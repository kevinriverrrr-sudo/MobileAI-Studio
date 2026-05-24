package com.mobileaistudio.di

import com.mobileaistudio.data.repository.ChatRepositoryImpl
import com.mobileaistudio.data.repository.HardwareRepositoryImpl
import com.mobileaistudio.data.repository.ModelRepositoryImpl
import com.mobileaistudio.domain.repository.IChatRepository
import com.mobileaistudio.domain.repository.IHardwareRepository
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindModelRepository(impl: ModelRepositoryImpl): IModelRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): IChatRepository

    @Binds
    @Singleton
    abstract fun bindHardwareRepository(impl: HardwareRepositoryImpl): IHardwareRepository
}
