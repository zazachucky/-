package com.moon.jachisekki.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moon.jachisekki.DetailActivity;
import com.moon.jachisekki.R;
import com.moon.jachisekki.refrigerator.ContentDTO;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity {
    private static FirebaseFirestore searchdb;
    private static FirebaseAuth mauth;
    static String searchString;
    private static ArrayList<ContentDTO> searchlist;
    private static HashMap<String, ArrayList<String>> freezHashmap;
    private static MyReAdapterSearch adapter;
    private static ArrayList<String> searchFreezeList;
    private static int count;
    public static ArrayList<String> myFreezeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        searchString = intent.getStringExtra("search");

        //검색결과 레시피 저장 리스트
        searchlist = new ArrayList<>();

        freezHashmap = new HashMap<String, ArrayList<String>>();
        //파이어스토어 객체
        searchdb = FirebaseFirestore.getInstance();

        //파이어스토어 로그인 객체
        mauth = FirebaseAuth.getInstance();

        //검색 비교할 요리 레시피의 재료 리스트
        searchFreezeList = new ArrayList<>();

        //사용자의 냉장고 재료 리스트
        myFreezeList = new ArrayList<>();


        count = 0;
        //냉장고 초기화
        initMyFreez();

        //어댑터 선언
        adapter = new MyReAdapterSearch(SearchActivity.this, searchlist);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 3));
        recyclerView.setAdapter(adapter);
    }

    //나만의 냉장고 초기화 함수 - 검색 결과를 바로 보여준다.
    public static void initMyFreez() {
        //파이어스토어 접근 (유저의 이메일이 문서 이름)
        searchdb.document("MyFreez/" + mauth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                myFreezeList.clear();
                DocumentSnapshot document = task.getResult();
                List list2 = (List) document.getData().get("freezer");
                for (int i = 0; i < list2.size(); i++) {
                    HashMap map = (HashMap) list2.get(i);
                    //냉장고 리스트에 저장
                    myFreezeList.add(map.get("name").toString());
                }
            }
            //요리 레시피 데이터 가져오기
            searchdb.collection("recipe")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            //리스트 내부 초기화
                            freezHashmap.clear();
                            searchlist.clear();
                            searchFreezeList.clear();
                            //모든 레시피 꺼내오기
                            for (QueryDocumentSnapshot doc : value) {
                                //카운트, 리스트 초기화
                                count = 0;
                                searchFreezeList.clear();
                                if (doc.get("rName") != null) {
                                    //메인 액티비티 검색 버튼으로 출력할 때
                                    if (searchString != null) {
                                        //검색 단어와 요리의 이름이 일치할 경우만 리스트에 추가.
                                        if (doc.getString("rName").equals(searchString)) {
                                            searchlist.add(0, new ContentDTO(doc.getId(),
                                                    doc.getString("rName"),
                                                    doc.getString("rIngredients"),
                                                    doc.getString("rHow"),
                                                    doc.getString("rTime"),
                                                    doc.getString("rPic"),
                                                    Integer.parseInt(doc.get("rCount").toString())));
                                        }
                                    }
                                    //레코멘디드 레시피에서 냉장고 검색 버튼으로 출력할 때
                                    else {
                                        List list3 = (List) doc.get("rFreez");
                                        for (int i = 0; i < (list3).size(); i++) {
                                            searchFreezeList.add(i, (String) list3.get(i));
                                        }
                                        int sizeOfRelist = searchFreezeList.size();
                                        int sizeOfMylist = myFreezeList.size();
                                        for (int j = 0; j < sizeOfMylist; j++) {
                                            String mystr = myFreezeList.get(j);
                                            for (int k = 0; k < sizeOfRelist; k++) {
                                                String rfStr = searchFreezeList.get(k);
                                                if (mystr.equals(rfStr))
                                                    count++;
                                            }
                                        }
                                        //만약 요리의 재료들이 내 냉장고에 모두 포함될 경우 true
                                        if (count == sizeOfRelist) {
                                            searchlist.add(0, new ContentDTO(doc.getId(),
                                                    doc.getString("rName"),
                                                    doc.getString("rIngredients"),
                                                    doc.getString("rHow"),
                                                    doc.getString("rTime"),
                                                    doc.getString("rPic"),
                                                    Integer.parseInt(doc.get("rCount").toString())));
                                        }
                                    }

                                }
                            }
                            //어답터 갱신
                            adapter.notifyDataSetChanged();
                        }
                    });
        });

    }
    //디테일 뷰 호출 함수
    public void callDetailView(String name, String how, String time, String ing, byte[] byteArray) {
        
        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("how", how);
        intent.putExtra("time", time);
        intent.putExtra("ing", ing);
        intent.putExtra("image", byteArray);
        startActivity(intent);
    }
    //어댑터 클래스 
    private class MyReAdapterSearch extends RecyclerView.Adapter<MyReAdapterSearch.MyViewHolder> {

        private ArrayList<ContentDTO> mList;
        private LayoutInflater mInflate;
        private Context mContext;
        ArrayList<ContentDTO> filteredList;

        public MyReAdapterSearch(Context context, ArrayList<ContentDTO> list) {
            this.mList = list;
            this.filteredList = list;
            this.mContext = context;
            this.mInflate = LayoutInflater.from(context);
        }

        @Override
        public MyReAdapterSearch.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflate.inflate(R.layout.item_detailview, parent, false);
            MyReAdapterSearch.MyViewHolder viewHolder = new MyReAdapterSearch.MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyReAdapterSearch.MyViewHolder holder, int position) {
            //
            holder.rName.setText(mList.get(position).getRname());
            holder.rIng.setText("재료: " + mList.get(position).getrIngrediants());
            holder.rTime.setText("시간: " + mList.get(position).getrTime());
            holder.rHow.setText("방법: " + mList.get(position).getrHow());
            Glide.with(holder.rPic.getContext())
                    .load(mList.get(position).rPic)
                    .into(holder.rPic);
        }

        //아이템 갯수 반환 함수
        @Override
        public int getItemCount() {
            return mList.size();
        }
        //뷰홀더
        public class MyViewHolder extends RecyclerView.ViewHolder {
            //객체 선언
            public TextView rName;
            public TextView rHow;
            public TextView rTime;
            public TextView rIng;
            public ImageView rPic;

            public MyViewHolder(View itemView) {
                super(itemView);
                //객체 초기화
                rName = itemView.findViewById(R.id.detailviewitem_title_textview);
                rHow = itemView.findViewById(R.id.detailviewitem_how_textview);
                rTime = itemView.findViewById(R.id.detailviewitem_time_textview);
                rIng = itemView.findViewById(R.id.detailviewitem_ing_textview);
                rPic = itemView.findViewById(R.id.detailviewitem_imageview_content);
                
                //안보이게 설정
                rHow.setVisibility(View.GONE);
                rTime.setVisibility(View.GONE);
                rIng.setVisibility(View.GONE);
                //사진 클릭 온클릭 리스너
                rPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        //사진이 파이어스토어에서 가져온 것이므로 byteArray로 변환 후 intent로 전송.
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap = ((BitmapDrawable) rPic.getDrawable()).getBitmap();
                        float scale = (float) (512 / (float) bitmap.getWidth());
                        int image_w = (int) (bitmap.getWidth() * scale);
                        int image_h = (int) (bitmap.getHeight() * scale);
                        Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        //디테일 액티비티에 데이터 전송.
                        callDetailView(rName.getText().toString(), rHow.getText().toString(), rTime.getText().toString(), rIng.getText().toString(), byteArray);
                        //조회수 증가를 위한 파이어스토어 접근
                        DocumentReference doc = searchdb.collection("recipe").document(mList.get(position).getuId());
                        //조회수++ 후  후 저장
                        int rCounter = mList.get(position).getrCount() + 1;
                        //조회수를 파이어스토어에 저장
                        doc
                                .update("rCount", rCounter)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        }
    }
}
