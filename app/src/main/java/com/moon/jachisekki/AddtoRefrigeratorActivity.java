package com.moon.jachisekki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moon.jachisekki.main.MyRefrigerator;
import com.moon.jachisekki.refrigerator.RefItem;
import com.moon.jachisekki.refrigerator.RefViewer;

public class AddtoRefrigeratorActivity extends AppCompatActivity {
    private boolean[][] flag;
    /*** 중복상태를 저장할 2차원 배열 ***/
    //객체 초기화
    private GridView gridView1, gridView2, gridView3, gridView4, gridView5;
    private RefAdapter refAdapter1, refAdapter2, refAdapter3, refAdapter4, refAdapter5;
    Bundle bundle;
    Fragment mf;
    Map<Integer, String> arFreez;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FragmentTransaction ft;
    //어댑터 클래스 정의
    class RefAdapter extends BaseAdapter {
        ArrayList<RefItem> items = new ArrayList<RefItem>();

        @Override
        public int getCount() {
            return items.size();
        }
        //아이템을 저장하는 함수
        public void addItem(RefItem refItem) {
            items.add(refItem);
        }

        @Override
        public RefItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RefViewer refViewer = new RefViewer(getApplicationContext());
            refViewer.setItem(items.get(i));
            return refViewer;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //객체 초기화
        flag = new boolean[5][10]; /*** 배열 선언 5 * 10 ***/
        mf = getSupportFragmentManager().findFragmentById(R.id.fragment_ref);
        ft = getSupportFragmentManager().beginTransaction();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bundle = new Bundle();
        arFreez = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        db = FirebaseFirestore.getInstance();

        /***어댑터 초기화***/
        refAdapter1 = new RefAdapter();
        setAdapter(1);
        refAdapter2 = new RefAdapter();
        setAdapter(2);
        refAdapter3 = new RefAdapter();
        setAdapter(3);
        refAdapter4 = new RefAdapter();
        setAdapter(4);
        refAdapter5 = new RefAdapter();
        setAdapter(5);
        /*** 중복 상태 점검 ***/
        initFlags();
        setGridViews();
        setGridViewLister();
    }
    
