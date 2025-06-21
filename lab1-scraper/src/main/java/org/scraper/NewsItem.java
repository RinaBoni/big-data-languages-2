package org.scraper;

public class NewsItem {
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
