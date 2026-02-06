package com.example.manseong;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void generateRandomNumber(View view){
        TextView textViewRandomNumber = findViewById(R.id.textViewRandomNumber);
        //Random random = new Random();
        int randomNumber = new Random().nextInt(100);

        textViewRandomNumber.setText("난수 : " + randomNumber);
    }
}