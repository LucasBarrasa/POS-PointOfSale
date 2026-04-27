package com.barradev.chester.core.data.local.converters

import androidx.room.TypeConverter
import com.barradev.chester.core.model.models.SyncStatus


class SyncStatusConverter {

    // Escritura, Kotlin -> Room
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String{
        return status.name
    }

    // Lectura, Room -> Kotlin
    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus{
        return try {
            enumValueOf<SyncStatus>(value)
        } catch (e: Exception){
            SyncStatus.DRAFT
        }
    }
}