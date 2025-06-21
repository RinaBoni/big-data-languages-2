package org.scraper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWork {

    // =============================================
    // Метод для записи данных в CSV
    // =============================================
    public static void writeToCsv(List<String[]> data, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Записываем каждую строку данных
            for (String[] row : data) {
                writer.write(toCsvRow(row) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в CSV файл: " + e.getMessage());
        }
    }

    // =============================================
    // Метод для форматирования строки CSV
    // =============================================
    public static String toCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();

        // Обрабатываем каждое поле в строке
        for (int i = 0; i < fields.length; i++) {
            // Добавляем запятую перед всеми полями, кроме первого
            if (i > 0) sb.append(",");

            // Получаем значение поля (или пустую строку, если null)
            String field = fields[i] != null ? fields[i] : "";

            // Если поле содержит запятые или кавычки - обрабатываем специальным образом
            if (field.contains(",") || field.contains("\"")) {
                // Экранируем кавычки и заключаем поле в кавычки
                sb.append("\"").append(field.replace("\"", "\"\"")).append("\"");
            } else {
                // Просто добавляем поле без обработки
                sb.append(field);
            }
        }

        return sb.toString();
    }

}