    /*** 인텐트로 받은 데이터를 바탕으로 이미 추가된 재료 체크하는 함수 ***/
    private void initFlags() {
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                flag[i][j] = false;
            }
        }

        for (int i = 0; i < 10; i++) {
            if (data.contains(refAdapter1.getItem(i).getName())) {
                flag[0][i] = true;
            }
            if (data.contains(refAdapter2.getItem(i).getName())) {
                flag[1][i] = true;
            }
            if (data.contains(refAdapter3.getItem(i).getName())) {
                flag[2][i] = true;
            }
            if (data.contains(refAdapter4.getItem(i).getName())) {
                flag[3][i] = true;
            }
            if (data.contains(refAdapter5.getItem(i).getName())) {
                flag[4][i] = true;
            }
        }
    }
    
    //어댑터 세팅 함수
    private void setAdapter(int num) {
        if (num == 1) {
            refAdapter1.addItem(new RefItem("대파", R.drawable.pa));
            refAdapter1.addItem(new RefItem("양배추", R.drawable.cabbage));
            refAdapter1.addItem(new RefItem("당근", R.drawable.carrot));
            refAdapter1.addItem(new RefItem("다진 마늘", R.drawable.chopped_garlic));
            refAdapter1.addItem(new RefItem("오이", R.drawable.cucumber));
            refAdapter1.addItem(new RefItem("콩나물", R.drawable.kong));
            refAdapter1.addItem(new RefItem("무", R.drawable.mu));
            refAdapter1.addItem(new RefItem("양파", R.drawable.onion));
            refAdapter1.addItem(new RefItem("팽이버섯", R.drawable.pengei_mushroom));
            refAdapter1.addItem(new RefItem("감자", R.drawable.potato));
            gridView1 = findViewById(R.id.gridView1);
            gridView1.setAdapter(refAdapter1);
        } else if (num == 2) {
            gridView2 = (GridView) findViewById(R.id.gridView2);
            refAdapter2 = new RefAdapter();
            refAdapter2.addItem(new RefItem("베이컨", R.drawable.bacon));
            refAdapter2.addItem(new RefItem("소고기     (국거리)", R.drawable.beef_gukgeori));
            refAdapter2.addItem(new RefItem("소고기     (구이용)", R.drawable.beef_roast));
            refAdapter2.addItem(new RefItem("생닭", R.drawable.chicken));
            refAdapter2.addItem(new RefItem("닭가슴살", R.drawable.chicken_breast));
            refAdapter2.addItem(new RefItem("계란", R.drawable.egg));
            refAdapter2.addItem(new RefItem("돼지고기   (국거리)", R.drawable.pork_gukgoeri));
            refAdapter2.addItem(new RefItem("돼지고기   (구이용)", R.drawable.pork_roast));
            refAdapter2.addItem(new RefItem("소세지", R.drawable.sausage));
            refAdapter2.addItem(new RefItem("스팸", R.drawable.spam));
            gridView2.setAdapter(refAdapter2);
        } else if (num == 3) {
            gridView3 = (GridView) findViewById(R.id.gridView3);
            refAdapter3 = new RefAdapter();
            refAdapter3.addItem(new RefItem("멸치", R.drawable.anchovy));
            refAdapter3.addItem(new RefItem("바지락", R.drawable.bajilag));
            refAdapter3.addItem(new RefItem("게", R.drawable.crab));
            refAdapter3.addItem(new RefItem("갈치", R.drawable.galchi));
            refAdapter3.addItem(new RefItem("김", R.drawable.gim));
            refAdapter3.addItem(new RefItem("고등어", R.drawable.godunger));
            refAdapter3.addItem(new RefItem("연어", R.drawable.salmon));
            refAdapter3.addItem(new RefItem("미역", R.drawable.seaweed));
            refAdapter3.addItem(new RefItem("새우", R.drawable.shirimp));
            refAdapter3.addItem(new RefItem("오징어", R.drawable.squid));
            gridView3.setAdapter(refAdapter3);
        } else if (num == 4) {
            gridView4 = (GridView) findViewById(R.id.gridView4);
            refAdapter4 = new RefAdapter();
            refAdapter4.addItem(new RefItem("김치", R.drawable.kimchi));
            refAdapter4.addItem(new RefItem("만두", R.drawable.mandu));
            refAdapter4.addItem(new RefItem("우유", R.drawable.milk));
            refAdapter4.addItem(new RefItem("모짜렐라   치즈", R.drawable.mozza_cheese));
            refAdapter4.addItem(new RefItem("밥", R.drawable.rice));
            refAdapter4.addItem(new RefItem("슬라이스   치즈", R.drawable.slice_cheese));
            refAdapter4.addItem(new RefItem("소면", R.drawable.somyeon));
            refAdapter4.addItem(new RefItem("스파게티", R.drawable.spaghetti));
            refAdapter4.addItem(new RefItem("두부", R.drawable.topo));
            refAdapter4.addItem(new RefItem("참치캔", R.drawable.tuna));
            gridView4.setAdapter(refAdapter4);
        } else {
            gridView5 = (GridView) findViewById(R.id.gridView5);
            refAdapter5 = new RefAdapter();
            refAdapter5.addItem(new RefItem("참기름", R.drawable.cham_oil));
            refAdapter5.addItem(new RefItem("후추", R.drawable.huchu2));
            refAdapter5.addItem(new RefItem("다시다", R.drawable.dasida));
            refAdapter5.addItem(new RefItem("된장", R.drawable.doenjang));
            refAdapter5.addItem(new RefItem("액젓", R.drawable.fish_sauce));
            refAdapter5.addItem(new RefItem("고춧가루", R.drawable.gochu_powder));
            refAdapter5.addItem(new RefItem("고추장", R.drawable.gochujang));
            refAdapter5.addItem(new RefItem("소금", R.drawable.salt2));
            refAdapter5.addItem(new RefItem("간장", R.drawable.soybob));
            refAdapter5.addItem(new RefItem("설탕", R.drawable.sugar));
            gridView5.setAdapter(refAdapter5);
        }
    }
    //그리드뷰 설정 함수
    private void setGridViews() {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        gridView1.measure(0, expandSpec);
        gridView1.getLayoutParams().height = gridView1.getMeasuredHeight();
        gridView2.measure(0, expandSpec);
        gridView2.getLayoutParams().height = gridView1.getMeasuredHeight();
        gridView3.measure(0, expandSpec);
        gridView3.getLayoutParams().height = gridView1.getMeasuredHeight();
        gridView4.measure(0, expandSpec);
        gridView4.getLayoutParams().height = gridView1.getMeasuredHeight();
        gridView5.measure(0, expandSpec);
        gridView5.getLayoutParams().height = gridView1.getMeasuredHeight();
    }

    //그리드뷰 리스너
    private void setGridViewLister() {
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /***여기서 데이터베이스 처리***/
                /***********************************************************************/
                if (flag[0][position]) {
                    /*** 데이터 베이스에 삭제 ***/
                    MyRefrigerator.removeItem(refAdapter1.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "delete", Toast.LENGTH_SHORT).show();
                    flag[0][position] = !flag[0][position];
                } else {
                    /*** 데이터 베이스에 추가 ***/
                    MyRefrigerator.addItem(refAdapter1.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "add", Toast.LENGTH_SHORT).show();
                    flag[0][position] = !flag[0][position];
                }
            }
        });
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /***********************************************************************/
                if (flag[1][position]) {
                    /*** 데이터 베이스에 삭제 ***/
                    MyRefrigerator.removeItem(refAdapter2.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "delete", Toast.LENGTH_SHORT).show();
                    flag[1][position] = !flag[1][position];
                } else {
                    /*** 데이터 베이스에 추가 ***/
                    MyRefrigerator.addItem(refAdapter2.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "add", Toast.LENGTH_SHORT).show();
                    flag[1][position] = !flag[1][position];
                }
            }
        });
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /***********************************************************************/
                if (flag[2][position]) {
                    /*** 데이터 베이스에 삭제 ***/
                    MyRefrigerator.removeItem(refAdapter3.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "delete", Toast.LENGTH_SHORT).show();
                    flag[2][position] = !flag[2][position];
                } else {
                    /*** 데이터 베이스에 추가 ***/
                    MyRefrigerator.addItem(refAdapter3.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "add", Toast.LENGTH_SHORT).show();
                    flag[2][position] = !flag[2][position];
                }
            }
        });
        gridView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /***********************************************************************/
                if (flag[3][position]) {
                    /*** 데이터 베이스에 삭제 ***/
                    MyRefrigerator.removeItem(refAdapter4.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "delete", Toast.LENGTH_SHORT).show();
                    flag[3][position] = !flag[3][position];
                } else {
                    /*** 데이터 베이스에 추가 ***/
                    MyRefrigerator.addItem(refAdapter4.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "add", Toast.LENGTH_SHORT).show();
                    flag[3][position] = !flag[3][position];
                }
            }
        });
        gridView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /***********************************************************************/
                if (flag[4][position]) {
                    /*** 데이터 베이스에 삭제 ***/
                    MyRefrigerator.removeItem(refAdapter5.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "delete", Toast.LENGTH_SHORT).show();
                    flag[4][position] = !flag[4][position];
                } else {
                    /*** 데이터 베이스에 추가 ***/
                    MyRefrigerator.addItem(refAdapter5.getItem(position));
                    Toast.makeText(AddtoRefrigeratorActivity.this, "add", Toast.LENGTH_SHORT).show();
                    flag[4][position] = !flag[4][position];
                }
            }
        });

    }
    //왼쪽 상단 백버튼 처리 함수
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_goBack:
                finish();
                break;
        }
    }
}
