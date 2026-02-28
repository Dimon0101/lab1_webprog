package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Управління книгами, читачами, видача/повернення")
class LibraryTest {

    private Library library;
    private Book    book1, book2;
    private Reader  reader1, reader2;

    @BeforeEach
    void setUp() {
        library = new Library();
        book1   = new Book(1, "Кобзар",       "Тарас Шевченко");
        book2   = new Book(2, "Лісова пісня",  "Леся Українка");
        reader1 = new Reader(101, "Олександр");
        reader2 = new Reader(102, "Марія");
    }

    @Test
    @DisplayName("Нова книга → true, знаходиться у бібліотеці")
    void addBook_success() {
        assertTrue(library.addBook(book1));
        assertNotNull(library.findBook(1));
    }

    @Test
    @DisplayName("Дублікат ID → false")
    void addBook_duplicate() {
        library.addBook(book1);
        assertFalse(library.addBook(new Book(1, "Інша", "Інший")));
    }

    @Test
    @DisplayName("null → false")
    void addBook_null() {
        assertFalse(library.addBook(null));
    }

    @Test
    @DisplayName("Вільна книга → видаляється")
    void removeBook_success() {
        library.addBook(book1);
        assertTrue(library.removeBook(1));
        assertNull(library.findBook(1));
    }

    @Test
    @DisplayName("Неіснуюча книга → false")
    void removeBook_notFound() {
        assertFalse(library.removeBook(999));
    }

    @Test
    @DisplayName("Видана книга → false (не можна видалити)")
    void removeBook_borrowed() {
        library.addBook(book1);
        library.registerReader(reader1);
        library.borrowBook(1, 101);
        assertFalse(library.removeBook(1));
    }
    @Test
    @DisplayName("Новий читач → true")
    void registerReader_success() {
        assertTrue(library.registerReader(reader1));
        assertNotNull(library.findReader(101));
    }

    @Test
    @DisplayName("Дублікат → false")
    void registerReader_duplicate() {
        library.registerReader(reader1);
        assertFalse(library.registerReader(new Reader(101, "Інший")));
    }

    @Test
    @DisplayName("Читач без книг → видаляється")
    void removeReader_success() {
        library.registerReader(reader1);
        assertTrue(library.removeReader(101));
        assertNull(library.findReader(101));
    }

    @Test
    @DisplayName("Читач з книгами → false")
    void removeReader_hasBorrowedBooks() {
        library.addBook(book1);
        library.registerReader(reader1);
        library.borrowBook(1, 101);
        assertFalse(library.removeReader(101));
    }

    @Test
    @DisplayName("Після повернення книг → видаляється")
    void removeReader_afterReturn() {
        library.addBook(book1);
        library.registerReader(reader1);
        library.borrowBook(1, 101);
        library.returnBook(101, 1);
        assertTrue(library.removeReader(101));
    }
    @Test
    @DisplayName("Успішна видача → SUCCESS, книга недоступна")
    void borrowBook_success() {
        library.addBook(book1);
        library.registerReader(reader1);
        assertEquals(Library.BorrowResult.SUCCESS, library.borrowBook(1, 101));
        assertFalse(library.findBook(1).isAvailable());
        assertTrue(library.findReader(101).hasBorrowedBook(1));
    }

    @Test
    @DisplayName("Книга вже видана → ALREADY_BORROWED")
    void borrowBook_alreadyBorrowed() {
        library.addBook(book1);
        library.registerReader(reader1);
        library.registerReader(reader2);
        library.borrowBook(1, 101);
        assertEquals(Library.BorrowResult.ALREADY_BORROWED, library.borrowBook(1, 102));
    }

    @Test
    @DisplayName("Неіснуючий ID → NOT_FOUND")
    void borrowBook_notFound() {
        assertEquals(Library.BorrowResult.NOT_FOUND, library.borrowBook(999, 999));
    }

    @Test
    @DisplayName("Успішне повернення → SUCCESS, книга доступна")
    void returnBook_success() {
        library.addBook(book1);
        library.registerReader(reader1);
        library.borrowBook(1, 101);
        assertEquals(Library.ReturnResult.SUCCESS, library.returnBook(101, 1));
        assertTrue(library.findBook(1).isAvailable());
    }

    @Test
    @DisplayName("Читач не брав книгу → NOT_BORROWED_BY_READER")
    void returnBook_notBorrowed() {
        library.addBook(book1);
        library.registerReader(reader1);
        assertEquals(Library.ReturnResult.NOT_BORROWED_BY_READER, library.returnBook(101, 1));
    }

    @Test
    @DisplayName("Неіснуючі ID → NOT_FOUND")
    void returnBook_notFound() {
        assertEquals(Library.ReturnResult.NOT_FOUND, library.returnBook(999, 999));
    }

    @Test
    @DisplayName("Сортування за назвою (А→Я)")
    void getBooksSorted_byTitle() {
        library.addBook(book2);   // Лісова пісня
        library.addBook(book1);   // Кобзар
        List<Book> sorted = library.getBooksSorted(LibraryFileManager.sortByTitle());
        assertEquals("Кобзар",      sorted.get(0).getName());
        assertEquals("Лісова пісня", sorted.get(1).getName());
    }

    @Test
    @DisplayName("null comparator → без сортування")
    void getBooksSorted_nullComparator() {
        library.addBook(book1);
        library.addBook(book2);
        assertEquals(2, library.getBooksSorted(null).size());
    }
}
