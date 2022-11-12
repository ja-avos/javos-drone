@file:Suppress("MemberVisibilityCanBePrivate")

package co.javos.drone.utils

import co.javos.drone.MainApplication
import dji.common.product.Model
import dji.sdk.accessory.AccessoryAggregation
import dji.sdk.accessory.beacon.Beacon
import dji.sdk.accessory.speaker.Speaker
import dji.sdk.accessory.spotlight.Spotlight
import dji.sdk.base.BaseProduct
import dji.sdk.flightcontroller.FlightController
import dji.sdk.flightcontroller.Simulator
import dji.sdk.products.Aircraft
import dji.sdk.products.HandHeld


/**
 * Created by dji on 16/1/6.
 */
object ModuleVerificationUtil {
    val isProductModuleAvailable: Boolean
        get() = (null != MainApplication.getProductInstance())
    val isAircraft: Boolean
        get() = MainApplication.getProductInstance() is Aircraft
    val isHandHeld: Boolean
        get() = MainApplication.getProductInstance() is HandHeld
    val isCameraModuleAvailable: Boolean
        get() = isProductModuleAvailable && (null != MainApplication.getProductInstance()
            ?.camera)
    val isPlaybackAvailable: Boolean
        get() = isCameraModuleAvailable && (null != MainApplication.getProductInstance()
            ?.camera
            ?.playbackManager)
    val isMediaManagerAvailable: Boolean
        get() = isCameraModuleAvailable && (null != MainApplication.getProductInstance()
            ?.camera
            ?.mediaManager)
    val isRemoteControllerAvailable: Boolean
        get() {
            return isProductModuleAvailable && isAircraft && (null != MainApplication.getAircraftInstance()
                ?.remoteController)
        }
    val isFlightControllerAvailable: Boolean
        get() {
            return isProductModuleAvailable && isAircraft && (null != MainApplication.getAircraftInstance()
                ?.flightController)
        }
    val isCompassAvailable: Boolean
        get() {
            return isFlightControllerAvailable && isAircraft && (null != MainApplication.getAircraftInstance()
                ?.flightController
                ?.compass)
        }
    val isFlightLimitationAvailable: Boolean
        get() {
            return isFlightControllerAvailable && isAircraft
        }
    val isGimbalModuleAvailable: Boolean
        get() {
            return isProductModuleAvailable && (null != MainApplication.getProductInstance()
                ?.gimbal)
        }
    val isAirlinkAvailable: Boolean
        get() {
            return isProductModuleAvailable && (null != MainApplication.getProductInstance()
                ?.airLink)
        }
    val isWiFiLinkAvailable: Boolean
        get() {
            return isAirlinkAvailable && (null != MainApplication.getProductInstance()
                ?.airLink?.wiFiLink)
        }
    val isLightbridgeLinkAvailable: Boolean
        get() {
            return isAirlinkAvailable && (null != MainApplication.getProductInstance()
                ?.airLink
                ?.lightbridgeLink)
        }
    val isOcuSyncLinkAvailable: Boolean
        get() {
            return isAirlinkAvailable && (null != MainApplication.getProductInstance()
                ?.airLink
                ?.ocuSyncLink)
        }
    val isPayloadAvailable: Boolean
        get() {
            return isProductModuleAvailable && isAircraft && (null != MainApplication.getAircraftInstance()
                ?.payload)
        }
    val isRTKAvailable: Boolean
        get() {
            return isProductModuleAvailable && isAircraft && (null != MainApplication.getAircraftInstance()
                ?.flightController?.rtk)
        }
    val accessoryAggregation: AccessoryAggregation?
        get() {
            val aircraft: Aircraft? = MainApplication.getProductInstance() as Aircraft
            if (aircraft != null && null != aircraft.accessoryAggregation) {
                return aircraft.accessoryAggregation
            }
            return null
        }
    val speaker: Speaker?
        get() {
            val aircraft: Aircraft? = MainApplication.getProductInstance() as Aircraft
            if ((aircraft != null) && (null != aircraft.accessoryAggregation) && (null != aircraft.accessoryAggregation!!
                    .speaker)
            ) {
                return aircraft.accessoryAggregation!!.speaker
            }
            return null
        }
    val beacon: Beacon?
        get() {
            val aircraft: Aircraft? = MainApplication.getProductInstance() as Aircraft
            if ((aircraft != null) && (null != aircraft.accessoryAggregation) && (null != aircraft.accessoryAggregation!!
                    .beacon)
            ) {
                return aircraft.accessoryAggregation!!.beacon
            }
            return null
        }
    val spotlight: Spotlight?
        get() {
            val aircraft: Aircraft? = MainApplication.getProductInstance() as Aircraft
            if ((aircraft != null) && (null != aircraft.accessoryAggregation) && (null != aircraft.accessoryAggregation!!
                    .spotlight)
            ) {
                return aircraft.accessoryAggregation!!.spotlight
            }
            return null
        }
    val simulator: Simulator?
        get() {
            val aircraft: Aircraft? = MainApplication.getAircraftInstance()
            if (aircraft != null) {
                val flightController: FlightController? = aircraft.flightController
                if (flightController != null) {
                    return flightController.simulator
                }
            }
            return null
        }
    val flightController: FlightController?
        get() {
            val aircraft: Aircraft? = MainApplication.getAircraftInstance()
            if (aircraft != null) {
                return aircraft.flightController
            }
            return null
        }
    val isMavic2Product: Boolean
        get() {
            val baseProduct: BaseProduct? = MainApplication.getProductInstance()
            if (baseProduct != null) {
                return baseProduct.model == Model.MAVIC_2_PRO || baseProduct.model == Model.MAVIC_2_ZOOM
            }
            return false
        }
    val isMatrice300RTK: Boolean
        get() {
            val baseProduct: BaseProduct? = MainApplication.getProductInstance()
            if (baseProduct != null) {
                return baseProduct.model == Model.MATRICE_300_RTK
            }
            return false
        }
    val isMavicAir2: Boolean
        get() {
            val baseProduct: BaseProduct? = MainApplication.getProductInstance()
            if (baseProduct != null) {
                return baseProduct.model == Model.MAVIC_AIR_2
            }
            return false
        }

    val isAir2S: Boolean
        get() {
            val baseProduct: BaseProduct? = MainApplication.getProductInstance()
            if (baseProduct != null) {
                return baseProduct.model == Model.DJI_AIR_2S
            }
            return false
        }

    val isMavicPro: Boolean
        get() {
            val baseProduct: BaseProduct? = MainApplication.getProductInstance()
            if (baseProduct != null) {
                return baseProduct.model == Model.MAVIC_PRO
            }
            return false
        }
}