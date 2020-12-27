package com.moon.jachisekki;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    //객체 선언
    TextView title;
    TextView how;
    TextView time;
    TextView ingT;
    ImageView imgV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        //객체 초기화
        imgV = findViewById(R.id.dvPic);
        title = findViewById(R.id.dvName);
        how = findViewById(R.id.dvHow);
        time = findViewById(R.id.dvTime);
        ingT = findViewById(R.id.dvIng);

        //인텐트 받는 번들 선언
        Bundle extras = getIntent().getExtras();
        //Key값으로 각각의 데이터 값 저장
        String rname = extras.getString("name");
        String rhow = extras.getString("how");
        String rtime = extras.getString("time");
        String rIng = extras.getString("ing");
        //byteArray로 이미지 데이터 저장
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        //비트맵으로 해석
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        
        //TextView setText
        title.setText(rname);
        //rHow텍스트 내부의 \n을 줄바꿈으로 자동 변경
        how.setText(rhow.replace("\\n", System.getProperty("line.separator")));
        time.setText(rtime);
        ingT.setText(rIng);
        //이미지뷰에 사진 호출
        imgV.setImageBitmap(bitmap);
    }
}
