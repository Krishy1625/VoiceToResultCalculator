
package com.calculator;

import javax.swing.*;
import java.awt.*;

public class CalculatorFrame extends JFrame {

    private final Calculator calculator = new Calculator();
    private final SpeechService speechService = new SpeechService();
    private final JTextField display = new JTextField("0");

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

        // Calculator buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 5, 5));

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

        setVisible(true);
    }

    private void handleButton(String text) {

        // Handle number buttons
        if (text.matches("[0-9]")) {

            if (startNewNumber || display.getText().equals("0")) {
                display.setText(text);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + text);
            }

            // Handle clear button
        } else if (text.equals("C")) {

            display.setText("0");
            firstNumber = 0;
            operator = '\0';
            startNewNumber = true;

            // Handle equals button
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

                // Convert the operator into words
                String operatorName;

                switch (operator) {
                    case '+':
                        operatorName = "plus";
                        break;
                    case '-':
                        operatorName = "minus";
                        break;
                    case '*':
                        operatorName = "times";
                        break;
                    case '/':
                        operatorName = "divided by";
                        break;
                    default:
                        operatorName = "unknown operator";
                }

                // Build the sentence to speak
                String spokenText = firstNumber + " "
                        + operatorName + " "
                        + secondNumber + " equals "
                        + result;

                // Display the answer and speak the calculation
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

            // Handle arithmetic operators
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
