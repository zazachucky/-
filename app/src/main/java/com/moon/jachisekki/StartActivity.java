package com.moon.jachisekki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//처음 시작 액티비티
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //시작 액티비티는 시작 화면 보여주고 2초 뒤 로그인 액티비티로 이동
        try {
            Thread.sleep(2000);
            //로그인 액티비티 인텐트 저장
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}