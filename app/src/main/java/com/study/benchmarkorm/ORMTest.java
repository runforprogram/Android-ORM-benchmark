package com.study.benchmarkorm;

import android.content.Context;
import android.util.Pair;

import com.study.benchmarkorm.model.Book;
import com.study.benchmarkorm.model.Library;
import com.study.benchmarkorm.model.Person;

import java.util.ArrayList;
import java.util.List;

public abstract class ORMTest {
    protected RandomObjectsGenerator randomObjectsGenerator = new RandomObjectsGenerator();

    public ORMTest(Context context) {
        initDB(context);
    }

    public abstract void initDB(Context context);

    public abstract void writeSimple(List<Book> books);

    public abstract List<Book> readSimple(int booksQuantity);

    public abstract void updateSimple(List<Book> books);

    public abstract void deleteSimple(List<Book> books);

    public abstract void writeComplex(List<Library> libraries, List<Book> books, List<Person> persons);

    public abstract Pair<List<Library>, Pair<List<Book>, List<Person>>> readComplex(int librariesQuantity, int booksQuantity, int personsQuantity);

    public abstract void updateComplex(List<Library> libraries, List<Book> books, List<Person> persons);

    public abstract void deleteComplex(List<Library> libraries, List<Book> books, List<Person> persons);

    public abstract boolean isSimpleEmpty();

    public abstract boolean isComplexEmpty();

