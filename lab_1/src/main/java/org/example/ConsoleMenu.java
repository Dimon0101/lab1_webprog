package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final Library            library     = new Library();
    private final LibraryFileManager fileManager = new LibraryFileManager();
    private final Scanner            sc          = new Scanner(System.in, "UTF-8");

    public void run() {
        println("Система управління бібліотекою");
        boolean running = true;
        while (running) {
            printMainMenu();
            switch (readInt("Ваш вибір: ")) {
                case 1  -> menuBooks();
                case 2  -> menuReaders();
                case 3  -> actionBorrow();
                case 4  -> actionReturn();
                case 5  -> actionExport();
                case 6  -> actionImport();
                case 7  -> displayState();
                case 0  -> running = false;
                default -> println("Невідома команда.\n");
            }
        }
        println("До побачення!");
    }

    private void menuBooks() {
        println("\nУправління книгами");
        println("  1. Додати книгу");
        println("  2. Видалити книгу");
        println("  3. Показати всі книги");
        println("  0. Назад");
        switch (readInt("Вибір: ")) {
            case 1 -> actionAddBook();
            case 2 -> actionRemoveBook();
            case 3 -> actionListBooks();
        }
    }

    private void menuReaders() {
        println("\n── Управління читачами ──");
        println("  1. Зареєструвати читача");
        println("  2. Видалити читача");
        println("  3. Показати всіх читачів");
        println("  0. Назад");
        switch (readInt("Вибір: ")) {
            case 1 -> actionAddReader();
            case 2 -> actionRemoveReader();
            case 3 -> actionListReaders();
        }
    }

    private void actionAddBook() {
        int    id     = readInt("  ID книги : ");
        String title  = readString("  Назва    : ");
        String author = readString("  Автор    : ");
        if (library.addBook(new Book(id, title, author))) {
            println("Книгу додано.");
        } else {
            println("Книга з таким ID вже існує.");
        }
    }

    private void actionRemoveBook() {
        int id = readInt("  ID книги для видалення: ");
        if (library.removeBook(id)) {
            println("Книгу видалено.");
        } else {
            println("Не вдалося видалити: книга не знайдена або зараз видана читачеві.");
        }
    }

    private void actionListBooks() {
        var books = library.getAllBooks();
        if (books.isEmpty()) { println("  Книг немає."); return; }
        println("");
        books.forEach(b -> println("  " + b));
        println("");
    }

    private void actionAddReader() {
        int    id   = readInt("  ID читача : ");
        String name = readString("  Ім'я      : ");
        if (library.registerReader(new Reader(id, name))) {
            println("Читача зареєстровано.");
        } else {
            println("Читач з таким ID вже існує.");
        }
    }

    private void actionRemoveReader() {
        int id = readInt("  ID читача для видалення: ");
        if (library.removeReader(id)) {
            println("Читача видалено.");
        } else {
            println("Не вдалося видалити: читач не знайдений або ще має книги.");
        }
    }

    private void actionListReaders() {
        var readers = library.getAllReaders();
        if (readers.isEmpty()) { println("  Читачів немає."); return; }
        println("");
        readers.forEach(r -> println("  " + r));
        println("");
    }

    private void actionBorrow() {
        println("Видача книги");
        int bookId   = readInt("  ID книги   : ");
        int readerId = readInt("  ID читача  : ");
        switch (library.borrowBook(bookId, readerId)) {
            case SUCCESS          -> println("  Книгу видано читачеві.");
            case NOT_FOUND        -> println("  Книгу або читача не знайдено.");
            case ALREADY_BORROWED -> println("  Книга вже видана іншому читачеві.");
        }
    }

    private void actionReturn() {
        println("Повернення книги");
        int readerId = readInt("  ID читача  : ");
        int bookId   = readInt("  ID книги   : ");
        switch (library.returnBook(readerId, bookId)) {
            case SUCCESS                -> println(" Книгу повернено до бібліотеки.");
            case NOT_FOUND              -> println(" Книгу або читача не знайдено.");
            case NOT_BORROWED_BY_READER -> println(" Цей читач не брав дану книгу.");
        }
    }

    private void actionExport() {
        println("\nЕкспорт книг у CSV");
        println("Сортування:");
        println(" 1 — за назвою   2 — за автором   3 — за ID   0 — без сортування");
        int sort = readInt("  Вибір: ");

        var cmp = switch (sort) {
            case 1  -> LibraryFileManager.sortByTitle();
            case 2  -> LibraryFileManager.sortByAuthor();
            case 3  -> LibraryFileManager.sortById();
            default -> null;
        };

        String path = readString("Шлях до файлу (напр. books.csv): ");
        List<Book> books = new ArrayList<>(library.getAllBooks());
        try {
            fileManager.exportBooksToFile(books, cmp, path);
            println("Експортовано " + books.size() + " книг → " + path);
        } catch (IOException e) {
            println("Помилка запису: " + e.getMessage());
        }
    }

    private void actionImport() {
        println("\nІмпорт книг з CSV");
        String path = readString("Шлях до файлу (напр. books.csv): ");
        try {
            List<Book> imported = fileManager.importBooksFromFile(path);
            int added = 0;
            for (Book b : imported) {
                if (library.addBook(b)) added++;
            }
            println(String.format("Прочитано %d рядків; додано %d нових книг (дублікатів: %d).",
                    imported.size(), added, imported.size() - added));
        } catch (IOException e) {
            println("Помилка читання: " + e.getMessage());
        }
    }

    private void displayState() {
        println("\nСтан бібліотеки");
        println("Книги:");
        var books = library.getAllBooks();
        if (books.isEmpty()) println("  (немає)");
        else books.forEach(b -> println("  " + b));

        println("\nЧитачі:");
        var readers = library.getAllReaders();
        if (readers.isEmpty()) println("  (немає)");
        else readers.forEach(r -> println("  " + r));
    }

    private void printMainMenu() {
        println("\nГоловне меню ");
        println("  1. Управління книгами");
        println("  2. Управління читачами");
        println("  3. Видати книгу читачеві");
        println("  4. Повернути книгу");
        println("  5. Експортувати книги у CSV");
        println("  6. Імпортувати книги з CSV");
        println("  7. Показати стан бібліотеки");
        println("  0. Вихід");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                println("Будь ласка, введіть ціле число.");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private void println(String msg) {
        System.out.println(msg);
    }
}