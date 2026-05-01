package com.example.dpadplayer

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DPadApplication : Application() {
    // Application-level coroutine scope for background work that should survive
    // UI/ViewModel lifecycles (cancellation only when the process is terminated).
    val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onCreate() {
        super.onCreate()
        // Use the application scope for MediaStoreScanner background writes so
        // cached album data persists independently of UI lifecycles.
        MediaStoreScanner.scope = applicationScope
    }
}
