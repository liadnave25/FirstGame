package com.example.oniongarlicrun.utils
import android.content.Context
import android.media.MediaPlayer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Sounds(context: Context) {
    private val context: Context = context.applicationContext
    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun playSound(resId: Int) {
        executor.execute {
            val mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer.isLooping = false
            mediaPlayer.setVolume(1.0f, 1.0f)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mp ->
                mp?.stop()
                mp?.release()
            }
        }
    }
}
