package org.example;

import java.util.*;

public class Library {

    private final Map<Integer, Book>   books   = new LinkedHashMap<>();
    private final Map<Integer, Reader> readers = new LinkedHashMap<>();

    public boolean addBook(Book book) {
        if (book == null || books.containsKey(book.getId())) return false;
        books.put(book.getId(), book);
        return true;
    }

    public boolean removeBook(int id) {
        Book book = books.get(id);
        if (book == null || !book.isAvailable()) return false;
        books.remove(id);
        return true;
    }

    public Collection<Book> getAllBooks() {
        return Collections.unmodifiableCollection(books.values());
    }

    public Book findBook(int id) {
        return books.get(id);
    }

    public boolean registerReader(Reader reader) {
        if (reader == null || readers.containsKey(reader.getId())) return false;
        readers.put(reader.getId(), reader);
        return true;
    }

    public boolean removeReader(int id) {
        Reader reader = readers.get(id);
        if (reader == null || !reader.getBorrowedBooks().isEmpty()) return false;
        readers.remove(id);
        return true;
    }

    public Collection<Reader> getAllReaders() {
        return Collections.unmodifiableCollection(readers.values());
    }

    public Reader findReader(int id) {
        return readers.get(id);
    }

    public BorrowResult borrowBook(int bookId, int readerId) {
        Book   book   = books.get(bookId);
        Reader reader = readers.get(readerId);
        if (book == null || reader == null) return BorrowResult.NOT_FOUND;
        if (!book.isAvailable())            return BorrowResult.ALREADY_BORROWED;
        book.setAvailable(false);
        reader.borrowBook(book);
        return BorrowResult.SUCCESS;
    }

    public ReturnResult returnBook(int readerId, int bookId) {
        Book   book   = books.get(bookId);
        Reader reader = readers.get(readerId);
        if (book == null || reader == null) return ReturnResult.NOT_FOUND;
        boolean removed = reader.returnBook(book);
        if (!removed) return ReturnResult.NOT_BORROWED_BY_READER;
        book.setAvailable(true);
        return ReturnResult.SUCCESS;
    }

    public List<Book> getBooksSorted(Comparator<Book> cmp) {
        List<Book> list = new ArrayList<>(books.values());
        if (cmp != null) list.sort(cmp);
        return list;
    }

    public List<Reader> getReadersSorted(Comparator<Reader> cmp) {
        List<Reader> list = new ArrayList<>(readers.values());
        if (cmp != null) list.sort(cmp);
        return list;
    }

    public enum BorrowResult {
        SUCCESS,
        NOT_FOUND,
        ALREADY_BORROWED
    }

    public enum ReturnResult {
        SUCCESS,
        NOT_FOUND,
        NOT_BORROWED_BY_READER
    }
}
