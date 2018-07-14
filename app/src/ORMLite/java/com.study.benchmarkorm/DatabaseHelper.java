package com.study.benchmarkorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.study.benchmarkorm.model.Book;
import com.study.benchmarkorm.model.Library;
import com.study.benchmarkorm.model.Person;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "LibrariesDB_ORMLite.db";

    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Library.class);
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, Book.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {
        try {
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Book.class, true);
            TableUtils.dropTable(connectionSource, Person.class, true);
            TableUtils.dropTable(connectionSource, Library.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVer);
            throw new RuntimeException(e);
        }
    }



    @Override
    public void close() {
        super.close();
    }
}
