package com.ntg.mybudget.sync.work.workers.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.ConfigRepository
import com.ntg.core.data.repository.ContactRepository
import com.ntg.mybudget.sync.work.workers.initializers.SyncConstraints
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect

@HiltWorker
class ConfigWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val bankRepository: BankCardRepository,
    private val contactRepository: ContactRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        configRepository.updateConfigs()
        contactRepository.updateWithServer()
        val localBankCount = bankRepository.getLocalBankCount()
        if (localBankCount == 0){
            bankRepository.getUserLocalBanks().collect()
        }
        return Result.success()
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(ConfigWorker::class.delegatedData())
            .build()
    }

}