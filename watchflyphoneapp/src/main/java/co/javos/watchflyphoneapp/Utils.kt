package co.javos.watchflyphoneapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class Utils {
    companion object {
        fun bitmapDescriptorFromVector(
            context: Context,
            vectorResId: Int,
            size: Int = 120,
            color: Color = Color.Black
        ): BitmapDescriptor {
            val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable?.setBounds(0, 0, size, size)
            vectorDrawable?.alpha = 255
            val bitmap: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable!!.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}
