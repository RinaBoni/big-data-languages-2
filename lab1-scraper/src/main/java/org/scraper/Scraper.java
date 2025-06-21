package org.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.scraper.CsvWork.writeToCsv;
import static org.scraper.DbWork.initializeDatabase;
import static org.scraper.DbWork.saveToDatabase;
import static org.scraper.Parse.parseNewsItem;

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




    // =============================================
    // Класс для хранения данных одной новости
    // =============================================


    // =============================================
    // Основной метод
    // =============================================
    public static void main(String[] args) {

        // URL целевой страницы
        String url = "https://www.chita.ru/text/";

        // Инициализация БД
        initializeDatabase();

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
            // Получение основной страницы с новостями

            // Подключаемся к сайту с установленным таймаутом
            Document document = Jsoup.connect(url)
                    .timeout(CONNECTION_TIMEOUT)
                    .get();

            // Получаем список всех новостных блоков на странице
            Elements newsItems = document.select(".wrap_RL97A");
            System.out.println("Найдено новостей: " + newsItems.size());

            // Счетчик для контроля задержек между запросами
            int requestCounter = 0;

            // Параллельных парсинг новостей

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

            // Собираем результаты

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

            // Сохранение результатов в CSV

            // Записываем все собранные данные в CSV
            writeToCsv(data, csvFile);
            System.out.println("Данные успешно сохранены в: " + csvFile);
            for (Future<NewsItem> future : futures) {
                try {
                    NewsItem newsItem = future.get();
                    saveToDatabase(newsItem);
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Ошибка при обработке элемента: " + e.getMessage());
                }
            }

            System.out.println("Данные успешно сохранены в базу данных");

        } catch (IOException e) {
            // Обработка ошибок подключения к сайту
            System.err.println("Ошибка при подключении к сайту: " + e.getMessage());
        } finally {


            // Завершение работы пулов потоков

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

}