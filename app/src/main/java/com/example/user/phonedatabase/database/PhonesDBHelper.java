package com.example.user.phonedatabase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class PhonesDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todotable.db";
    private static final int DATABASE_VERSION = 1;


    public PhonesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Metoda jest wywolyywania podczas tworzenia bazy danych
    @Override
    public void onCreate(SQLiteDatabase database) {
        PhonesTable.onCreate(database);
    }


    // Metoda jest wywolyywania podczas aktualizacji bazy danych, np zmiany wersji.
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        PhonesTable.onUpgrade(database, oldVersion, newVersion);
    }
}