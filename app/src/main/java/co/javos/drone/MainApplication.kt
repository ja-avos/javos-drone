package co.javos.drone

import android.app.Application
import android.content.Context
import com.secneo.sdk.Helper
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.products.HandHeld
import dji.sdk.sdkmanager.DJISDKManager

class MainApplication : Application() {

    companion object {
        @Synchronized
        fun getProductInstance(): BaseProduct? {
            return DJISDKManager.getInstance().product
        }

        fun isAircraftConnected(): Boolean {
            return getProductInstance() != null && getProductInstance() is Aircraft
        }

        fun isHandHeldConnected(): Boolean {
            return getProductInstance() != null && getProductInstance() is HandHeld
        }

        @Synchronized
        fun getAircraftInstance(): Aircraft? {
            return if (!isAircraftConnected()) {
                null
            } else  getProductInstance() as Aircraft
        }
    }


    override fun attachBaseContext(paramContext: Context?) {
        super.attachBaseContext(paramContext)
        Helper.install(this@MainApplication)
    }
}