package org.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    public static void main(String[] args) {
        String url = "https://www.chita.ru/text/";
        String csvFile = "news_results.csv";

        try {
            Document document = Jsoup.connect(url).get();
            Elements news = document.select(".wrap_RL97A");

            List<String[]> data = new ArrayList<>();
            // Заголовки столбцов
            data.add(new String[]{"Title", "Description", "Date/Time", "Views", "Image URL"});

            System.out.println("Начинаем сбор данных...");

            for (Element item: news) {
                String title = item.select(".header_RL97A").text();
                String shortDescription = item.select(".subtitle_RL97A").text();

                // Сбор даты и времени
                String dateTime = item.select(".statistic_RL97A .text_eiDCU").first().text();

                // Сбор количества просмотров
                String views = item.select(".statistic_RL97A .btnBox_eiDCU .text_eiDCU").first().text();

                // Сбор URL изображения
                String imageUrl = item.select(".image_65Oqn picture img").attr("src");

                data.add(new String[]{
                        title,
                        shortDescription,
                        dateTime,
                        views,
                        imageUrl
                });

                System.out.println("Собрана запись: " + title);
            }

            // Запись в CSV файл
            try (FileWriter writer = new FileWriter(csvFile)) {
                for (String[] row : data) {
                    writer.write(toCsvRow(row) + "\n");
                }
                System.out.println("Данные успешно сохранены в файл: " + csvFile);
            }

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    // Метод для корректного форматирования CSV строки
    private static String toCsvRow(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(",");
            String field = fields[i].replace("\"", "\"\"");
            sb.append("\"").append(field).append("\"");
        }
        return sb.toString();
    }
}
