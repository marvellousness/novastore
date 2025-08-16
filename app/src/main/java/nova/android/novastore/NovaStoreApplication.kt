package nova.android.novastore

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NovaStoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}