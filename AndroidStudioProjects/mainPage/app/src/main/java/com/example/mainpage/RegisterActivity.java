package com.example.mainpage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText editName, editId, editPassword;
    Button btnRegister;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.checkAndCopyDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "DB 복사 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String id = editId.getText().toString().trim();
                String password = editPassword.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = null;
                Cursor cursor = null;
                try {
                    db = dbHelper.openDatabase();

                    cursor = db.rawQuery("SELECT * FROM user WHERE ID=?", new String[]{id});
                    if (cursor.moveToFirst()) {
                        Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        boolean inserted = dbHelper.insertUser(id, name, password);
                        if (inserted) {
                            Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "DB 에러: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (cursor != null) cursor.close();
                    if (db != null) db.close();
                }
            }
        });
    }
}
