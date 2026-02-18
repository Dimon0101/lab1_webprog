package org.example;

import java.util.*;
import java.util.Dictionary;
import java.util.function.Predicate;

class Book
{
    int id;
    String name;
    String author;
    Boolean isAvailable;

    public Book(int id_, String name_, String author_)
    {
        id = id_;
        name = name_;
        author = author_;
        isAvailable = true;
    }

    public int getId()
    {
        return id;
    }
    public String getName()
    {
        return name  ;
    }
    public boolean isAvailable()
    {
        return isAvailable;
    }
    public void setAvailable(boolean available)
    {
        isAvailable = available;
    }

    @Override
    public String toString() {
        String status = "";
        if(isAvailable == true)
        {
            status = "Доступна";
        }
        else
        {
            status = "Недоступна";
        }
        return String.format("%d. %s (%s) %s", id, name, author, status);
    }
}

class Reader
{
    String name;
    int id;
    List<Book> borrowedBooks;

    public Reader(int id, String name)
    {
        this.id = id;
        this.name = name;
        this.borrowedBooks = new ArrayList<Book>();
    }
    public int getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public List<Book> getBorrowedBooks()
    {
        return borrowedBooks;
    }

    public void BorrowBook(Book book)
    {
        borrowedBooks.add(book);
    }

    public void ReturnBook(Book book)
    {
        if(borrowedBooks.contains(book))
        {
            borrowedBooks.remove(book);
        }
        else
        {
            System.out.println("You don`t borrow that`s book");
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(borrowedBooks.isEmpty())
        {
            str.append("Нема книг");
        }
        else
        {
            for(Book b : borrowedBooks)
            {
                str.append(b.getName()).append(" , ");
            }
        }
        return String.format("%d. %s Книги: %s", id,name,str);
    }
}

class Library
{
    Map<Integer,Book> allBooks = new HashMap<>();
    Map<Integer,Reader> allReaders = new HashMap<>();

    public void AddBook(Book book)
    {
        if(!allBooks.containsKey(book.id))
        {
            System.out.println("Book successfully added");
            allBooks.put(book.id,book);
            return;
        }
        System.out.println("Can`t add book");
    }

    public void RemoveBook(int id)
    {
        if(allBooks.containsKey(id))
        {
            if(allBooks.get(id).isAvailable == true) {
                System.out.println("Book successfully deleted");
                allBooks.remove(id);
                return;
            }
            System.out.println("Borowed books can`t be removed");
            return;
        }
        System.out.println("Book can`t be removed");
    }

    public void RegisterReader(Reader reader)
    {
        if(!allReaders.containsKey(reader.id))
        {
            System.out.println("Reader added successfully");
            allReaders.put(reader.id, reader);
            return;
        }
        System.out.println("Can`t add reader");
    }

    public void RemoveReader(int id)
    {
        if(allReaders.containsKey(id))
        {
            System.out.println("Reader deleted");
            allReaders.remove(id);
            return;
        }
        System.out.println("Reader can`t be deleted");
    }

    public void BorrowBook(int bookId, int readerId)
    {
        if(allBooks.containsKey(bookId) && allReaders.containsKey(readerId))
        {
            Book book = allBooks.get(bookId);
            Reader reader = allReaders.get(readerId);
            if(book.isAvailable)
            {
                book.setAvailable(false);
                reader.BorrowBook(book);
                System.out.println("Reader successfully borrow book");
                return;
            }
            System.out.println("That`s book was borrowed before");
            return;
        }
        System.out.println("No such user or book id`s");
    }

    public void ReturnBook(int readerId, int bookId)
    {
        if(allBooks.containsKey(bookId) && allReaders.containsKey(readerId))
        {
            Book book = allBooks.get(bookId);
            Reader reader = allReaders.get(readerId);
            if(reader.borrowedBooks.contains(book))
            {
                book.setAvailable(true);
                reader.ReturnBook(book);
                System.out.println("Book get back successfully");
                return;
            }
            System.out.println("Reader don`t borrow that`s book");
            return;
        }
        System.out.println("No such user or book id`s");
    }

    public void displayLibraryState() {
        System.out.println("\nState of library");
        System.out.println("Books:");
        for(Book b : allBooks.values())
        {
            System.out.println(b.toString());
        }

        System.out.println("\nReaders:");
        for(Reader r : allReaders.values())
        {
            System.out.println(r.toString());
        }
        System.out.println("\n");
    }
}
public class Main {
    static void main() {
        Library library = new Library();

        // Створення книг
        Book b1 = new Book(1, "Кобзар", "Тарас Шевченко");
        Book b2 = new Book(2, "Тіні забутих предків", "Михайло Коцюбинський");
        Book b3 = new Book(3, "Лісова пісня", "Леся Українка");

        // Додавання книг до бібліотеки
        library.AddBook(b1);
        library.AddBook(b2);
        library.AddBook(b3);
        library.AddBook(b1); // Спроба додати дублікат (має бути помилка)

        // Створення та реєстрація читачів
        Reader r1 = new Reader(101, "Олександр");
        Reader r2 = new Reader(102, "Марія");

        library.RegisterReader(r1);
        library.RegisterReader(r2);

        // Виводимо початковий стан
        library.displayLibraryState();

        // Олександр бере "Кобзар" (успішно)
        library.BorrowBook(1, 101);

        // Марія намагається взяти "Кобзар" (вже зайнята - помилка)
        library.BorrowBook(1, 102);

        // Марія бере "Тіні забутих предків" (успішно)
        library.BorrowBook(2, 102);

        // Спроба взяти неіснуючу книгу
        library.BorrowBook(999, 101);

        library.displayLibraryState();

        // Олександр повертає книгу
        library.ReturnBook(101, 1);

        // Спроба повернути книгу, яку не брав
        library.ReturnBook(101, 3);

        library.displayLibraryState();

        // Видалення вільної книги (успішно)
        library.RemoveBook(1); // Кобзар зараз вільний

        // Спроба видалення зайнятої книги (Марія ще тримає книгу з ID 2)
        library.RemoveBook(2);

        // Видалення читача
        library.RemoveReader(101); // Видаляємо Олександра

        library.displayLibraryState();
    }
}
