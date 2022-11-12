package co.javos.drone.utils
//
//import androidx.compose.runtime.Composable
//
//class VideoFeedView {
//
//
//
//    @Composable
//    fun getView() {
//
//    }
//}

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import dji.midware.usb.P3.UsbAccessoryService
import dji.sdk.camera.VideoFeeder.VideoDataListener
import dji.sdk.camera.VideoFeeder.VideoFeed
import dji.sdk.codec.DJICodecManager
import dji.thirdparty.rx.Observable
import dji.thirdparty.rx.Subscription
import dji.thirdparty.rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * VideoView will show the live video for the given video feed.
 */
class VideoFeedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    SurfaceView(context, attrs, defStyle) {
    private var codecManager: DJICodecManager? = null
    private var videoDataListener: VideoDataListener? = null
    private val videoWidth = 0
    private val videoHeight = 0
    private var isPrimaryVideoFeed = false
    private var coverView: View? = null
    private val WAIT_TIME: Long = 500 // Half of a second
    private val lastReceivedFrameTime = AtomicLong(0)
    private val timer: Observable<*> = Observable.timer(100, TimeUnit.MICROSECONDS).observeOn(
        AndroidSchedulers.mainThread()
    ).repeat()
    private var subscription: Subscription? = null
    private var surfaceHolder: SurfaceHolder? = null

    //endregion
    //region Life-Cycle
    init {
        init(context)
    }

    fun setCoverView(view: View?) {
        coverView = view
    }

    private fun init(context: Context) {
        // Avoid the rending exception in the Android Studio Preview view.
        if (isInEditMode) {
            return
        }
        surfaceHolder = holder
        surfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                if (codecManager == null) {
                    codecManager = DJICodecManager(
                        context,
                        surfaceHolder,
                        width,
                        height,
                        if (isPrimaryVideoFeed) UsbAccessoryService.VideoStreamSource.Camera else UsbAccessoryService.VideoStreamSource.Fpv
                    )
                }
            }

            override fun surfaceChanged(
                surfaceHolder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                if (codecManager != null) {
                    codecManager!!.cleanSurface()
                    codecManager!!.destroyCodec()
                    codecManager = null
                }
            }
        })
        videoDataListener = VideoDataListener { videoBuffer, size ->
            lastReceivedFrameTime.set(System.currentTimeMillis())
            if (codecManager != null) {
                codecManager!!.sendDataToDecoder(
                    videoBuffer,
                    size,
                    if (isPrimaryVideoFeed) UsbAccessoryService.VideoStreamSource.Camera.index else UsbAccessoryService.VideoStreamSource.Fpv.index
                )
            }
        }
        subscription = timer.subscribe {
            val now = System.currentTimeMillis()
            val ellapsedTime = now - lastReceivedFrameTime.get()
            if (coverView != null) {
                if (ellapsedTime > WAIT_TIME && !ModuleVerificationUtil.isMavic2Product) {
                    if (coverView!!.visibility == INVISIBLE) {
                        coverView!!.visibility = VISIBLE
                    }
                } else {
                    if (coverView!!.visibility == VISIBLE) {
                        coverView!!.visibility = INVISIBLE
                    }
                }
            }
        }
    }

    fun registerLiveVideo(videoFeed: VideoFeed?, isPrimary: Boolean): VideoDataListener? {
        isPrimaryVideoFeed = isPrimary
        if (videoDataListener != null && videoFeed != null && !videoFeed.listeners.contains(
                videoDataListener
            )
        ) {
            videoFeed.addVideoDataListener(videoDataListener!!)
            return videoDataListener
        }
        return null
    }

    fun changeSourceResetKeyFrame() {
        if (codecManager != null) {
            codecManager!!.resetKeyFrame()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }

    companion object {
        //region Properties
        private const val TAG = "VIDEO_FEED"
    }
}