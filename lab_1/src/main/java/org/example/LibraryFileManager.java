package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryFileManager {

    public static final String CSV_HEADER = "id,name,author,available";

    public void exportBooks(List<Book> books, Comparator<Book> sortBy, Writer writer) throws IOException {
        List<Book> sorted = new ArrayList<>(books);
        if (sortBy != null) sorted.sort(sortBy);

        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(CSV_HEADER);
        bw.newLine();
        for (Book b : sorted) {
            bw.write(b.toCsvLine());
            bw.newLine();
        }
        bw.flush();
    }

    public void exportBooksToFile(List<Book> books, Comparator<Book> sortBy, String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            exportBooks(books, sortBy, fw);
        }
    }

    public List<Book> importBooks(java.io.Reader reader) throws IOException {
        List<Book> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String line;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
            if (firstLine) { firstLine = false; continue; }   // skip header
            Book b = Book.fromCsvLine(line);
            if (b != null) result.add(b);
        }
        return result;
    }

    public List<Book> importBooksFromFile(String filePath) throws IOException {
        try (FileReader fr = new FileReader(filePath, StandardCharsets.UTF_8)) {
            return importBooks(fr);
        }
    }

    public static Comparator<Book> sortByTitle() {
        return Comparator.comparing(Book::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Book> sortByAuthor() {
        return Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Book> sortById() {
        return Comparator.comparingInt(Book::getId);
    }
}
