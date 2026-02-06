package com.example.mainpage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoEditActivity extends AppCompatActivity {
    TextView tvId, tvPassword, tvSkinType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);

        tvId = findViewById(R.id.ID);
        tvPassword = findViewById(R.id.Password);
        tvSkinType = findViewById(R.id.skinType);

        tvId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(tvId, "아이디 변경", "새 아이디를 입력하세요");
            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(tvPassword, "비밀번호 변경", "새 비밀번호를 입력하세요");
            }
        });
        tvSkinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(tvSkinType, "피부타입 변경", "새 피부타입을 입력하세요");
            }
        });
        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoEditActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.nav_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoEditActivity.this, QrActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.nav_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoEditActivity.this, MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showEditDialog(final TextView targetView, String title, String hint) {
        final EditText editText = new EditText(this);
        editText.setHint(hint);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newText = editText.getText().toString();
                        if (!newText.isEmpty()) {
                            targetView.setText(newText);
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
