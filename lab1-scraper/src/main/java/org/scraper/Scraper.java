package org.scraper;

// Импорт необходимых библиотек
import org.jsoup.Jsoup;          // Для работы с HTML и парсинга веб-страниц
import org.jsoup.nodes.Document; // Представление HTML документа
import org.jsoup.nodes.Element;  // Представление HTML элемента
import org.jsoup.select.Elements; // Коллекция HTML элементов

import java.io.FileWriter;       // Для записи в файл
import java.io.IOException;      // Для обработки ошибок ввода-вывода
import java.util.ArrayList;      // Динамический массив для хранения данных
import java.util.List;           // Интерфейс списка

public class Scraper {
    // Константы для настройки таймаутов (в миллисекундах)
    private static final int CONNECTION_TIMEOUT = 10000; // 10 секунд на подключение
    private static final int REQUEST_DELAY = 2000;      // 2 секунды между запросами

    public static void main(String[] args) {
        // URL целевой страницы для парсинга
        String url = "https://www.chita.ru/text/";
        // Имя файла для сохранения результатов
        String csvFile = "news_results.csv";

        try {
            // Подключаемся к URL и получаем HTML документ
            Document document = Jsoup.connect(url).timeout(CONNECTION_TIMEOUT).get();

            // Пауза между запросами
            Thread.sleep(REQUEST_DELAY);

            // Выбираем все новостные блоки по CSS селектору
            Elements news = document.select(".wrap_RL97A");

            // Список для хранения данных (каждый элемент - массив строк)
            List<String[]> data = new ArrayList<>();
            // Добавляем заголовки столбцов в первую строку
            data.add(new String[]{"Title", "Description", "Date/Time", "Views", "Comments", "Image URL"});

            System.out.println("Начинаем сбор данных...");

            // Перебираем все новостные блоки
            for (Element item: news) {
                // Извлекаем заголовок новости
                String title = item.select(".header_RL97A").text();
                // Извлекаем краткое описание
                String shortDescription = item.select(".subtitle_RL97A").text();

                /* Блок сбора статистики */
                // Находим блок со статистикой
                Element stats = item.select(".statistic_RL97A").first();
                // Извлекаем дату и время (первый элемент с классом text_eiDCU)
                String date = stats != null ? stats.select(".text_eiDCU").first().text() : "N/A";

                // Инициализируем значения по умолчанию
                String views = "0";
                String comments = "0";

                // Если блок статистики найден
                if (stats != null) {
                    // Получаем все элементы статистики
                    Elements statItems = stats.select(".cell_eiDCU");

                    // Перебираем элементы статистики
                    for (Element stat : statItems) {
                        // Если элемент содержит иконку глаза - это просмотры
                        if (stat.html().contains("icon-eye")) {
                            views = stat.select(".text_eiDCU").text();
                        }
                        // Если элемент содержит иконку комментариев
                        else if (stat.html().contains("icon-comments")) {
                            String commentsText = stat.select(".text_eiDCU").text();
                            // Если текст "Обсудить" - комментариев нет (0)
                            comments = commentsText.equals("Обсудить") ? "0" : commentsText;
                        }
                    }
                }

                // Извлекаем URL изображения
                String imageUrl = item.select(".image_65Oqn picture img").attr("src");

                // Добавляем собранные данные в список
                data.add(new String[]{
                        title,
                        shortDescription,
                        date,
                        views,
                        comments,
                        imageUrl
                });

                // Выводим в консоль информацию о собранной записи
                System.out.println("Собрана запись: " + title);

                // Пауза между обработкой элементов
                Thread.sleep(REQUEST_DELAY / 2); // 1 секунда между элементами
            }

            /* Запись данных в CSV файл */
            try (FileWriter writer = new FileWriter(csvFile)) {
                // Перебираем все строки данных
                for (String[] row : data) {
                    // Конвертируем массив в CSV строку и записываем в файл
                    writer.write(toCsvRow(row) + "\n");
                }
                System.out.println("Данные успешно сохранены в файл: " + csvFile);
            }

        } catch (IOException | InterruptedException e) {
            // Обработка ошибок подключения или записи в файл
            System.err.println("Ошибка: " + e.getMessage());
            // Восстанавливаем статус прерывания
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Метод для корректного форматирования строки CSV
     * @param fields массив строк с данными
     * @return строка в формате CSV (значения в кавычках, разделенные запятыми)
     */
    private static String toCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            // Добавляем запятую перед всеми элементами, кроме первого
            if (i > 0) sb.append(",");

            // Экранируем кавычки в данных (удваиваем их)
            String field = fields[i].replace("\"", "\"\"");

            // Обрамляем каждое значение в кавычки
            sb.append("\"").append(field).append("\"");
        }
        return sb.toString();
    }
}