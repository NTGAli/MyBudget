package com.ntg.mybudget.sync.work.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.ContactRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.mybudget.common.logd
import com.ntg.mybudget.sync.work.workers.initializers.SyncConstraints
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val sourceExpenditureRepository: WalletsRepository,
    private val contactRepository: ContactRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        accountRepository.syncAccounts()
        sourceExpenditureRepository.syncSources()
        contactRepository.syncContacts()
        return Result.success()
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup and update user data
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }

}