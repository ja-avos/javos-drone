package co.javos.watchflyphoneapp.ui.screens

import android.content.Context
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.ui.widgets.NoConnectionStatusWidget
import co.javos.watchflyphoneapp.viewmodels.LiveFeedViewModel
import dji.sdk.camera.VideoFeeder
import dji.sdk.codec.DJICodecManager

class LiveFeedView {

    @Composable
    fun LiveFeed(liveFeedViewModel: LiveFeedViewModel?, context: Context) {

        val droneStatus = liveFeedViewModel?.droneStatus?.collectAsState()?.value

        val aspectRatio = 16f / 9f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (droneStatus?.isDroneConnected() != true)
                NoConnectionStatusWidget().NoConnectionStatus(
                    droneStatus?.state ?: DroneState.NO_REMOTE
                )
            else
                AndroidView(
                    modifier = Modifier.fillMaxSize().aspectRatio(aspectRatio),
                    factory = {
                        ctx ->
                        val surfaceView = SurfaceView(ctx)

                        // Set the SurfaceHolder callback to ensure surface creation
                        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {
                                // Surface is created, initialize the codec manager with the new surface
                                holder.let {
                                    val codecManager = DJICodecManager(
                                        ctx,
                                        it,
                                        surfaceView.width,
                                        surfaceView.height
                                    )

                                    // Get the primary video feed from the drone and set the listener
                                    VideoFeeder.getInstance()?.primaryVideoFeed?.addVideoDataListener { videoBuffer, size ->
                                        codecManager.sendDataToDecoder(videoBuffer, size)
                                    }
                                }
                            }

                            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                                // Surface changed, reinitialize or handle resizing if necessary
                            }

                            override fun surfaceDestroyed(holder: SurfaceHolder) {
                                // Release resources or stop the video feed if the surface is destroyed
                                holder.let {
                                    VideoFeeder.getInstance()?.primaryVideoFeed?.destroy()
                                }
                            }
                        })

                        surfaceView
                    },
                    update = {
                        surfaceView ->

                    }
                )
        }
    }
}