    public long writeSimple() {
        if (!isSimpleEmpty()){
            deleteSimple();
        }
        final int booksBatchNumber = 1000;

        final int numberOfPasses = 10;
        // warming-up
        for (int i = 0; i < numberOfPasses; i++) {
            writeSimple(randomObjectsGenerator.generateBooks(booksBatchNumber));
            deleteSimple(readSimple(booksBatchNumber));
        }
        randomObjectsGenerator.refresh();

        // main part
        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            List<Book> books = randomObjectsGenerator.generateBooks(booksBatchNumber);
            simpleProfiler.start();
            writeSimple(books);
            allTime[i] = simpleProfiler.stop();
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long readSimple() {
        if (isSimpleEmpty()){
            writeSimple();
        }
        final int booksBatchNumber = 1000;
        final int numberOfPasses = 10;

        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            simpleProfiler.start();
            List<Book> books = readSimple(booksBatchNumber);
            allTime[i] = simpleProfiler.stop();
            deleteSimple(books);
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long updateSimple() {
        if (isSimpleEmpty()){
            writeSimple();
        }
        final int booksBatchNumber = 1000;

        final int numberOfPasses = 10;
        // warming-up
        for (int i = 0; i < numberOfPasses; i++) {
            List<Book> books = readSimple(booksBatchNumber);
            for (Book book: books) {
                book.setAuthor(randomObjectsGenerator.nextString());
            }
            updateSimple(books);
        }

        // main part
        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            List<Book> books = readSimple(booksBatchNumber);
            for (Book book: books) {
                book.setAuthor(randomObjectsGenerator.nextString());
            }
            simpleProfiler.start();
            updateSimple(books);
            allTime[i] = simpleProfiler.stop();
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long deleteSimple() {
        if(isSimpleEmpty()){
            writeSimple();
        }
        final int booksBatchNumber = 1000;
        final int numberOfPasses = 10;

        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            List<Book> books = readSimple(booksBatchNumber);
            simpleProfiler.start();
            deleteSimple(books);
            allTime[i] = simpleProfiler.stop();
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long writeBalanced() {
        if (!isComplexEmpty()){
            deleteBalanced();
        }
        final int booksBatchNumber = 50;
        final int librariesBatchNumber = 50;
        final int personsBatchNumber = 50;

        return writeComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    protected long writeComplexBenchmark(int booksBatchNumber, int librariesBatchNumber, int personsBatchNumber) {
        if (!isComplexEmpty()){
            deleteBalanced();
        }
        final int numberOfPasses = 10;
        final List<Book> books = new ArrayList<>(booksBatchNumber * librariesBatchNumber);
        final List<Person> persons = new ArrayList<>(personsBatchNumber * librariesBatchNumber);
        final List<Library> libraries = new ArrayList<>(librariesBatchNumber);
        List<Book> oneLibraryBooks;
        List<Person> oneLibraryPersons;

        // warming-up
        for (int i = 0; i < numberOfPasses; i++) {
            for (int j = 0; j < librariesBatchNumber; j++) {
                oneLibraryBooks = randomObjectsGenerator.generateBooks(booksBatchNumber);
                oneLibraryPersons = randomObjectsGenerator.generatePersons(personsBatchNumber);
                libraries.add(randomObjectsGenerator.nextLibrary(oneLibraryBooks, oneLibraryPersons));
                books.addAll(oneLibraryBooks);
                persons.addAll(oneLibraryPersons);
                randomObjectsGenerator.refresh();
            }
            writeComplex(libraries, books, persons);
            deleteComplex(libraries, books, persons);
            libraries.clear();
            books.clear();
            persons.clear();
        }

        // main part
        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            for (int j = 0; j < librariesBatchNumber; j++) {
                oneLibraryBooks = randomObjectsGenerator.generateBooks(booksBatchNumber);
                oneLibraryPersons = randomObjectsGenerator.generatePersons(personsBatchNumber);
                libraries.add(randomObjectsGenerator.nextLibrary(oneLibraryBooks, oneLibraryPersons));
                books.addAll(oneLibraryBooks);
                persons.addAll(oneLibraryPersons);
                randomObjectsGenerator.refresh();
            }

            simpleProfiler.start();
            writeComplex(libraries, books, persons);
            allTime[i] = simpleProfiler.stop();

            libraries.clear();
            books.clear();
            persons.clear();
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long readBalanced() {
        if(isComplexEmpty()){
           writeBalanced();
        }
        final int booksBatchNumber = 50;
        final int librariesBatchNumber = 50;
        final int personsBatchNumber = 50;

        return readComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    protected long readComplexBenchmark(int booksBatchNumber, int librariesBatchNumber, int personsBatchNumber) {
        if(isComplexEmpty()){
            writeComplex();
        }
        final int numberOfPasses = 10;
        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            simpleProfiler.start();
            Pair<List<Library>, Pair<List<Book>, List<Person>>> data = readComplex(librariesBatchNumber, booksBatchNumber, personsBatchNumber);
            allTime[i] = simpleProfiler.stop();
            deleteComplex(data.first, data.second.first, data.second.second);
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long updateBalanced() {
        if(isComplexEmpty()){
            writeBalanced();
        }
        final int booksBatchNumber = 50;
        final int librariesBatchNumber = 50;
        final int personsBatchNumber = 50;

        return updateComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    protected long updateComplexBenchmark(int booksBatchNumber, int librariesBatchNumber, int personsBatchNumber) {
        if(isComplexEmpty()){
            writeComplex();
        }
        final int numberOfPasses = 10;

        // warming-up
        for (int i = 0; i < numberOfPasses; i++) {
            Pair<List<Library>, Pair<List<Book>, List<Person>>> readed = readComplex(librariesBatchNumber, booksBatchNumber, personsBatchNumber);
            List<Library> libraries = readed.first;
            List<Book> books = readed.second.first;
            List<Person> persons = readed.second.second;

            for (Library library: libraries) {
                library.setName(randomObjectsGenerator.nextString());
            }

            for (Book book: books) {
                book.setAuthor(randomObjectsGenerator.nextString());
            }

            for (Person person: persons) {
                person.setFirstName(randomObjectsGenerator.nextString());
                person.setSecondName(randomObjectsGenerator.nextString());
            }
            updateComplex(libraries, books, persons);
        }

        // main part
        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            Pair<List<Library>, Pair<List<Book>, List<Person>>> readed = readComplex(librariesBatchNumber, booksBatchNumber, personsBatchNumber);
            List<Library> libraries = readed.first;
            List<Book> books = readed.second.first;
            List<Person> persons = readed.second.second;

            for (Library library: libraries) {
                library.setName(randomObjectsGenerator.nextString());
            }

            for (Book book: books) {
                book.setAuthor(randomObjectsGenerator.nextString());
            }

            for (Person person: persons) {
                person.setFirstName(randomObjectsGenerator.nextString());
                person.setSecondName(randomObjectsGenerator.nextString());
            }

            simpleProfiler.start();
            updateComplex(libraries, books, persons);
            allTime[i] = simpleProfiler.stop();

        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long deleteBalanced() {
        if(isComplexEmpty()){
            writeBalanced();
        }
        final int booksBatchNumber = 50;
        final int librariesBatchNumber = 50;
        final int personsBatchNumber = 50;

        return deleteComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    protected long deleteComplexBenchmark(int booksBatchNumber, int librariesBatchNumber, int personsBatchNumber) {
        if(isComplexEmpty()){
            writeComplex();
        }
        final int numberOfPasses = 10;

        long[] allTime = new long[numberOfPasses];
        SimpleProfiler simpleProfiler = new SimpleProfiler();
        for (int i = 0; i < numberOfPasses; i++) {
            Pair<List<Library>, Pair<List<Book>, List<Person>>> data = readComplex(librariesBatchNumber, booksBatchNumber, personsBatchNumber);
            simpleProfiler.start();
            deleteComplex(data.first, data.second.first, data.second.second);
            allTime[i] = simpleProfiler.stop();
        }

        long average = 0;
        for (int i = 0; i < numberOfPasses; i++) {
            average += allTime[i] / numberOfPasses;
        }
        return average;
    }

    public long writeComplex() {
        if(!isComplexEmpty()){
            deleteComplex();
        }
        final int booksBatchNumber = 500;
        final int librariesBatchNumber = 5;
        final int personsBatchNumber = 400;

        return writeComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    public long readComplex() {
        if(!isComplexEmpty()){
            writeComplex();
        }
        final int booksBatchNumber = 500;
        final int librariesBatchNumber = 5;
        final int personsBatchNumber = 400;

        return readComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    public long updateComplex() {
        if(isComplexEmpty()){
            writeComplex();
        }
        final int booksBatchNumber = 500;
        final int librariesBatchNumber = 5;
        final int personsBatchNumber = 400;

        return updateComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }

    public long deleteComplex() {
        if(isComplexEmpty()){
            writeComplex();
        }
        final int booksBatchNumber = 500;
        final int librariesBatchNumber = 5;
        final int personsBatchNumber = 400;

        return deleteComplexBenchmark(booksBatchNumber, librariesBatchNumber, personsBatchNumber);
    }
}