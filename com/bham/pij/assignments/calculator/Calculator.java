package com.bham.pij.assignments.calculator;

/**
 * This is a project that implies the basic function of calculator.
 * @author: Changxv
 * @version: 0.1
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Calculator {
    private static final String NUMBER = "(m|([+-]?\\d*\\.?\\d+))";
    private static final String OPERATOR = "[+\\-\\*/]";
    private static final String SHORT_EXPR = OPERATOR + " " + NUMBER;

    private float current_value = 0f;
    private float memory_value = 0f;
    private final ArrayList<Float> history_values = new ArrayList<Float>(); 
    public Calculator() {}

    private static <T> T back(ArrayList<T> arr) throws RuntimeException {
        return arr.get(arr.size() - 1);
    }
    private static <T> void pop(ArrayList<T> arr) throws RuntimeException {
        arr.remove(arr.size() - 1);
    }

    private void cal(ArrayList<Float> nums, ArrayList<Character> ops) 
      throws RuntimeException {
        char op = back(ops);
        pop(ops);
        float y = back(nums);
        pop(nums);
        float x = back(nums);
        pop(nums);
        float z = 0f;
        switch (op) {
            case '+':
                z = x + y;
                break;
            case '-':
                z = x - y;
                break;
            case '*':
                z = x * y;
                break;
            case '/':
                if (y == 0f) throw new RuntimeException();
                z = x / y;
                break;
        }
        nums.add(z);
    }
    private int grade(char op) throws RuntimeException{
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                throw new RuntimeException();
        }
    }
    private float evaluateSlice(String expression) throws RuntimeException {
        var nums = new ArrayList<Float>();
        var ops = new ArrayList<Character>();
        String[] exprs = expression.split(" ");
        for (String s: exprs) {
            if (s.matches(NUMBER)) nums.add(Float.parseFloat(s));
            else if (s.matches(OPERATOR)) {
                if (s.length() != 1) throw new RuntimeException();
                char c = s.charAt(0);
                while (!ops.isEmpty() && grade(back(ops)) >= grade(c))
                    cal(nums, ops);
                ops.add(c);
            } else throw new RuntimeException();
        }
        while (!ops.isEmpty())
            cal(nums, ops);
        return back(nums);
    }

    public float evaluate(String expression) throws RuntimeException {
        if (expression.matches(SHORT_EXPR))
            expression = memory_value + " " + expression;
        
        var brackets = new ArrayList<Integer>();
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i); 
            if (c == '(') brackets.add(i);
            else if (c == ')') {
                int start = back(brackets) + 1;
                String expr = expression.substring(start, i);
                pop(brackets);
                expression = expression.replaceAll(
                    Pattern.quote('(' + expr + ')'), 
                    String.valueOf(evaluateSlice(expr)));
                i = start - 1;
            }
        }
        if (!brackets.isEmpty()) throw new RuntimeException();
        current_value = evaluateSlice(expression);
        history_values.add(current_value);
        return Float.MIN_VALUE;
    }
    public void printHistoryValues() {
        for (int i = 0; i < history_values.size(); ++i)
            System.out.printf("%.6f ", getHistoryValue(i));
        System.out.println();
    }
    public void err() {
        current_value = 0f;
        System.out.println("Invalid input.");
    }
    public float getCurrentValue() {
        return current_value;
    }
    public float getMemoryValue() {
        return memory_value;
    }
    public float getHistoryValue(int index) {
        return history_values.get(index);
    }
    public void setMemoryValue(float memval) {
        memory_value = memval;
    }
    public void clearMemory() {
        setMemoryValue(0);
    }
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        var out = System.out;
        var calc = new Calculator();
        out.println("Please enter an expression, or press m/mr/c/h, or press q to quit.");
        while (true) {
            String expr = sc.nextLine();
            switch (expr) {
                case "q":
                    out.println("The calculator exits successfully.");
                    sc.close();
                    return;
                case "mr":
                    out.printf("The memory value is %.6f\n", 
                               calc.getMemoryValue());
                    break;
                case "m":
                    calc.setMemoryValue(calc.getCurrentValue());
                    out.printf("The memory value is set to %.6f\n",
                               calc.getCurrentValue());
                    break;
                case "h":
                    out.println("All of the history expressions' value are:");
                    calc.printHistoryValues();
                    break;
                case "c":
                    calc.clearMemory();
                    out.println("Memory value has been cleared.");
                    break;
                default:
                    try {
                        calc.evaluate(expr);
                    } catch (Exception e) {
                        calc.err();
                        break;
                    }
                    out.printf("The answer is %.6f\n", calc.getCurrentValue());
            }
        }
    }
}

/*

javac com\bham\pij\assignments\calculator\Calculator.java 
java com.bham.pij.assignments.calculator.Calculator

*/