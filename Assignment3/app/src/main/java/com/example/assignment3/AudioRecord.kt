package com.example.assignment3

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class AudioRecord() {
    var context: Context? = null
    var sampleRate: Int = 44100
    var buffer: Int = 2

    private var audiorecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording: Boolean = false

    interface OnAudioFrameCaptured {
        fun onFrameCaptured(frame: ShortArray)
    }

    var listener: OnAudioFrameCaptured? = null

    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ) * buffer

    fun hasMicPremission(): Boolean {
        return context?.let {
            ActivityCompat.checkSelfPermission(it, Manifest.permission.RECORD_AUDIO)
        } == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (audiorecord != null && audiorecord?.state == AudioRecord.STATE_INITIALIZED) {
            return
        }

        if (!hasMicPremission()) {
            return
        }

        if (hasMicPremission()) {
            audiorecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        }

        audiorecord?.startRecording()
        isRecording = true

        recordingThread = thread(start = true) {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                val bytesRead = audiorecord?.read(buffer, 0, bufferSize) ?: 0
                if (bytesRead > 0) {
                    listener?.onFrameCaptured(buffer.copyOf(bytesRead))
                }
            }
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        isRecording = false
        recordingThread?.join()
        audiorecord?.stop()
        audiorecord?.release()
        audiorecord = null
        recordingThread = null
    }
}