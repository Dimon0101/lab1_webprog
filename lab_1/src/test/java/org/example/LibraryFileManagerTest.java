package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CSV export / import")
class LibraryFileManagerTest {

    private LibraryFileManager manager;

    @BeforeEach
    void setUp() {
        manager = new LibraryFileManager();
    }

    @Test
    @DisplayName("Записує CSV-заголовок першим рядком")
    void export_writesHeader() throws IOException {
        StringWriter sw = new StringWriter();
        manager.exportBooks(List.of(), null, new StringWriter());
        manager.exportBooks(List.of(), null, sw);
        assertTrue(sw.toString().startsWith(LibraryFileManager.CSV_HEADER));
    }

    @Test
    @DisplayName("Усі книги присутні у виводі")
    void export_allBooksPresent() throws IOException {
        List<Book> books = List.of(
                new Book(1, "Кобзар",       "Шевченко"),
                new Book(2, "Лісова пісня", "Леся Українка")
        );
        StringWriter sw = new StringWriter();
        manager.exportBooks(books, null, sw);
        String csv = sw.toString();
        assertTrue(csv.contains("Кобзар"));
        assertTrue(csv.contains("Лісова пісня"));
    }

    @Test
    @DisplayName("Сортування за назвою — Кобзар перед Тінями")
    void export_sortedByTitle() throws IOException {
        List<Book> books = List.of(
                new Book(2, "Тіні забутих предків", "Коцюбинський"),
                new Book(1, "Кобзар",               "Шевченко")
        );
        StringWriter sw = new StringWriter();
        manager.exportBooks(books, LibraryFileManager.sortByTitle(), sw);
        String csv = sw.toString();
        assertTrue(csv.indexOf("Кобзар") < csv.indexOf("Тіні"),
                "Кобзар має йти раніше за Тіні при сортуванні за назвою");
    }

    @Test
    @DisplayName("Сортування за автором")
    void export_sortedByAuthor() throws IOException {
        List<Book> books = List.of(
                new Book(1, "Книга А", "Шевченко"),
                new Book(2, "Книга Б", "Андрієнко")
        );
        StringWriter sw = new StringWriter();
        manager.exportBooks(books, LibraryFileManager.sortByAuthor(), sw);
        String csv = sw.toString();
        assertTrue(csv.indexOf("Андрієнко") < csv.indexOf("Шевченко"),
                "Андрієнко має йти раніше за Шевченко");
    }

    @Test
    @DisplayName("Порожній список — лише заголовок")
    void export_emptyList() throws IOException {
        StringWriter sw = new StringWriter();
        manager.exportBooks(List.of(), null, sw);
        assertEquals(LibraryFileManager.CSV_HEADER, sw.toString().trim());
    }

    @Test
    @DisplayName("Соректний CSV → список книг з правильними полями")
    void import_valid() throws IOException {
        String csv = LibraryFileManager.CSV_HEADER + "\n"
                + "1,Кобзар,Шевченко,true\n"
                + "2,Лісова пісня,Леся Українка,false\n";
        List<Book> books = manager.importBooks(new StringReader(csv));
        assertEquals(2, books.size());
        assertEquals("Кобзар", books.get(0).getName());
        assertTrue(books.get(0).isAvailable());
        assertFalse(books.get(1).isAvailable());
    }

    @Test
    @DisplayName("Некоректні рядки пропускаються")
    void import_skipsInvalidLines() throws IOException {
        String csv = LibraryFileManager.CSV_HEADER + "\n"
                + "НЕ_ЧИСЛО,Назва,Автор,true\n"
                + "1,Кобзар,Шевченко,true\n";
        List<Book> books = manager.importBooks(new StringReader(csv));
        assertEquals(1, books.size());
        assertEquals("Кобзар", books.get(0).getName());
    }

    @Test
    @DisplayName("Тільки заголовок → порожній список")
    void import_headerOnly() throws IOException {
        List<Book> books = manager.importBooks(
                new StringReader(LibraryFileManager.CSV_HEADER));
        assertTrue(books.isEmpty());
    }

    @Test
    @DisplayName("Порожній вміст → порожній список")
    void import_empty() throws IOException {
        List<Book> books = manager.importBooks(new StringReader(""));
        assertTrue(books.isEmpty());
    }

    @Test
    @DisplayName("export → import відтворює ті ж книги")
    void roundTrip() throws IOException {
        List<Book> original = List.of(
                new Book(1, "Кобзар",       "Шевченко"),
                new Book(2, "Лісова пісня", "Леся Українка"),
                new Book(3, "Тіні забутих предків", "Коцюбинський")
        );

        StringWriter sw = new StringWriter();
        manager.exportBooks(original, LibraryFileManager.sortById(), sw);

        List<Book> imported = manager.importBooks(new StringReader(sw.toString()));
        assertEquals(original.size(), imported.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i), imported.get(i));
        }
    }
}
