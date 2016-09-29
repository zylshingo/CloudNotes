package com.jikexueyuan.cloudnotes.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

    public static final Uri uri = Uri.parse("content://com.jikexueyuan.cloudnotes");
    SQLiteDatabase database;

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return database.delete("notes",selection,selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        database.insert("notes","_id",values);
        return uri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        database = getContext().openOrCreateDatabase("cloudNotes.db3", Context.MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS notes(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "objectId CHAR(20),time CHAR(20),post TEXT,title CHAR(255),UNIQUE (title))");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = database.query("notes",projection,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        // TODO: Implement this to handle requests to update one or more rows.
        return database.update("notes",values,selection,selectionArgs);
    }
}
