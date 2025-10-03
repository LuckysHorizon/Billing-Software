package com.grocerypos.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Simple sound utility to play a short beep sound without external files.
 */
public class SoundUtils {
    private static Clip cachedBeepClip;

    /**
     * Plays a short beep sound (non-blocking). Safe to call from EDT.
     */
    public static void playBeep() {
        try {
            if (cachedBeepClip == null) {
                cachedBeepClip = createBeepClip();
            }
            if (cachedBeepClip.isRunning()) {
                cachedBeepClip.stop();
            }
            cachedBeepClip.setFramePosition(0);
            cachedBeepClip.start();
        } catch (Exception ignored) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }

    private static Clip createBeepClip() throws Exception {
        // Generate a 440 Hz sine wave, 120 ms, 16-bit mono PCM
        float sampleRate = 44100f;
        int durationMs = 120;
        int numSamples = Math.round(sampleRate * durationMs / 1000f);
        byte[] pcm = new byte[numSamples * 2]; // 16-bit mono
        double twoPiF = 2 * Math.PI * 440.0;
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            short sample = (short) (Math.sin(twoPiF * t) * 32767);
            pcm[i * 2] = (byte) (sample & 0xFF);
            pcm[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        InputStream bais = new ByteArrayInputStream(pcm);
        AudioInputStream ais = new AudioInputStream(bais, format, numSamples);
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }
}


