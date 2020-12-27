package com.moon.jachisekki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.moon.jachisekki.main.SearchActivity;
import com.moon.jachisekki.main.SectionsPagerAdapter;

//메인 액티비티
public class MainActivity extends AppCompatActivity {
    //객체 선언
    private Context mContext = MainActivity.this;
    private ViewGroup mainLayout;
    private ViewGroup viewLayout;
    private ViewGroup sideLayout;
    private Boolean isMenuShow = false;
    private Boolean isExitFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //섹션 페이지 어댑터 초기화 및 객체 초기화
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        mainLayout = findViewById(R.id.main);
        viewLayout = findViewById(R.id.fl_silde);
        sideLayout = findViewById(R.id.view_sildebar);
        Button btn_menu = findViewById(R.id.btn_sideMenu);
        //버튼 온클릭 리스너
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃 함수
                signOut();
                //로그아웃 했으므로 다시 로그인 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                //액티비티를 종료해서 오류 제거
                finish();
            }
        });
        //우측 상단 검색 버튼 온클릭 리스너
        ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다이알로그 생성
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("레시피 검색");
                dialog.setMessage("원하는 메뉴를 입력하세요");
                dialog.setIcon(R.drawable.search);
                //검색 에디트 텍스트 객체 초기화
                final EditText searchWord = new EditText(MainActivity.this);
                //힌트 설정
                searchWord.setHint("(ex. 된장찌개, 만두전골, 베이컨말이)");
                searchWord.setSingleLine();
                dialog.setView(searchWord);
                //확인 버튼 온클릭 리스너
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*** 레시피 검색 정상 작동 ***/
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        //인텐트로 검색 단어 SearchActivity로 전송
                        intent.putExtra("search", searchWord.getText().toString());
                        //SearchActivity 실행
                        startActivity(intent);

                    }
                });
                //취소 버튼 온클릭 리스너
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //다이알로그 출력
                dialog.show();
            }
        });
    }

    //파이어베이스 로그아웃 함수
    private void signOut() {
        //로그아웃(구글, 이메일 둘 다 가능)
        FirebaseAuth.getInstance().signOut();
    }

    //백버튼 이벤트 처리 함수
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //백버튼 처리 함수 호출
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Detailview 호출 함수 : RecommedRecipe에서 사용. DetailActivity로 선택한 요리의 정보 전송
    public void callDetailView(String name, String how, String time, String ing, byte[] byteArray, String uid, int count) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("how", how);
        intent.putExtra("time", time);
        intent.putExtra("ing", ing);
        intent.putExtra("image", byteArray);
        intent.putExtra("uid", uid);
        intent.putExtra("count", count);
        startActivity(intent);
    }

    //백버튼 처리 함수
    @Override
    public void onBackPressed() {
        if (isExitFlag) {
            finish();
        } else {
            isExitFlag = true;
            //백버튼 토스트
            Toast.makeText(this, "뒤로가기를 한번더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExitFlag = false;
                }
            }, 2000);
        }
    }

}