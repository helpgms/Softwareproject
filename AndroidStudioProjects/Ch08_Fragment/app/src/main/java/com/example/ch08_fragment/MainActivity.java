package com.example.ch08_fragment;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.FloatRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void setFrag1(View v){
        FragmentManager man = getSupportFragmentManager();
        FragmentTransaction ft = man.beginTransaction();

        ft.replace(R.id.frame_container, new Fragment1(),"one");
        ft.commitAllowingStateLoss();
    }
    public void setFrag2(View v){
        FragmentManager man = getSupportFragmentManager();
        FragmentTransaction ft = man.beginTransaction();

        ft.replace(R.id.frame_container, new Fragment2(),"two");
        ft.commitAllowingStateLoss();
    }
}