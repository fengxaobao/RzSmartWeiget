import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi


/**
 * 作者：iss on 2020/6/9 17:56
 * 邮箱：55921173@qq.com
 * 类备注：
 */
class BatteryManager {
    companion object {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getBatteryCapacity(context: Context): Int {

            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val receiver: Intent = context.registerReceiver(null, filter)!!
            return receiver.getIntExtra("level", 0) //获取当前电量
//            val scale = receiver.getIntExtra("scale", 0) //获取总电量
//            val status = receiver.getIntExtra("status", 0) //获取充电状态
//            val voltage = receiver.getIntExtra("voltage", 0) //获取电压(mv)
//            val temperature = receiver.getIntExtra("temperature", 0) //获取温度(数值)
//            val t = temperature / 10.0 //运算转换,电池摄氏温度，默认获取的非摄氏温度值
//
//            Log.e("aaa level", "$level%")
//            Log.e("aaa scale", "" + scale)
//            Log.e("aaa status", "" + status)
//            Log.e("aaa voltage", "" + voltage)
//            Log.e("aaa temperature", "" + t)

        }
    }
}