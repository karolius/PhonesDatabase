package com.example.user.phonedatabase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



// Kazda tabela jest zapisana w odzielnej klasie zgodnie z dobrymi praktykami. W tym wypadku w
// aplikacji jest tylko jedna, stad jedna dodatkowa klasa, a struktura jest gotowa na dodanie
// kolejnych w przypadku rozwijania projektu.
public class PhonesTable {
    public static final String TABLE_PHONES = "phone";
    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_BRAND = "brand";
    public final static String COLUMN_MODEL = "model";
    public final static String COLUMN_SYSTEM_REV = "system_rev";
    public final static String COLUMN_WWW = "www";
    
    // Wyrazenie odpowiedzialne za tworzenie bazy
    private static final String DB_CREATE = "create table " 
            + TABLE_PHONES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BRAND + " text not null, "
            + COLUMN_MODEL + " text not null,"
            + COLUMN_SYSTEM_REV + " text not null,"
            + COLUMN_WWW + " text);";

    // Wyrazenie odpowiedzialne za usuwanie bazy
    private static final String DB_DELETE = "DROP TABLE IF EXISTS " + TABLE_PHONES;


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PhonesTable.class.getName(), "Upgrading db from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(DB_DELETE);
        onCreate(db);
    }
}