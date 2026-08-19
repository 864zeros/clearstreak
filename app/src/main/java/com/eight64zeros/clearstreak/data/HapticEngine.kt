package com.eight64zeros.clearstreak.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Central haptic vocabulary for the Somatosensory Reset tools (blueprint §2).
 *
 * Android exposes amplitude (0–255) + timing, not literal frequency, and cheap
 * rotational motors approximate rather than reproduce these patterns — so the
 * intent (rising, settling, ticking) is encoded via amplitude waveforms with an
 * on/off fallback on devices without amplitude control.
 */
object HapticEngine {

    private fun vibrator(context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

    private fun play(context: Context, timings: LongArray, amplitudes: IntArray) {
        val v = vibrator(context)
        if (!v.hasVibrator()) return
        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    fun cancel(context: Context) = vibrator(context).cancel()

    /** Two crisp pulses — the 60-second micro-time-chunking tick. */
    fun dualPulse(context: Context) =
        play(context, longArrayOf(0, 55, 90, 55), intArrayOf(0, 210, 0, 210))

    /** Distinct triple accent at the halfway point. */
    fun midpointAccent(context: Context) =
        play(context, longArrayOf(0, 40, 60, 40, 60, 150), intArrayOf(0, 160, 0, 160, 0, 255))

    /** Rising-then-settling wave on completion. */
    fun resolvingWave(context: Context) =
        play(context, longArrayOf(0, 140, 140, 140, 220), intArrayOf(0, 80, 150, 230, 70))

    // --- 4x4 box breather phase cues (each ~4s) ---

    /** Inhale: intensity ramps up. */
    fun inhale(context: Context) =
        play(
            context,
            longArrayOf(0, 500, 500, 500, 500, 500, 500, 500),
            intArrayOf(0, 40, 80, 120, 160, 200, 230, 255)
        )

    /** Hold (full): subtle one-second micro-ticks. */
    fun holdTicks(context: Context) =
        play(
            context,
            longArrayOf(0, 25, 975, 25, 975, 25, 975, 25),
            intArrayOf(0, 90, 0, 90, 0, 90, 0, 90)
        )

    /** Exhale: intensity decays. */
    fun exhale(context: Context) =
        play(
            context,
            longArrayOf(0, 500, 500, 500, 500, 500, 500, 500),
            intArrayOf(0, 255, 220, 180, 140, 100, 60, 30)
        )

    /** Hold (empty): a single soft resting pulse. */
    fun restPulse(context: Context) =
        play(context, longArrayOf(0, 120), intArrayOf(0, 60))
}
