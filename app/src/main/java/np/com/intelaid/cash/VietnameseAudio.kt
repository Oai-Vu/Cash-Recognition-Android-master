package np.com.intelaid.cash
import android.app.PendingIntent.getActivity
import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast

class VietnameseAudio {
    private lateinit var mp: MediaPlayer
    private var context: Context

    constructor(context: Context) {
       this.context = context
    }

    fun initialMessageAudio(): MediaPlayer {
        return MediaPlayer.create (context, R.raw.initial_message)
    }

    fun cashAudio(amount: String): MediaPlayer{
        return when(amount) {
            "one" -> MediaPlayer.create(context, R.raw.one)
            "two" -> MediaPlayer.create(context, R.raw.two)
            "five" -> MediaPlayer.create(context, R.raw.five)
            "ten" -> MediaPlayer.create(context, R.raw.ten)
            "twenty" -> MediaPlayer.create(context, R.raw.twenty)
            "fifty" -> MediaPlayer.create(context, R.raw.fifty)
            "onehundred" -> MediaPlayer.create(context, R.raw.one_hundred)
            "twohundred" -> MediaPlayer.create(context, R.raw.two_hundred)
            "fivehundred" -> MediaPlayer.create(context, R.raw.five_hundred)
//            "one" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "two" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "five" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "ten" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "twenty" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "fifty" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "onehundred" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "twohundred" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
//            "fivehundred" -> Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
            else -> {
                 MediaPlayer.create(context, R.raw.error)
//                Toast.makeText(context, "Xin lỗi vui lòng thử lại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}