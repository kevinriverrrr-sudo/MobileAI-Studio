package com.mobileaistudio.data.local.db.dao

import androidx.room.*
import com.mobileaistudio.data.local.db.entities.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM models ORDER BY lastUsedAt DESC")
    fun getAllModels(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE isLoaded = 1 LIMIT 1")
    fun getLoadedModel(): Flow<ModelEntity?>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun getModelById(id: String): ModelEntity?

    @Query("SELECT * FROM models WHERE repoId = :repoId")
    suspend fun getByRepoId(repoId: String): List<ModelEntity>

    @Query("SELECT * FROM models WHERE isFavorite = 1 ORDER BY lastUsedAt DESC")
    fun getFavorites(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE isLoaded = 1")
    suspend fun getLoadedModelOnce(): ModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: ModelEntity)

    @Update
    suspend fun update(model: ModelEntity)

    @Delete
    suspend fun delete(model: ModelEntity)

    @Query("UPDATE models SET isLoaded = 0 WHERE isLoaded = 1")
    suspend fun unloadAll()

    @Query("UPDATE models SET isLoaded = :loaded WHERE id = :id")
    suspend fun setLoaded(id: String, loaded: Boolean)

    @Query("UPDATE models SET lastUsedAt = :time WHERE id = :id")
    suspend fun updateLastUsed(id: String, time: Long)

    @Query("UPDATE models SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: String, fav: Boolean)

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteById(id: String)
}
