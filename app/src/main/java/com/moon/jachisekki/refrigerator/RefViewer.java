package com.moon.jachisekki.refrigerator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.moon.jachisekki.R;

//RefItem Viewer Layout
public class RefViewer extends LinearLayout {

    TextView textView;
    ImageView imageView;
    public RefViewer(Context context) {
        super(context);

        init(context);
    }

    public RefViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }
    //초기화 함수
    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ref_item, this, true);

        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);
    }
    //아이템 설정함수
    public void setItem(RefItem singerItem){
        textView.setText(singerItem.getName());
        imageView.setImageResource(singerItem.getImage());
    }
}

