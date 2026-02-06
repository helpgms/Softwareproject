package com.example.ch08_quiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class QuizFragment2 extends Fragment {

    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz2, container, false);

        radioGroup = view.findViewById(R.id.radio_group);
        Button nextButton = view.findViewById(R.id.btn_next);

        nextButton.setOnClickListener(v -> {
            int selected = radioGroup.getCheckedRadioButtonId();
            if (selected == -1) {
                Toast.makeText(getActivity(), "정답을 선택하세요", Toast.LENGTH_SHORT).show();
            } else {
                if (selected == R.id.radio_class) {
                    QuizData.correctAnswers.add(2);
                }

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new QuizFragment3())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
