package com.example.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // 1. 기본정보 수정
        LinearLayout menuEdit = findViewById(R.id.menu_edit);
        menuEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, InfoEditActivity.class);
                startActivity(intent);
            }
        });

        // 2. 피부 타입 조사하기
        LinearLayout menuSkin = findViewById(R.id.menu_skin);
        menuSkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, SurveyActivity.class);
                startActivity(intent);
            }
        });

        // 3. 로그아웃
        LinearLayout menuLogout = findViewById(R.id.menu_logout);
        menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 하단 네비게이션
        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.nav_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, QrActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.nav_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPageActivity.this, "이미 마이 페이지입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
