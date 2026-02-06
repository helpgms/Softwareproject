package com.example.ch08_quiz;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class QuizFragment3 extends Fragment {

    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz3, container, false);

        radioGroup = view.findViewById(R.id.radio_group);
        Button submitButton = view.findViewById(R.id.btn_submit);

        submitButton.setOnClickListener(v -> {
            int selected = radioGroup.getCheckedRadioButtonId();

            if (selected == -1) {
                Toast.makeText(getActivity(), "정답을 선택하세요", Toast.LENGTH_SHORT).show();
            } else {
                if (selected == R.id.radio_activity) {
                    QuizData.correctAnswers.add(3);
                }

                showResult();
            }
        });

        return view;
    }

    private void showResult() {
        int count = QuizData.correctAnswers.size();
        StringBuilder builder = new StringBuilder();
        builder.append("3문제 중 ").append(count).append("문제 맞췄습니다.\n");

        if (count > 0) {
            builder.append("맞춘 문제 번호: ");
            for (int i = 0; i < count; i++) {
                builder.append(QuizData.correctAnswers.get(i));
                if (i < count - 1) builder.append(", ");
            }
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("결과")
                .setMessage(builder.toString())
                .setPositiveButton("확인", null)
                .show();

        // 결과 후 초기화 (원하면)
        // QuizData.correctAnswers.clear();
    }
}
