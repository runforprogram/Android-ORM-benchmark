package com.study.benchmarkorm;

import android.content.Context;
import android.util.Pair;

import com.study.benchmarkorm.model.Book;
import com.study.benchmarkorm.model.Library;
import com.study.benchmarkorm.model.Person;

import org.litepal.LitePal;

import java.util.List;

public class ORMTestImpl extends ORMTest {

    public ORMTestImpl(Context context) {
        super(context);
    }

    @Override
    public void initDB(Context context) {

    }

    @Override
    public void writeSimple(final List<Book> books) {
        LitePal.saveAll(books);
    }

    @Override
    public List<Book> readSimple(int booksQuantity) {
        List<Book> books = LitePal.findAll(Book.class);
        return books;
    }

    @Override
    public void updateSimple(List<Book> books) {
        writeSimple(books);
    }

    @Override
    public void deleteSimple(final List<Book> books) {
        LitePal.markAsDeleted(books);
    }

    @Override
    public void writeComplex(final List<Library> libraries, final List<Book> books, final List<Person> persons) {
        LitePal.saveAll(libraries);
        LitePal.saveAll(books);
        LitePal.saveAll(persons);
    }

    @Override
    public Pair<List<Library>, Pair<List<Book>, List<Person>>> readComplex(int librariesQuantity, int booksQuantity, int personsQuantity) {
        List<Library> libraries = LitePal.findAll(Library.class);
        List<Book> books = LitePal.findAll(Book.class);
        List<Person> persons = LitePal.findAll(Person.class);
        return new Pair<>(libraries, new Pair<>(books, persons));
    }

    @Override
    public void updateComplex(List<Library> libraries, List<Book> books, List<Person> persons) {
        writeComplex(libraries, books, persons);
    }

    @Override
    public void deleteComplex(final List<Library> libraries, final List<Book> books, final List<Person> persons) {
        LitePal.markAsDeleted(libraries);
        LitePal.markAsDeleted(books);
        LitePal.markAsDeleted(persons);
    }

}
