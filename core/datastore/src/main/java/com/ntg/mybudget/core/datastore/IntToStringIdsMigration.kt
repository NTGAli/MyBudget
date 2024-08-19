//package com.ntg.mybudget.core.datastore
//
//import androidx.datastore.core.DataMigration
//import com.nt.com.core.datastore.UserPreferences
//import com.nt.com.core.datastore.copy
//
///**
// * Migrates saved ids from [Int] to [String] types
// */
//internal object IntToStringIdsMigration : DataMigration<UserPreferences> {
//
//    override suspend fun cleanUp() = Unit
//
//    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
//        currentData.copy {
//            // Migrate topic ids
//
//            deprecatedFollowedTopicIds.clear()
//            deprecatedFollowedTopicIds.addAll(
//                currentData.deprecatedIntFollowedTopicIdsList.map(Int::toString),
//            )
//            deprecatedIntFollowedTopicIds.clear()
//
//            // Migrate author ids
//            deprecatedFollowedAuthorIds.clear()
//            deprecatedFollowedAuthorIds.addAll(
//                currentData.deprecatedIntFollowedAuthorIdsList.map(Int::toString),
//            )
//            deprecatedIntFollowedAuthorIds.clear()
//
//            // Mark migration as complete
//            hasDoneIntToStringIdMigration = true
//        }
//
//    override suspend fun shouldMigrate(currentData: UserPreferencesOuterClass.UserPreferences): Boolean =
//        !currentData.hasDoneIntToStringIdMigration
//}
