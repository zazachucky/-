package com.moon.jachisekki.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moon.jachisekki.AddNotepadActivity;
import com.moon.jachisekki.AddtoRefrigeratorActivity;
import com.moon.jachisekki.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class Notepad extends Fragment implements View.OnClickListener  {    // 장보러 갈 때 살 품목을 체크할 수 있는 메모를 제공해주는 기능
    int max = 0;                        // Total Ingredients count
    int count = 0;                      // Count Checked items
    public static String data = "";     // Name of Ingredients
    private View view;                  // For Fragment
    private SharedPreferences pre;      // For Restoring
    private String SharedPreFile = "com.moon.jachisekki.main";
    String[] txtArr;                    // For Split Data

    LinearLayout nl = null;             // Notepad Linear layout
    CheckBox[] cb;                      // Dynamic assignment Check box
    SeekBar sb;                         // Seek bar

    TextView max_txt;                   // Quantity display [count / max]
    TextView count_txt;                 //              ex) [3 / 10]

    FloatingActionButton btn_add, btn_delete ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_notepad, container, false);       // Fragment를 사용하기위한 view 선언
        pre = this.getActivity().getSharedPreferences(SharedPreFile, Context.MODE_PRIVATE);     // 자동복원을 위한 SharedPreferences 선언

        return view;
    }

    @Override
    public void onStart() {             // onCreateView가 끝난 후 실행
        super.onStart();
        init(view);
    }

    public void init(View view)         // 메모기능 핵심함수
    {
        sb = view.findViewById(R.id.seek_bar);
        max_txt = view.findViewById(R.id.text_max);
        count_txt = view.findViewById(R.id.text_count);

        sb.setMax(0);                                // 초기화
        sb.setProgress(0);
        count_txt.setText("0");
        max_txt.setText("/" + Integer.toString(0));

        for (int i = 0; i < max; i++)               // 매 선택 시 기존의 존재했던 체크박스 삭제
            nl.removeView(cb[i]);

        txtArr = new String[max];
        if (!data.equals("")) {                    // AddNotepad에서 받아온 재료들이 있을 경우
            txtArr = data.split(",");       // ","을 기준으로 분할
            max = txtArr.length;                  // 받아온 재료의 갯수 만큼 max 초기화
        }
        else
            max = 0;

        nl = view.findViewById(R.id.notepad);
        cb = new CheckBox[max];

        for (int i = 0; i < max; i++)
        {
            cb[i] = new CheckBox(view.getContext());    // 재료의 갯수만큼 체크박스 생성
            cb[i].setOnClickListener(this);
            cb[i].setText(txtArr[i]);                   // 각 재료의 이름으로 체크박스 텍스트 지정
            nl.addView(cb[i]);                          // Linear Layout에 차례로 추가
        }

        sb.setMax(max);                                 // Seek Bar의 최대값을 재료의 갯수만큼 지정
        max_txt.setText("/" + Integer.toString(max));

        btn_add = view.findViewById(R.id.Fab);          // 재료 추가버튼
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {            // 누를 시 재료목록을 보여주는 인텐트로 이동
                Intent intent = new Intent(view.getContext(), AddNotepadActivity.class);
                intent.putExtra("Key", data);
                startActivity(intent);
            }
        });

        btn_delete = view.findViewById(R.id.Fab_deleteAll); // 메모 삭제버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                // 누를 시 현재 보이던 체크박스를 모두 삭제
                data = "";
                sb.setProgress(0);
                count_txt.setText("0");
                max_txt.setText("/" + Integer.toString(0));
                for (int i = 0; i < max; i++)
                {
                    nl.removeView(cb[i]);
                }
            }
        });
    }

    @Override
    public void onPause(){      // 일시정지, 실행이 종료되기 전
        super.onPause();

        SharedPreferences.Editor preEditor = pre.edit();    // Shared Preferences를 사용하여
        preEditor.commit();                                 // 기존의 메모가 유지되도록 기능
    }

    @Override
    public void onClick(View v) {           // Check 버튼 클릭 시
        for (int i = 0; i < max; i++)
        {
            if(cb[i].isChecked())
                count += 1;
        }
        sb.setProgress(count);              // 눌려져 있는 체크박스의 갯수 만큼 진행도를 표현
        count_txt.setText(Integer.toString(count));
        count = 0;
    }
}