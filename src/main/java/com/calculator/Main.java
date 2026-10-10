package com.calculator;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorFrame::new);
    }
}