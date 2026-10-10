
package com.calculator;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class SpeechService {

    public void speak(String text) {

        Thread speechThread = new Thread(() -> {

            System.setProperty(
                    "freetts.voices",
                    "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"
            );

            Voice voice = VoiceManager.getInstance()
                    .getVoice("kevin16");

            if (voice == null) {
                System.err.println("Kevin voice not found.");
                return;
            }

            try {
                voice.allocate();
                voice.speak(text);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                voice.deallocate();
            }
        });

        speechThread.setDaemon(true);
        speechThread.start();
    }
}
