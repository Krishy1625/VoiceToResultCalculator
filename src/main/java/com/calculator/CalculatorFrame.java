
package com.calculator;

import javax.swing.*;
import java.awt.*;

public class CalculatorFrame extends JFrame {

    private final Calculator calculator = new Calculator();
    private final SpeechService speechService = new SpeechService();
    private final SpeechRecognitionService speechRecognitionService =
            new SpeechRecognitionService();
    private final VoiceCommandParser voiceCommandParser =
            new VoiceCommandParser();

    private final JTextField display = new JTextField("0");
    private final JButton listenButton = new JButton("Listen");

    private double firstNumber;
    private char operator = '\0';
    private boolean startNewNumber = true;

    public CalculatorFrame() {

        setTitle("Text-to-Speech Calculator");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Calculator display
        display.setFont(new Font("Arial", Font.PLAIN, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        // Number and operator buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "C", "0", "=", "+"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 22));
            button.addActionListener(e -> handleButton(text));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Voice input button
        listenButton.setFont(new Font("Arial", Font.BOLD, 18));
        listenButton.addActionListener(e -> listenForCalculation());
        add(listenButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void listenForCalculation() {

        listenButton.setEnabled(false);
        listenButton.setText("Listening...");

        // Run microphone recognition off the Swing UI thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {

            @Override
            protected String doInBackground() throws Exception {
                return speechRecognitionService.listenOnce();
            }

            @Override
            protected void done() {

                try {
                    String recognisedText = get();

                    // Convert recognised words into a calculation
                    double[] calculation =
                            voiceCommandParser.parse(recognisedText);

                    double first = calculation[0];
                    char operation = (char) calculation[1];
                    double second = calculation[2];

                    double result = calculator.calculate(
                            first, second, operation
                    );

                    // Display the answer
                    display.setText(String.valueOf(result));

                    // Speak the answer
                    speechService.speak(
                            first + " " + operatorToWords(operation)
                                    + " " + second + " equals " + result
                    );

                    // Reset manual calculator input state
                    firstNumber = result;
                    operator = '\0';
                    startNewNumber = true;

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            CalculatorFrame.this,
                            "Could not process voice input: "
                                    + ex.getMessage(),
                            "Voice Input Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    listenButton.setEnabled(true);
                    listenButton.setText("Listen");
                }
            }
        };

        worker.execute();
    }

    private String operatorToWords(char operator) {
        switch (operator) {
            case '+':
                return "plus";
            case '-':
                return "minus";
            case '*':
                return "times";
            case '/':
                return "divided by";
            default:
                return "unknown operator";
        }
    }

    private void handleButton(String text) {

        if (text.matches("[0-9]")) {

            if (startNewNumber || display.getText().equals("0")) {
                display.setText(text);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + text);
            }

        } else if (text.equals("C")) {

            display.setText("0");
            firstNumber = 0;
            operator = '\0';
            startNewNumber = true;

        } else if (text.equals("=")) {

            if (operator == '\0') {
                return;
            }

            try {
                double secondNumber =
                        Double.parseDouble(display.getText());

                double result = calculator.calculate(
                        firstNumber, secondNumber, operator
                );

                String spokenText = firstNumber + " "
                        + operatorToWords(operator) + " "
                        + secondNumber + " equals " + result;

                display.setText(String.valueOf(result));
                speechService.speak(spokenText);

                operator = '\0';
                startNewNumber = true;

            } catch (ArithmeticException ex) {

                display.setText("Cannot divide by zero");
                speechService.speak("Cannot divide by zero");

                operator = '\0';
                startNewNumber = true;

            } catch (NumberFormatException ex) {

                display.setText("Invalid number");
                operator = '\0';
                startNewNumber = true;
            }

        } else {

            try {
                firstNumber =
                        Double.parseDouble(display.getText());

                operator = text.charAt(0);
                startNewNumber = true;

            } catch (NumberFormatException ex) {
                display.setText("Invalid number");
            }
        }
    }
}
