package np.com.intelaid.cash

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.camerakit.CameraKit
import com.camerakit.CameraKitView
import java.util.concurrent.Executors





class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val mInputSize = 224
    private val mModelPath = "cash.tflite"
    private val mLabelPath = "label.txt"
    private lateinit var classifier: Classifier
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var cameraView: CameraKitView
    private lateinit var resultOutput: TextView
    private lateinit var captureButton: Button
    private lateinit var tts: TextToSpeech
    private lateinit var builder: AlertDialog.Builder
    // private lateinit var dialogView: View
    private lateinit var dialogMessage: TextView
    private lateinit var dialog: AlertDialog
    private lateinit var vietnameseAudio: VietnameseAudio
    private lateinit var audioPlayer: MediaPlayer
    private  var textToSpeechStatus: Int = 0
    private  var result: String = ""

    override fun onInit(status: Int) {
        textToSpeechStatus = status
//        Toast.makeText(this, "Vui lòng bấm nút chụp ảnh", Toast.LENGTH_SHORT).show()
         playInitialMessage(textToSpeechStatus)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTensorFlowAndLoadModel()
        initView()

        vietnameseAudio = VietnameseAudio(this)

        //Text to Speech
        tts = TextToSpeech(this, this)
        audioPlayer = vietnameseAudio.initialMessageAudio()


        captureButton.setOnClickListener {
            // dialog.show()
            cameraView.captureImage { _, photo ->
                Thread(Runnable {
                    onCaptureImage(photo)
                    resultOutput.post { resultOutput.text = result }
                }).start()
            }
        }

        cameraView.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                // dialog.show()
                cameraView.captureImage { _, photo ->
                    Thread(Runnable {
                        onCaptureImage(photo)
                        resultOutput.post { resultOutput.text = result }
                    }).start()
                }
            }
            true
        }
    }

    private fun initView() {
        cameraView = findViewById(R.id.cameraView)
        resultOutput = findViewById(R.id.resultOutput)
        captureButton = findViewById(R.id.captureButton)
    }

    private fun playInitialMessage(status: Int) {
        tts.stop() // stop tts if it's playing
        //Vietnamese audio
        audioPlayer = vietnameseAudio.initialMessageAudio()
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 7s
            audioPlayer.start()
        }, 4500)
    }

    private fun vibrate() {
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(500)
    }

    private fun onCaptureImage(photo: ByteArray) {
        cameraView.flash = CameraKit.FLASH_ON
        val bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.size)
        val results = classifier.recognizeImage(bitmap)
        val beepAudioPlayer = MediaPlayer.create(this, R.raw.beep)
        beepAudioPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 2s
            playCashAudio(results)
        }, 1000)
        // dialog.dismiss()

        // runOnUiThread { Toast.makeText(this, results[0].toString(), Toast.LENGTH_SHORT).show() }
    }

    private fun playCashAudio(results: List<Classifier.Recognition>) {
        tts.stop()
        result = if (results.isNotEmpty()) {
            results[0].toString()
        } else {
            ""
        }

        val audioResult = if (results.isNotEmpty())
        {
            results[0].title
        } else {
            ""
        }

        // dialog.dismiss()

        vibrate() // vibrate to give user feedback
        audioPlayer = vietnameseAudio.cashAudio(audioResult)
        vietnameseAudio.cashAudio(audioResult)
        audioPlayer.start()
    }

    override fun onResume() {
        super.onResume()
        cameraView.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraView.onPause()
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
        executor.execute { classifier.close() }
        audioPlayer.release()
    }

    private fun initTensorFlowAndLoadModel() {
        executor.execute {
            try {
                classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }
    }
}
