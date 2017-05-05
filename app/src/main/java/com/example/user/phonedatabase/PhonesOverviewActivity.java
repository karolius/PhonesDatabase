package com.example.user.phonedatabase;



import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.user.phonedatabase.contentprovider.PhonesContentProvider;
import com.example.user.phonedatabase.database.PhonesTable;


/*
PhonesOverviewActivity wyswietla istniejace telefony z bazy w liscie.
Telefon mozna dodac poprzez klikniecie w przycisk na ActionBar lub usunac
klikajac na dany element z listy.
#TODO dodaj tez edytowanie i usuwanie wielu na raz
 */
public class PhonesOverviewActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
    
    
    // Metoda jest wywolyywaniagdy aktywnosc jest uruchamiana
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phones_list);
        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
    }
    
    
    // Tworzy menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return true;
    }

    // TODO dodaj wybieranie wielu
    // Reakcje na wybor w menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                createPhonesDatabase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                Uri uri = Uri.parse(PhonesContentProvider.CONTENT_URI + "/" + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createPhonesDatabase() {
        Intent detailView = new Intent(this, PhonesDetailActivity.class);
        startActivity(detailView);
    }

    
    // Jesli kliknieto w dany element otwiera dla niego widok PhonesDetailActivity 
    @Override
    protected void onListItemClick(ListView lV, View v, int position, long id) {
        super.onListItemClick(lV, v, position, id);
        Intent detailView = new Intent(this, PhonesDetailActivity.class);
        Uri phonesDbUri = Uri.parse(PhonesContentProvider.CONTENT_URI + "/" + id);
        detailView.putExtra(PhonesContentProvider.CONTENT_ITEM_TYPE, phonesDbUri);
        startActivity(detailView);
    }


    private void fillData() {
        // Utworzenie mapowania między kolumnami tabeli, a kolumnami wyświetlanej listy
        String[] mapFrom = new String[] { PhonesTable.COLUMN_BRAND, PhonesTable.COLUMN_MODEL };
        // TODO int[] to = new int[]{R.id.label};
        int[] mapTo = new int[] { R.id.phoneBrandTV, R.id.phoneModelTV };
        // Adapter wymaga aby w wyniku zapytania znajdowala sie kolumna _id stad
        // ustawienia aktywnosci listy nowego adaptera jako źródła danych.
        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.phones_list_row, null, mapFrom, mapTo, 0);
        setListAdapter(adapter);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }


    // Tworzy nowy loader po wywolaniu initLoader()
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {PhonesTable.COLUMN_ID,
                PhonesTable.COLUMN_BRAND, PhonesTable.COLUMN_MODEL };
        CursorLoader cursorLoader = new CursorLoader(this,
                PhonesContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

}