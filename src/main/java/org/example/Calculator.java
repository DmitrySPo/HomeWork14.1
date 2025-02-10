package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private static final String DB_URL =
            "jdbc:h2:./cache_db;MODE=MYSQL;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public List <Integer> fibonacci(int n) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Создаем таблицу, если она еще не создана
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS cached_result (" +
                    "method VARCHAR(50)," +
                    "arguments INT," +
                    "result VARCHAR(500)," +
                    "PRIMARY KEY (method, arguments)" +
                    ")");

            // Проверяем наличие результата в кэше
            PreparedStatement preparedStm = conn.prepareStatement("SELECT " +
                    "result FROM cached_result WHERE method = ? AND arguments = ?");
            preparedStm.setString(1, "fibonacci");
            preparedStm.setInt(2, n);
            ResultSet rs = preparedStm.executeQuery();

            if (rs.next()) {
                // Получаем результат из кэша
                String cachedResult = rs.getString("result");
                System.out.println("Результат получен из кэша.");
                return parseResult(cachedResult);
            } else {
                System.out.println("Результат не получен из кэша, проводим вычисление.");
                // Вычисляем результат
                List<Integer> result = calculateFibonacci(n);

                // Записываем результат в кэш
                preparedStm = conn.prepareStatement("INSERT INTO" +
                        " cached_result (method, arguments, result) VALUES (?, ?, ?)");
                preparedStm.setString(1, "fibonacci");
                preparedStm.setInt(2, n);
                preparedStm.setString(3, serializeResult(result));
                preparedStm.executeUpdate();

                System.out.println("Результат записан в кэш.");
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
            }
    }

    private List<Integer> calculateFibonacci(int n) {
        List<Integer> result = new ArrayList<>();
        int a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            result.add(a);
            int next = a + b;
            a = b;
            b = next;
        }
        return result;
    }


    private String serializeResult(List<Integer> list) {
        if (list.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer num : list) {
            sb.append(num).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private List<Integer> parseResult(String serialized) {
        String[] parts = serialized.split(",");
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }

}
