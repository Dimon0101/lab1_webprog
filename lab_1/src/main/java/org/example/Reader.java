package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Reader {

    private int id;
    private String name;
    private final List<Book> borrowedBooks = new ArrayList<>();

    public Reader(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }

    public List<Book> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks);
    }

    public void setBorrowedBooks(List<Book> books) {
        borrowedBooks.clear();
        if (books != null) borrowedBooks.addAll(books);
    }

    public void borrowBook(Book book) {
        if (book == null)
            throw new IllegalArgumentException("Book must not be null");
        if (borrowedBooks.contains(book))
            throw new IllegalStateException("Reader already borrowed: " + book.getName());
        borrowedBooks.add(book);
    }

    public boolean returnBook(Book book) {
        return borrowedBooks.remove(book);
    }

    public boolean hasBorrowedBook(int bookId) {
        return borrowedBooks.stream().anyMatch(b -> b.getId() == bookId);
    }

    @Override
    public String toString() {
        if (borrowedBooks.isEmpty()) {
            return String.format("[%d] %s | Книги: немає", id, name);
        }
        StringBuilder sb = new StringBuilder();
        for (Book b : borrowedBooks) {
            sb.append("\"").append(b.getName()).append("\"; ");
        }
        return String.format("[%d] %s | Книги: %s", id, name, sb.toString().trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return id == reader.id && Objects.equals(name, reader.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
