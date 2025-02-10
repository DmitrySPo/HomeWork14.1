package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        // Тестируем с разными данными
        testFibonacci(calculator, 0);
        testFibonacci(calculator, 5);
        testFibonacci(calculator, 10);
        testFibonacci(calculator, 20);
        testFibonacci(calculator, 10);

    }

    private static void testFibonacci(Calculator calculator, int n) {
        System.out.println("Тестируем метод fibonacci с параметром " + n);

        long start = System.currentTimeMillis();
        List<Integer> result = calculator.fibonacci(n);
        long end = System.currentTimeMillis();

        double time = end - start;

        System.out.println("Результат: " + result);
        System.out.println("Время выполнения: " + time + " мс");
    }
}