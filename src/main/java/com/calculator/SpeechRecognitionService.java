
package com.calculator;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeechRecognitionService {

    private static final String MODEL_PATH = "vosk-model";
    private static final float SAMPLE_RATE = 16000.0f;

    public String listenOnce() throws Exception {

        Model model = null;
        Recognizer recognizer = null;
        TargetDataLine microphone = null;

        try {
            // Load the offline speech recognition model
            model = new Model(MODEL_PATH);

            // Create the speech recogniser
            recognizer = new Recognizer(model, SAMPLE_RATE);

            // Configure the microphone for audio input
            AudioFormat format = new AudioFormat(
                    SAMPLE_RATE,
                    16,
                    1,
                    true,
                    false
            );

            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
            microphone.start();

            System.out.println("Listening... Speak your calculation.");

            byte[] buffer = new byte[4096];

            while (true) {

                // Read audio from the microphone
                int bytesRead = microphone.read(
                        buffer, 0, buffer.length
                );

                // Check whether Vosk has recognised a phrase
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {

                    String result = recognizer.getResult();
                    String recognisedText = extractText(result);

                    if (!recognisedText.isBlank()) {
                        System.out.println(
                                "Recognised: " + recognisedText
                        );

                        return recognisedText;
                    }
                }
            }

        } finally {
            // Release the microphone and recognition resources
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }

            if (recognizer != null) {
                recognizer.close();
            }

            if (model != null) {
                model.close();
            }
        }
    }

    private String extractText(String result) {

        Pattern pattern = Pattern.compile(
                "\"text\"\\s*:\\s*\"([^\"]*)\""
        );

        Matcher matcher = pattern.matcher(result);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }
}
