import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.rz.utils.sp.GlobalPreference
import java.io.IOException

/**
 * 作者：iss on 2020/6/6 19:31
 * 邮箱：55921173@qq.com
 * 类备注：
 */
object RxSoundUtils {
    fun playSound(context: Context, sound: Int) {
        val voice = GlobalPreference.get(GlobalPreference.KEY.GLOBAL_VOICE_MODE, true)
        if (!voice) {
            Log.d("RxSoundUtils", "playSound:voice=$voice ")
            return
        }

        try {
            var uri: String? = null
            uri = "android.resource://" + context.packageName + "/" + sound

            if (!TextUtils.isEmpty(uri)) {
                val r = RingtoneManager.getRingtone(context, Uri.parse(uri))
                r.play()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}