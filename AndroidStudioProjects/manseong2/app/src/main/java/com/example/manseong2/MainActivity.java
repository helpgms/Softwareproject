package com.example.manseong2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edit1, edit2;
    private TextView result;
    private Button btnAdd, btnMin, btnMul, btnHal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 연결
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2);
        result = findViewById(R.id.result);
        btnAdd = findViewById(R.id.btnAdd);
        btnMin = findViewById(R.id.btnMin);
        btnMul = findViewById(R.id.btnMul);
        btnHal = findViewById(R.id.btnHal);

        // 버튼 클릭 이벤트 추가
        btnAdd.setOnClickListener(v -> calculate('+'));
        btnMin.setOnClickListener(v -> calculate('-'));
        btnMul.setOnClickListener(v -> calculate('*'));
        btnHal.setOnClickListener(v -> calculate('/'));
    }

    // 사칙연산 계산 함수
    private void calculate(char operator) {
        String num1 = edit1.getText().toString().trim();
        String num2 = edit2.getText().toString().trim();

        // 입력값 확인
        if (num1.isEmpty() || num2.isEmpty()) {
            showToast("값을 입력하세요!");
            return;
        }

        double number1, number2;
        try {
            number1 = Double.parseDouble(num1);
            number2 = Double.parseDouble(num2);
        } catch (NumberFormatException e) {
            showToast("숫자를 입력하세요!");
            return;
        }

        double calcResult = 0;

        switch (operator) {
            case '+':
                calcResult = number1 + number2;
                break;
            case '-':
                calcResult = number1 - number2;
                break;
            case '*':
                calcResult = number1 * number2;
                break;
            case '/':
                if (number2 == 0) {
                    showToast("0으로 나눌 수 없습니다!");
                    return;
                }
                calcResult = number1 / number2;
                break;
        }

        result.setText("Result: " + calcResult);
    }

    // 토스트 메시지 표시 함수
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
