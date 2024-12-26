package co.javos.watchflyphoneapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.cySdkyc.clx.Helper

class MainApplication : Application() {
    override fun attachBaseContext(paramContext: Context?) {
        super.attachBaseContext(paramContext)
        try {
            Helper.install(this)
        } catch (e: Exception) {
            Log.e("MainApplication", "attachBaseContext: ", e)
        }
    }
}
