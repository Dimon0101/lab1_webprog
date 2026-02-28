package org.example;

import java.util.Objects;

public class Book {

    private int id;
    private String name;
    private String author;
    private boolean available;

    public Book(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.available = true;
    }

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getAuthor()             { return author; }
    public void setAuthor(String author)  { this.author = author; }

    public boolean isAvailable()                    { return available; }
    public void setAvailable(boolean available)     { this.available = available; }

    public String toCsvLine() {
        return String.format("%d,%s,%s,%b",
                id,
                escapeCsv(name),
                escapeCsv(author),
                available);
    }

    public static Book fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",", 4);
        if (parts.length != 4) return null;
        try {
            int     id      = Integer.parseInt(parts[0].trim());
            String  name    = parts[1].trim();
            String  author  = parts[2].trim();
            boolean avail   = Boolean.parseBoolean(parts[3].trim());
            Book book = new Book(id, name, author);
            book.setAvailable(avail);
            return book;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escapeCsv(String value) {
        return value == null ? "" : value.replace(",", ";");
    }

    @Override
    public String toString() {
        return String.format("[%d] \"%s\" — %s (%s)",
                id, name, author,
                available ? "Доступна" : "Недоступна");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id
                && Objects.equals(name, book.name)
                && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, author);
    }
}
