package com.calculator;

public class Calculator {

    public double calculate(double firstNumber,
                            double secondNumber,
                            char operator) {

        switch (operator) {
            case '+':
                return firstNumber + secondNumber;

            case '-':
                return firstNumber - secondNumber;

            case '*':
                return firstNumber * secondNumber;

            case '/':
                if (secondNumber == 0) {
                    throw new ArithmeticException(
                            "Cannot divide by zero"
                    );
                }
                return firstNumber / secondNumber;

            default:
                throw new IllegalArgumentException(
                        "Invalid operator: " + operator
                );
        }
    }
}
