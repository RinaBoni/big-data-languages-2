package org.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Scraper {

    // =============================================
    // КОНФИГУРАЦИОННЫЕ ПАРАМЕТРЫ
    // =============================================

    // Таймаут подключения к сайту (10 секунд)
    private static final int CONNECTION_TIMEOUT = 10000;

    // Задержка между запросами (2 секунды) для соблюдения правил вежливости
    private static final int REQUEST_DELAY_MS = 2000;

    // Размер пула потоков (оптимально для 4-8 ядерного процессора)
    private static final int THREAD_POOL_SIZE = 5;

    // Максимальное количество попыток парсинга при ошибках
    private static final int MAX_RETRIES = 3;

    // =============================================
    // Класс для хранения данных одной новости
    // =============================================
    private static class NewsItem {
        String title;         // Заголовок новости
        String description;   // Краткое описание
        String date;          // Дата и время публикации
        String views;         // Количество просмотров
        String comments;      // Количество комментариев
        String imageUrl;      // Ссылка на изображение

        // Преобразуем данные новости в массив строк для CSV
        String[] toArray() {
            return new String[]{title, description, date, views, comments, imageUrl};
        }
    }

    // =============================================
    // Основной метод
    // =============================================
    public static void main(String[] args) {
        // URL целевой страницы
        String url = "https://www.chita.ru/text/";

        // Имя файла для сохранения результатов
        String csvFile = "news_results.csv";

        // Создаем пул потоков с фиксированным количеством потоков
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Список для хранения Future объектов (результатов асинхронных задач)
        List<Future<NewsItem>> futures = new ArrayList<>();

        // Список для хранения данных (первая строка - заголовки столбцов)
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Title", "Description", "Date/Time", "Views", "Comments", "Image URL"});

        try {
            // =============================================
            // Получение основной страницы с новостями
            // =============================================

            // Подключаемся к сайту с установленным таймаутом и User-Agent
            Document document = Jsoup.connect(url)
                    .timeout(CONNECTION_TIMEOUT)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            // Получаем список всех новостных блоков на странице
            Elements newsItems = document.select(".wrap_RL97A");
            System.out.println("Найдено новостей: " + newsItems.size());

            // Счетчик для контроля задержек между запросами
            int requestCounter = 0;

            // =============================================
            // Параллельных парсинг новостей
            // =============================================

            // Для каждого новостного элемента создаем задачу парсинга
            for (Element item : newsItems) {
                // Увеличиваем счетчик запросов
                requestCounter++;

                // Каждые THREAD_POOL_SIZE запросов делаем паузу,
                // чтобы не перегружать сервер
                if (requestCounter % THREAD_POOL_SIZE == 0) {
                    try {
                        Thread.sleep(REQUEST_DELAY_MS);
                    } catch (InterruptedException e) {
                        // Восстанавливаем статус прерывания, если поток был прерван
                        Thread.currentThread().interrupt();
                    }
                }

                // Отправляем задачу парсинга в пул потоков
                // Каждая задача будет выполнена в отдельном потоке
                futures.add(executor.submit(() -> parseNewsItem(item)));
            }

            // =============================================
            // Собираем результаты
            // =============================================

            // Проходим по всем Future объектам и получаем результаты
            for (Future<NewsItem> future : futures) {
                try {
                    // Получаем результат выполнения задачи (блокирующий вызов)
                    NewsItem newsItem = future.get();

                    // Добавляем данные новости в общий список
                    data.add(newsItem.toArray());
                } catch (InterruptedException | ExecutionException e) {
                    // Обрабатываем возможные ошибки выполнения задач
                    System.err.println("Ошибка при обработке элемента: " + e.getMessage());
                }
            }

            // =============================================
            // Сохранение результатов в CSV
            // =============================================

            // Записываем все собранные данные в CSV
            writeToCsv(data, csvFile);
            System.out.println("Данные успешно сохранены в: " + csvFile);

        } catch (IOException e) {
            // Обработка ошибок подключения к сайту
            System.err.println("Ошибка при подключении к сайту: " + e.getMessage());
        } finally {
            // =============================================
            // Завершение работы пулов потоков
            // =============================================

            // Инициируем завершение работы пула потоков
            executor.shutdown();
            try {
                // Ожидаем завершения всех задач (максимум 60 секунд)
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // Принудительно завершаем работу, если задачи не завершились
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // В случае прерывания - принудительно завершаем работу
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // =============================================
    // Метод для парсинга одного элемента новости
    // =============================================
    private static NewsItem parseNewsItem(Element item) {
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

    // =============================================
    // Метод для записи данных в CSV
    // =============================================
    private static void writeToCsv(List<String[]> data, String filename) {
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
    private static String toCsvRow(String[] fields) {
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