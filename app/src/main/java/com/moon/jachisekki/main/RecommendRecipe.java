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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.moon.jachisekki.MainActivity;
import com.moon.jachisekki.R;
import com.moon.jachisekki.refrigerator.ContentDTO;
import com.moon.jachisekki.refrigerator.RefItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

//메인 페이지인 RecommendRecipe 프래그먼트
public class RecommendRecipe extends Fragment {

    public RecommendRecipe() {
        // Required empty public constructor
    }
    //객체 선언
    public FirebaseAuth fauth;
    private MyReAdapter2 adapter_rec;
    private MyReAdapter2 adapter_ran;
    private ArrayList<ContentDTO> list;
    private ArrayList<ContentDTO> list_ran;
    public ArrayList<RefItem> flist;
    Button searchButton;
    FirebaseFirestore db;
    public static RefAdapter3 adapter3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        //객체 초기화
        list = new ArrayList<>();
        list_ran = new ArrayList<>();
        flist = new ArrayList<>();
        fauth = FirebaseAuth.getInstance();
        //냉장고로 검색 버튼 온클릭 리스너
        searchButton = view.findViewById(R.id.btn_search_with_ingredients);
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //검색 액티비티 호출
                        Intent intent = new Intent(view.getContext(), SearchActivity.class);
                        startActivity(intent);
                    }
                }
        );
        //데이터베이스 연결 후 레시피 모두 가져오기
        db = FirebaseFirestore.getInstance();

        readAllRecipe();
        readUserFreez();
        
        //조회수 순으로 출력하는 리사이클러뷰
        adapter_rec = new MyReAdapter2(getActivity(), list);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recommendviewfragment_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        recyclerView.setAdapter(adapter_rec);
        Collections.reverse(list);
        adapter_rec.notifyDataSetChanged();


        //랜덤으로 추천하는 리사이클러뷰
        adapter_ran = new MyReAdapter2(getActivity(), list_ran);
        RecyclerView recyclerView_ran = (RecyclerView) view.findViewById(R.id.recommendviewfragment_recyclerview_random);
        recyclerView_ran.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        recyclerView_ran.setAdapter(adapter_ran);
        adapter_ran.notifyDataSetChanged();


        //냉장고 재료 출력 리사이클러뷰
        adapter3 = new RefAdapter3(getActivity(), flist);
        RecyclerView recyclerView_freez = (RecyclerView) view.findViewById(R.id.recommend_rv);
        recyclerView_freez.setLayoutManager(new GridLayoutManager(view.getContext(), 5));
        recyclerView_freez.setAdapter(adapter3);
        adapter3.notifyDataSetChanged();
        return view;
    }

    //모든 레시피를 가져오는 함수
    public void readAllRecipe() {
        db.collection("recipe")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        //리스트들 초기화
                        list.clear();
                        list_ran.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("rName") != null) {
                                //기본 리스트에 저장.
                                list.add(
                                        new ContentDTO(doc.getId(),
                                                doc.getString("rName"),
                                                doc.getString("rIngredients"),
                                                doc.getString("rHow"),
                                                doc.getString("rTime"),
                                                doc.getString("rPic"),
                                                Integer.parseInt(doc.get("rCount").toString())));
                                //랜덤 추천 리스트에 저장.
                                list_ran.add(
                                        new ContentDTO(doc.getId(),
                                                doc.getString("rName"),
                                                doc.getString("rIngredients"),
                                                doc.getString("rHow"),
                                                doc.getString("rTime"),
                                                doc.getString("rPic"),
                                                Integer.parseInt(doc.get("rCount").toString())));
                            }
                        }
                        //어답터 갱신
                        Collections.sort(list, new Comparator<ContentDTO>() {
                            @Override
                            public int compare(ContentDTO o1, ContentDTO o2) {
                                if (o1.getrCount() > o2.getrCount()) {
                                    return 1;
                                } else if (o1.getrCount() < o2.getrCount()) {
                                    return -1;
                                } else
                                    return 0;
                            }
                        });
                        //리스트 내림차순으로 정렬.
                        Collections.reverse(list);
                        //랜덤을 위한 난수 선언
                        long seed = System.nanoTime();
                        //랜덤 리스트를 셔플.
                        Collections.shuffle(list_ran, new Random(seed));
                        //일정한 갯수만 출력.
                        for (int i = list_ran.size() - 7; i >= 0; i--) {
                            list_ran.remove(i);
                        }
                        //데이터 변경 알림
                        adapter_rec.notifyDataSetChanged();
                        adapter_ran.notifyDataSetChanged();
                    }
                });
    }
    //유저 냉장고 재료 가져오는 함수
    public void readUserFreez() {
        //냉장고 재료 가져오기
        db.document("MyFreez/" + fauth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                //만약 아직 유저가 냉장고 데이터가 없을 경우
                if (document.getData() == null) {
                    //유저 냉장고 추가 함수 호출
                    addNewUser();
                } else {
                    if (document.getData().get("freezer") != null) {
                        //freezer를 리스트로 복사
                        List list2 = (List) document.getData().get("freezer");
                        flist.clear();
                        for (int i = 0; i < list2.size(); i++) {
                            //아이템이 이름(Stirng), 이미지 리소스값(int)로 들어가 있으므로
                            //해쉬맵으로 받아 리스트에 저장.
                            HashMap map = (HashMap) list2.get(i);
                            //리스트에 저장.
                            flist.add(new RefItem(map.get("name").toString(), Integer.parseInt(map.get("image").toString())));
                        }
                        adapter3.notifyDataSetChanged();
                    }
                }

            }
        });
    }
    //신규 유저일 경우 처리 함수
    public void addNewUser() {
        DocumentReference dref = db.collection("MyFreez").document(fauth.getCurrentUser().getEmail());
        Map<String, Object> data1 = new HashMap<>();
        //아직 freezer에 리스트가 없으므로 데이터를 설정한다.
        data1.put("freezer", flist);
        dref.set(data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
    //어댑터 초기화(SearchActivity의 어댑터들과 동일함.)
    private class MyReAdapter2 extends RecyclerView.Adapter<MyReAdapter2.MyViewHolder> {

        private ArrayList<ContentDTO> mList;
        private LayoutInflater mInflate;
        private Context mContext;
        ArrayList<ContentDTO> filteredList;

        public MyReAdapter2(Context context, ArrayList<ContentDTO> list) {
            this.mList = list;
            this.filteredList = list;
            this.mContext = context;
            this.mInflate = LayoutInflater.from(context);
        }

        @Override
        public MyReAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflate.inflate(R.layout.item_detailview, parent, false);
            MyReAdapter2.MyViewHolder viewHolder = new MyReAdapter2.MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyReAdapter2.MyViewHolder holder, int position) {

            holder.rName.setText(mList.get(position).getRname());
            holder.rIng.setText("재료: " + mList.get(position).getrIngrediants());
            holder.rTime.setText("시간: " + mList.get(position).getrTime());
            holder.rHow.setText("방법: " + mList.get(position).getrHow());
            holder.rUid.setText(mList.get(position).getuId());
            holder.rCount.setText("조회수 : " + mList.get(position).getrCount());
            Glide.with(holder.rPic.getContext())
                    .load(mList.get(position).rPic)
                    .into(holder.rPic);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView rName;
            public TextView rHow;
            public TextView rTime;
            public TextView rIng;
            public TextView rUid;
            public ImageView rPic;
            public TextView rCount;

            public MyViewHolder(View itemView) {
                super(itemView);

                rName = itemView.findViewById(R.id.detailviewitem_title_textview);
                rHow = itemView.findViewById(R.id.detailviewitem_how_textview);
                rTime = itemView.findViewById(R.id.detailviewitem_time_textview);
                rIng = itemView.findViewById(R.id.detailviewitem_ing_textview);
                rPic = itemView.findViewById(R.id.detailviewitem_imageview_content);
                rUid = itemView.findViewById(R.id.detailviewitem_uid);
                rCount = itemView.findViewById(R.id.detailviewitem_count_textview);

                rHow.setVisibility(View.GONE);
                rTime.setVisibility(View.GONE);
                rIng.setVisibility(View.GONE);
                rUid.setVisibility(View.GONE);
                rPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap = ((BitmapDrawable) rPic.getDrawable()).getBitmap();
                        float scale = (float) (512 / (float) bitmap.getWidth());
                        int image_w = (int) (bitmap.getWidth() * scale);
                        int image_h = (int) (bitmap.getHeight() * scale);
                        Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        int rCounter = mList.get(position).getrCount() + 1;
                        ((MainActivity) getActivity()).callDetailView(rName.getText().toString(), rHow.getText().toString(), rTime.getText().toString(), rIng.getText().toString(), byteArray, rUid.getText().toString(), rCounter);
                        DocumentReference doc = db.collection("recipe").document(mList.get(position).getuId());

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

                        Collections.sort(list, new Comparator<ContentDTO>() {
                            @Override
                            public int compare(ContentDTO o1, ContentDTO o2) {
                                if (o1.getrCount() > o2.getrCount()) {
                                    return 1;
                                } else if (o1.getrCount() < o2.getrCount()) {
                                    return -1;
                                } else
                                    return 0;
                            }
                        });
                        Collections.reverse(list);
                        adapter_rec.notifyDataSetChanged();
                    }
                });
            }
        }


    }

    //냉장고 아이템 어댑터
    class RefAdapter3 extends RecyclerView.Adapter<RefAdapter3.MyViewHolder> {

        private ArrayList<RefItem> mList2;
        private LayoutInflater mInflate2;
        private Context mContext;
        ArrayList<RefItem> filteredList2;

        public RefAdapter3(Context context, ArrayList<RefItem> list) {
            this.mList2 = list;
            this.filteredList2 = list;
            this.mContext = context;
            this.mInflate2 = LayoutInflater.from(context);
        }

        @Override
        public RefAdapter3.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflate2.inflate(R.layout.ref_item, parent, false);
            RefAdapter3.MyViewHolder viewHolder = new RefAdapter3.MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RefAdapter3.MyViewHolder holder, int position) {
            //리스트에서 이름과 이미지 데이터 가져오기
            holder.iName2.setText(((RefItem) mList2.get(position)).getName());
            holder.iPic2.setImageResource(((RefItem) mList2.get(position)).getImage());
        }

        @Override
        public int getItemCount() {
            return mList2.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            //객체 선언
            public TextView iName2;
            public ImageView iPic2;

            public MyViewHolder(View itemView) {
                super(itemView);
                //객체 초기화
                iName2 = itemView.findViewById(R.id.textView);
                iPic2 = itemView.findViewById(R.id.imageView);

            }
        }
    }
}
