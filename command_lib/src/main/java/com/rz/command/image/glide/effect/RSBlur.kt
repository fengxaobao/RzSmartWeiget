package com.rz.command.image.glide.effect


import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.*

object RSBlur {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Throws(RSRuntimeException::class)
    fun blur(context: Context, blurredBitmap: Bitmap, radius: Int): Bitmap? {
        var blurredBitmap = blurredBitmap
        try {
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(
                rs, blurredBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val blur = ScriptIntrinsicBlur.create(
                rs,
                Element.U8_4(rs)
            )

            blur.setInput(input)
            blur.setRadius(radius.toFloat())
            blur.forEach(output)
            output.copyTo(blurredBitmap)
            rs.destroy()
        } catch (e: RSRuntimeException) {
            blurredBitmap = FastBlur.blur(blurredBitmap, radius, true)!!
        }

        return blurredBitmap
    }
}
