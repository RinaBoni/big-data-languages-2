package org.scraper;

import java.sql.*;


public class DbWork {


    // Путь к файлу БД
    public static final String DB_URL = "jdbc:sqlite:news.db";

    // =============================================
    // Инициализация БД и создание таблицы
    // =============================================
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Создаем таблицу, если она не существует
            String sql = "CREATE TABLE IF NOT EXISTS news (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "date TEXT," +
                    "views TEXT," +
                    "comments INTEGER," +
                    "image_url TEXT" +
                    ")";

            stmt.execute(sql);
            System.out.println("Таблица news создана или уже существует");

        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации БД: " + e.getMessage());
        }
    }


    // =============================================
    // Сохранение новости в БД
    // =============================================
    public static void saveToDatabase(NewsItem item) {
        String sql = "INSERT INTO news(title, description, date, views, comments, image_url) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Устанавливаем параметры запроса
            pstmt.setString(1, item.title);
            pstmt.setString(2, item.description);
            pstmt.setString(3, item.date);
            pstmt.setString(4, item.views);
            pstmt.setInt(5, Integer.parseInt(item.comments));
            pstmt.setString(6, item.imageUrl);

            // Выполняем запрос
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }


}
