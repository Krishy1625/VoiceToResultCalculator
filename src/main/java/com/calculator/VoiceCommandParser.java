
package com.calculator;

import java.util.HashMap;
import java.util.Map;

public class VoiceCommandParser {

    private final Map<String, Integer> numbers = new HashMap<>();

    public VoiceCommandParser() {
        numbers.put("zero", 0);
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);
        numbers.put("six", 6);
        numbers.put("seven", 7);
        numbers.put("eight", 8);
        numbers.put("nine", 9);
        numbers.put("ten", 10);
        numbers.put("eleven", 11);
        numbers.put("twelve", 12);
        numbers.put("thirteen", 13);
        numbers.put("fourteen", 14);
        numbers.put("fifteen", 15);
        numbers.put("sixteen", 16);
        numbers.put("seventeen", 17);
        numbers.put("eighteen", 18);
        numbers.put("nineteen", 19);
        numbers.put("twenty", 20);
        numbers.put("thirty", 30);
        numbers.put("forty", 40);
        numbers.put("fifty", 50);
        numbers.put("sixty", 60);
        numbers.put("seventy", 70);
        numbers.put("eighty", 80);
        numbers.put("ninety", 90);
    }

    public double[] parse(String speech) {

        String text = speech.toLowerCase()
                .replace("what is", "")
                .replace("calculate", "")
                .replace("how much is", "")
                .trim();

        text = text.replace("plus", "|+|")
                .replace("add", "|+|")
                .replace("minus", "|-|")
                .replace("take away", "|-|")
                .replace("multiplied by", "|*|")
                .replace("times", "|*|")
                .replace("divided by", "|/|")
                .replace("divide by", "|/|");

        String[] parts = text.split("\\|");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Say two numbers and an operator, e.g. one hundred plus twenty."
            );
        }

        double firstNumber = parseNumber(parts[0].trim());
        char operator = parts[1].trim().charAt(0);
        double secondNumber = parseNumber(parts[2].trim());

        return new double[] {
                firstNumber,
                operator,
                secondNumber
        };
    }

    private int parseNumber(String words) {

        String[] tokens = words.trim().split("\\s+");

        int total = 0;
        int current = 0;

        for (String token : tokens) {

            if (token.equals("and")) {
                continue;
            }

            if (token.equals("hundred")) {
                if (current == 0) {
                    current = 1;
                }
                current *= 100;
                total += current;
                current = 0;

            } else if (numbers.containsKey(token)) {
                current += numbers.get(token);

            } else {
                try {
                    current += Integer.parseInt(token);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                            "I didn't understand the number: " + token
                    );
                }
            }
        }

        return total + current;
    }
}
