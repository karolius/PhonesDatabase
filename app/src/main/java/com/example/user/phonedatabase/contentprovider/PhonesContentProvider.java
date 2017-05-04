package com.example.user.phonedatabase.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.user.phonedatabase.database.PhonesDBHelper;
import com.example.user.phonedatabase.database.PhonesTable;

import java.util.Arrays;
import java.util.HashSet;


//Dostawca tresci, sluzy do przesylania danych takze do innych aplikacji
public class PhonesContentProvider extends ContentProvider {
    // Ddzieki niemu mamy dostep do DB
    private PhonesDBHelper database;

    // Stale odpowiedzialne za rozpoznanie URI
    private static final int WHOLE_TABLE = 10;
    private static final int CHOSEN_ROW = 20;

    // Odroznienie naszego dostawcy od innych
    private static final String IDENTIFIER = "com.example.user.phonedatabase.contentprovider";
    private static final String BASE_PATH = "phonedatabase"; // katalog glowny aplikacji
    public static final Uri CONTENT_URI = Uri.parse("content://" + IDENTIFIER
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/phonedatabase";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/phonedatabase";

    // UriMatcher oznaczony jako pusty- no_match
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static { //dodanie rozpoznawanych URI
        sURIMatcher.addURI(IDENTIFIER, BASE_PATH, WHOLE_TABLE);
        sURIMatcher.addURI(IDENTIFIER, BASE_PATH + "/#", CHOSEN_ROW);
    }


    @Override
    public boolean onCreate() {
        database = new PhonesDBHelper(getContext());
        return false;
    }


    @Override
    public Cursor query(Uri uri,                // jakie dane ma przyslac dostawca(**ODP. w DB)
                        String[] projection,    // ktore kol. z tabeli nas interesują (columns)
                        String selection,       // wybranie wierszy, zwracanych przez dostawce
                        String[] selectionArgs, // odpowiednio: (where i whereArgs)
                        String sortOrder) {

        // Uzywanie SQLiteQueryBuilder zamiast metody query()
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection); // Sprpawdz czy nie zazadano nieistniejacej kolumny

        // Ustawienie tabeli
        queryBuilder.setTables(PhonesTable.TABLE_PHONES);
        int uriType = sURIMatcher.match(uri); // rozpoznaj rodzaj URI(WHOLE TABLE v CHOSEN_ROW)
        switch (uriType) {
            case WHOLE_TABLE:
                break;
            case CHOSEN_ROW:
                // dodawanie ID do zapytania
                queryBuilder.appendWhere(PhonesTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // Upewnij sie, ze potencjalny listner zostal poinformowany
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Wstawienie wartosci zalecznie od tego czy jest to wiersz czy tabela
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                id = sqlDB.insert(PhonesTable.TABLE_PHONES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                rowsDeleted = sqlDB.delete(PhonesTable.TABLE_PHONES, selection,
                        selectionArgs);
                break;
            case CHOSEN_ROW:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            PhonesTable.TABLE_PHONES,
                            PhonesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            PhonesTable.TABLE_PHONES,
                            PhonesTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, // nowe wartosci pol
                      String selection,              // war, do spelnienia przez aktualizowane wier.
                      String[] selectionArgs) {      // potrzebne gdy jest selekcja "pole = ?"
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                rowsUpdated = sqlDB.update(PhonesTable.TABLE_PHONES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CHOSEN_ROW:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PhonesTable.TABLE_PHONES,
                            values,
                            PhonesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(PhonesTable.TABLE_PHONES,
                            values,
                            PhonesTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    private void checkColumns(String[] projection) {
        String[] available = { PhonesTable.COLUMN_BRAND, PhonesTable.COLUMN_MODEL,
                PhonesTable.COLUMN_SYSTEM_REV, PhonesTable.COLUMN_WWW, PhonesTable.COLUMN_ID };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}