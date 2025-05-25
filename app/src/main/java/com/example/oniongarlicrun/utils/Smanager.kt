package com.example.oniongarlicrun.utils
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import java.lang.ref.WeakReference

class Smanager private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    companion object {
        @Volatile
        private var instance: Smanager? = null

        fun init(context: Context): Smanager {
            return instance ?: synchronized(this) {
                instance ?: Smanager(context).also { instance = it }
            }
        }

        fun getInstance(): Smanager {
            return instance ?: throw IllegalStateException(
                "Smanager must be initialized by calling init(context) before use."
            )
        }
    }

    fun toast(text: String) {
        contextRef.get()?.let { context ->
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun vibrate(duration: Long = 500) {
        contextRef.get()?.let { context ->
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.vibrate(
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    }

}
