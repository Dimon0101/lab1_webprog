package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Бізнес-логіка (позика / повернення книг)")
class ReaderTest {

    private Reader reader;
    private Book   book;

    @BeforeEach
    void setUp() {
        reader = new Reader(1, "Олена");
        book   = new Book(10, "Кобзар", "Шевченко");
    }

    @Test
    @DisplayName("Книга додається до списку")
    void borrowBook_success() {
        reader.borrowBook(book);
        assertTrue(reader.getBorrowedBooks().contains(book));
    }

    @Test
    @DisplayName("null → IllegalArgumentException")
    void borrowBook_null() {
        assertThrows(IllegalArgumentException.class, () -> reader.borrowBook(null));
    }

    @Test
    @DisplayName("Дублікат → IllegalStateException")
    void borrowBook_duplicate() {
        reader.borrowBook(book);
        assertThrows(IllegalStateException.class, () -> reader.borrowBook(book));
    }

    @Test
    @DisplayName("Книга є у списку → видаляється, повертає true")
    void returnBook_success() {
        reader.borrowBook(book);
        assertTrue(reader.returnBook(book));
        assertFalse(reader.getBorrowedBooks().contains(book));
    }

    @Test
    @DisplayName("Книги немає → false (без виключення)")
    void returnBook_absent() {
        assertFalse(reader.returnBook(book));
    }

    @Test
    @DisplayName("Книга є → true")
    void hasBorrowedBook_true() {
        reader.borrowBook(book);
        assertTrue(reader.hasBorrowedBook(10));
    }

    @Test
    @DisplayName("Книги немає → false")
    void hasBorrowedBook_false() {
        assertFalse(reader.hasBorrowedBook(10));
    }

    @Test
    @DisplayName("Повертає незмінний список")
    void getBorrowedBooks_unmodifiable() {
        reader.borrowBook(book);
        assertThrows(UnsupportedOperationException.class,
                () -> reader.getBorrowedBooks().remove(0));
    }

    @Test
    @DisplayName("Однакові читачі — рівні")
    void equals_identical() {
        assertEquals(new Reader(1, "Олена"), new Reader(1, "Олена"));
    }

    @Test
    @DisplayName("Різні ID — не рівні")
    void equals_differentId() {
        assertNotEquals(new Reader(1, "Олена"), new Reader(2, "Олена"));
    }

    @Test
    @DisplayName("Рівні об'єкти — однаковий хеш")
    void hashCode_consistent() {
        assertEquals(
                new Reader(1, "Олена").hashCode(),
                new Reader(1, "Олена").hashCode()
        );
    }
}
