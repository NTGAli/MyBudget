package com.ntg.mybudget.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.nt.com.core.datastore.UserPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferencesKt] proto.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = TODO("Not yet implemented")

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            // readFrom is already called on the data store background thread
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
