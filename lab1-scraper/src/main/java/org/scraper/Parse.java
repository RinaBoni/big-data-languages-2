package org.scraper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parse {

    // Максимальное количество попыток парсинга при ошибках
    private static final int MAX_RETRIES = 3;

    // Задержка между запросами (2 секунды) для соблюдения правил вежливости
    private static final int REQUEST_DELAY_MS = 2000;

    // =============================================
    // Метод для парсинга одного элемента новости
    // =============================================

    public static NewsItem parseNewsItem(Element item) {
        // Создаем объект для хранения данных новости
        NewsItem newsItem = new NewsItem();

        // Счетчик попыток парсинга
        int retryCount = 0;

        // Флаг успешного завершения парсинга
        boolean success = false;

        // Пытаемся распарсить элемент (максимум MAX_RETRIES попыток)
        while (!success && retryCount < MAX_RETRIES) {
            try {
                // Парсим заголовок новости
                newsItem.title = item.select(".header_RL97A").text();

                // Парсим описание новости
                newsItem.description = item.select(".subtitle_RL97A").text();

                // Находим блок со статистикой (дата, просмотры, комментарии)
                Element stats = item.select(".statistic_RL97A").first();

                // Парсим дату и время публикации
                newsItem.date = stats != null ? stats.select(".text_eiDCU").first().text() : "N/A";

                // Инициализируем значения по умолчанию
                newsItem.views = "0";
                newsItem.comments = "0";

                // Если блок статистики найден
                if (stats != null) {
                    // Получаем все элементы статистики
                    Elements statItems = stats.select(".cell_eiDCU");

                    // Перебираем элементы статистики
                    for (Element stat : statItems) {
                        // Если элемент содержит иконку глаза - это просмотры
                        if (stat.html().contains("icon-eye")) {
                            newsItem.views = stat.select(".text_eiDCU").text();
                        }
                        // Если элемент содержит иконку комментариев
                        else if (stat.html().contains("icon-comments")) {
                            String commentsText = stat.select(".text_eiDCU").text();
                            // Если текст "Обсудить" - комментариев нет (0)
                            newsItem.comments = commentsText.equals("Обсудить") ? "0" : commentsText;
                        }
                    }
                }

                // Парсим URL изображения
                newsItem.imageUrl = item.select(".image_65Oqn picture img").attr("src");

                // Устанавливаем флаг успешного завершения
                success = true;

            } catch (Exception e) {
                // Увеличиваем счетчик попыток
                retryCount++;

                // Если превысили максимальное количество попыток
                if (retryCount >= MAX_RETRIES) {
                    System.err.println("Не удалось обработать элемент после " + MAX_RETRIES + " попыток");
                } else {
                    // Делаем задержку перед повторной попыткой (увеличивается с каждой попыткой)
                    try {
                        Thread.sleep(REQUEST_DELAY_MS * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return newsItem;
    }

}
