package com.moon.jachisekki.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moon.jachisekki.AddtoRefrigeratorActivity;
import com.moon.jachisekki.R;
import com.moon.jachisekki.refrigerator.RefItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyRefrigerator extends Fragment {
    //객체 선언
    public static RefAdapter adapter;
    public static FirebaseFirestore db;
    public static FirebaseAuth auth;
    public static ArrayList<RefItem> list;
    private RecyclerView recyclerView;
    public static String ingredients;
    public static Map<String, ArrayList<RefItem>> data1;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refrigerator, container, false);
        //객체 초기화
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ingredients = "";
        data1 = new HashMap<>();
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        //데이터 가져오기 함수 호출
        readMyFreez();
        //어댑터 초기화
        adapter = new RefAdapter(getActivity(), list);
        recyclerView = (RecyclerView) view.findViewById(R.id.gridViewMain);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 5));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (RefItem rt : list) {
                    ingredients = ingredients + rt.getName() + " ";
                }
                Intent intent = new Intent(view.getContext(), AddtoRefrigeratorActivity.class);
                intent.putExtra("data", ingredients);
                startActivity(intent);
            }
        });
        return view;
    }


    //데이터베이스에서 데이터 가져오기 -- RecommendRecipe에서 설명.
    public static void readMyFreez() {
        db.document("MyFreez/" + auth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List list2 = (List) document.getData().get("freezer");
                list.clear();
                for (int i = 0; i < list2.size(); i++) {
                    HashMap map = (HashMap) list2.get(i);
                    list.add(new RefItem(map.get("name").toString(), Integer.parseInt(map.get("image").toString())));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    //데이터베이스에 데이터 추가 함수.
    public static void addFreez() {
        //데이터준비
        DocumentReference dref = db.collection("MyFreez").document(auth.getCurrentUser().getEmail());
        Map<String, Object> data1 = new HashMap<>();
        //덮어쓰기
        //리스트를 통채로 데이터로 넣는다.
        data1.put("freezer", list);
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
    //외부에서 item을 add할때 호출하는 함수.
    //AddtoRefrigeratorActivity에서 사용한다.
    public static void addItem(RefItem item) {
        int count = list.size();
        for (int i = 0; i < list.size(); i++) {
            //중복 검사
            if (!list.get(i).getName().equals(item.getName())) {
                count--;
            }
        }
        if (count == 0)
            list.add(item);
        addFreez();
        readMyFreez();
        adapter.notifyDataSetChanged();
    }
    //외부에서 item을 remove할때 호출하는 함수.
    //AddtoRefrigeratorActivity에서 사용한다.
    public static void removeItem(RefItem item) {
        for (int i = 0; i < list.size(); i++) {
            //중복검사.
            if (list.get(i).getName().equals(item.getName())) {
                list.remove(i);
                break;
            }
        }
        addFreez();
        readMyFreez();
        adapter.notifyDataSetChanged();
    }

    //어댑터 클래스
    class RefAdapter extends RecyclerView.Adapter<RefAdapter.MyViewHolder> {

        private ArrayList<RefItem> mList;
        private LayoutInflater mInflate;
        private Context mContext;
        ArrayList<RefItem> filteredList;

        public RefAdapter(Context context, ArrayList<RefItem> list) {
            this.mList = list;
            this.filteredList = list;
            this.mContext = context;
            this.mInflate = LayoutInflater.from(context);
        }

        @Override
        public RefAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflate.inflate(R.layout.ref_item, parent, false);
            RefAdapter.MyViewHolder viewHolder = new RefAdapter.MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RefAdapter.MyViewHolder holder, int position) {

            holder.iName.setText(((RefItem) mList.get(position)).getName());
            holder.iPic.setImageResource(((RefItem) mList.get(position)).getImage());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView iName;
            public ImageView iPic;

            public MyViewHolder(View itemView) {
                super(itemView);
                iName = itemView.findViewById(R.id.textView);
                iPic = itemView.findViewById(R.id.imageView);

            }
        }


    }
}
