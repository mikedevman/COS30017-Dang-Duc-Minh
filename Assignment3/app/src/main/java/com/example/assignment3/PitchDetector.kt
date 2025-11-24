package com.example.assignment3

class PitchDetector() {
    private val minLag = 25

    // pcm = Pulse Code Modulation -> short array of audio data
    fun detectFrequency(pcm: ShortArray, sampleRate: Int): Double {
        val frequencySize = pcm.size
        val halfFrequencySize = frequencySize / 2 // Nyquist theorem

        var bestLag = 0
        var correlationOfBestLag = Double.NEGATIVE_INFINITY // ensure calculated sum will be higher than this

        for (lag in minLag until halfFrequencySize) {
            var sum = 0.0

            for (i in 0 until halfFrequencySize) {
                val x = pcm[i].toDouble() // get value at current position
                val y = pcm[i + lag].toDouble() // get value at shifted position
                sum += x * y
            }

            if (sum > correlationOfBestLag) {
                bestLag = lag
                correlationOfBestLag = sum
            }
        }
        if (bestLag == 0) return -1.0 // advoid division by 0
        return sampleRate.toDouble() / bestLag // f = sample rate / best lag value
    }
}

