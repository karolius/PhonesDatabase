package com.example.user.phonedatabase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.user.phonedatabase.contentprovider.PhonesContentProvider;
import com.example.user.phonedatabase.database.PhonesTable;



/*
PhonesDetailActivity pozwala na dodanie nowego telefonu do bazy lub edycje istniejacego.
 */
public class PhonesDetailActivity extends Activity {
    private Spinner mCategory;
    //private EditText mTitleText;
    //private EditText mBodyText;

    private Button cancleButton, saveButton, goWebButton;
    private EditText brandText, modelText, systemRevText, websiteText;
    private Uri phoneUri;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.phones_edit);

        brandText = (EditText) findViewById(R.id.brandET);
        modelText = (EditText) findViewById(R.id.modelET);
        systemRevText = (EditText) findViewById(R.id.systemRevET);
        websiteText = (EditText) findViewById(R.id.websiteET);

        cancleButton = (Button) findViewById(R.id.cancelButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        goWebButton = (Button) findViewById(R.id.websiteButton);
        Bundle extras = getIntent().getExtras();

        // Sprawdz z zapisanej instancji
        phoneUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(PhonesContentProvider.CONTENT_ITEM_TYPE);

        // Lub sprawdz z przekazanaej innej aktywnosci
        if (extras != null) {
            phoneUri = extras.getParcelable(PhonesContentProvider.CONTENT_ITEM_TYPE);
            fillData(phoneUri);
        }

        // Obsluga przycisku zapisz.
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                if (TextUtils.isEmpty(mTitleText.getText().toString())) { // #TODO walidacja
//                    makeToast();
//                } else {
//                    setResult(RESULT_OK);
//                    finish();
//                }
                setResult(RESULT_OK);
                finish();
            }

        });

        // Obsluga przycisku anuluj.
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Obsluga przycisku uruchamiajacego strone internetowa z danych.
        goWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushButtonWWW();
            }
        });
    }


    private void fillData(Uri uri) {
        String[] projection = { PhonesTable.COLUMN_BRAND, PhonesTable.COLUMN_MODEL,
                PhonesTable.COLUMN_SYSTEM_REV, PhonesTable.COLUMN_WWW };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            brandText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(PhonesTable.COLUMN_BRAND)));
            modelText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(PhonesTable.COLUMN_MODEL)));
            systemRevText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(PhonesTable.COLUMN_SYSTEM_REV)));
            websiteText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(PhonesTable.COLUMN_WWW)));

            cursor.close();
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(PhonesContentProvider.CONTENT_ITEM_TYPE, phoneUri);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }


    private void saveState() {
        String brand = brandText.getText().toString();
        String model = modelText.getText().toString();
        String systemRev = systemRevText.getText().toString();
        String website = websiteText.getText().toString();
        // Zapisz tylko gdy pola nie sa puste.
        if (brand.length() == 0 && model.length() == 0
                && systemRev.length() == 0 && website.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PhonesTable.COLUMN_BRAND, brand);
        values.put(PhonesTable.COLUMN_MODEL, model);
        values.put(PhonesTable.COLUMN_SYSTEM_REV, systemRev);
        values.put(PhonesTable.COLUMN_WWW, website);

        if (phoneUri == null) {
            // Nowy telefon
            phoneUri = getContentResolver().insert(
                    PhonesContentProvider.CONTENT_URI, values);
        } else {
            // Edycja istniejacego telefonu
            getContentResolver().update(phoneUri, values, null, null);
        }
    }


    private void makeToast() {
        Toast.makeText(PhonesDetailActivity.this, getString(R.string.fieldError),
                Toast.LENGTH_SHORT).show();
    }


    private void pushButtonWWW() {
        if (!TextUtils.isEmpty(websiteText.getText().toString())) {
            String address = websiteText.getText().toString();
            if (!address.startsWith("http://") && !address.startsWith("https://"))
                address = "http://" + address;
            Intent webBrowserIntent = new Intent("android.intent.action.VIEW", Uri.parse(address));
            startActivity(webBrowserIntent);
        } else {
            makeToast();
        }
    }
}