//package com.ntg.core.database.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Upsert
//import com.ntg.core.database.model.TagEntity
//
//@Dao
//interface TagsDao {
//
//    @Insert
//    suspend fun insert(tagEntity: TagEntity)
//
//    @Upsert
//    suspend fun upsert(tagEntity: TagEntity)
//
//    @Delete
//    suspend fun delete(tagEntity: TagEntity)
//
//    @Upsert
//    suspend fun upsertAll(tags: List<TagEntity>)
//
//}