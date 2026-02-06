package com.example.mainpage;

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

public class LoginActivity extends AppCompatActivity {

    EditText editId, editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        try {
            dbHelper.checkAndCopyDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "DB 복사 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString().trim();
                String password = editPassword.getText().toString();

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = null;
                Cursor cursor = null;
                try {
                    db = dbHelper.openDatabase();
                    String query = "SELECT * FROM user WHERE ID=? AND Password=?";
                    cursor = db.rawQuery(query, new String[]{id, password});

                    if (cursor.moveToFirst()) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "DB 에러: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (cursor != null) cursor.close();
                    if (db != null) db.close();
                }
            }
        });

    }
}
