package com.example.narva;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tours#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tours extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private ToursAdapter1 adapter1;
    private ToursAdapter2 adapter2;
    private ViewPager recyclerView2;
    private List<TourReader1> artistList;
    private List<TourReader2> lovedtours;
    String uid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Tours() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tours.
     */
    // TODO: Rename and change types and number of parameters
    public static Tours newInstance(String param1, String param2) {
        Tours fragment = new Tours();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        reference = FirebaseDatabase.getInstance().getReference("Tours");
        reference.addValueEventListener(valueEventListener);
        reference.addValueEventListener(valueEventListener2);
        reference.keepSynced(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }
    ValueEventListener valueEventListener = (new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            artistList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TourReader1 artist = snapshot.getValue(TourReader1.class);
                    artistList.add(artist);
                }
                adapter1.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    ValueEventListener valueEventListener2 = (new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            lovedtours.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TourReader2 artist = snapshot.getValue(TourReader2.class);
                    lovedtours.add(artist);
                }
                adapter2.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tours, container, false);
        recyclerView = rootview.findViewById(R.id.list2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistList = new ArrayList<>();
        adapter1 = new ToursAdapter1(getContext(), artistList);
        recyclerView.setAdapter(adapter1);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView2 = rootview.findViewById(R.id.list);
        lovedtours = new ArrayList<>();
        adapter2 = new ToursAdapter2(getContext(), lovedtours);
        recyclerView2.setAdapter(adapter2);
        LinearLayoutManager mLinearLayoutManagerHorizontal = new LinearLayoutManager(getContext());
        mLinearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        return rootview;
    }
}
