package com.example.mainpage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper {
    String DB_NAME = "mobileprograming.db";
    private final Context context;
    private final String dbPath;

    public DatabaseHelper(Context context) {
        this.context = context;
        this.dbPath = context.getDatabasePath(DB_NAME).getPath();
    }

    public void checkAndCopyDatabase() throws IOException {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            copyDatabase();
        }
    }

    private void copyDatabase() throws IOException {
        InputStream is = context.getAssets().open(DB_NAME);
        File dbDir = new File(context.getDatabasePath(DB_NAME).getParent());
        if (!dbDir.exists()) dbDir.mkdirs();

        OutputStream os = new FileOutputStream(dbPath);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        os.flush();
        os.close();
        is.close();
    }

    public SQLiteDatabase openDatabase() throws SQLiteException {
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public boolean insertUser(String id, String name, String password) {
        SQLiteDatabase db = openDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ID", id);
        values.put("Password", password);
        long result = db.insert("user", null, values);
        return result != -1;
    }
}
