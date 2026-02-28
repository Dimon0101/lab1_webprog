package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Серіалізація, equals, hashCode")
class BookTest {

    @Test
    @DisplayName("доступна книга → correct CSV")
    void toCsvLine_available() {
        Book b = new Book(1, "Кобзар", "Тарас Шевченко");
        assertEquals("1,Кобзар,Тарас Шевченко,true", b.toCsvLine());
    }

    @Test
    @DisplayName("Недоступна книга → available=false")
    void toCsvLine_unavailable() {
        Book b = new Book(2, "Лісова пісня", "Леся Українка");
        b.setAvailable(false);
        assertEquals("2,Лісова пісня,Леся Українка,false", b.toCsvLine());
    }

    @Test
    @DisplayName("Коми в назві замінюються на крапку з комою")
    void toCsvLine_commaInName() {
        Book b = new Book(3, "Наука, і мистецтво", "Автор");
        String csv = b.toCsvLine();
        assertFalse(csv.split(",").length > 4, "Кома в назві не має розбивати CSV");
        assertTrue(csv.contains("Наука; і мистецтво"));
    }


    @Test
    @DisplayName("Коректний рядок → Book з правильними полями")
    void fromCsvLine_valid() {
        Book b = Book.fromCsvLine("5,Тіні забутих предків,Коцюбинський,false");
        assertNotNull(b);
        assertEquals(5,              b.getId());
        assertEquals("Тіні забутих предків", b.getName());
        assertEquals("Коцюбинський", b.getAuthor());
        assertFalse(b.isAvailable());
    }

    @Test
    @DisplayName("null → null")
    void fromCsvLine_null() {
        assertNull(Book.fromCsvLine(null));
    }

    @Test
    @DisplayName("Порожній рядок → null")
    void fromCsvLine_blank() {
        assertNull(Book.fromCsvLine("   "));
    }

    @Test
    @DisplayName("Нечисловий ID → null")
    void fromCsvLine_invalidId() {
        assertNull(Book.fromCsvLine("abc,Назва,Автор,true"));
    }

    @Test
    @DisplayName("Замало полів → null")
    void fromCsvLine_tooFewFields() {
        assertNull(Book.fromCsvLine("1,Назва,Автор"));
    }


    @Test
    @DisplayName("Однакові книги — рівні")
    void equals_identical() {
        assertEquals(new Book(1, "A", "B"), new Book(1, "A", "B"));
    }

    @Test
    @DisplayName("Різні ID — не рівні")
    void equals_differentId() {
        assertNotEquals(new Book(1, "A", "B"), new Book(2, "A", "B"));
    }

    @Test
    @DisplayName("Рівні об'єкти — однаковий хеш")
    void hashCode_consistent() {
        assertEquals(
                new Book(1, "A", "B").hashCode(),
                new Book(1, "A", "B").hashCode()
        );
    }

    @Test
    @DisplayName("Порівняння з null → false")
    void equals_null() {
        assertNotEquals(null, new Book(1, "A", "B"));
    }

    @Test
    @DisplayName("Порівняння з іншим типом → false")
    void equals_otherType() {
        assertNotEquals("string", new Book(1, "A", "B"));
    }
}
