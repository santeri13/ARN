package com.example.narva;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

public class ToursAdapter2 extends PagerAdapter {

    private Context mCtx;
    private List<TourReader2> tourList;
    private LayoutInflater inflater;

    public ToursAdapter2(Context mCtx, List<TourReader2> tourList){
        this.mCtx = mCtx;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.recycleview_tours2,container,false);

        TextView textViewName, timetext, liketext;
        ImageView foregroundLinearLayout;
        CardView cardView;
        SparkButton sparkButton;
        LinearLayout textcolor;
        sparkButton = view.findViewById(R.id.spark_button);
        cardView = view.findViewById(R.id.card_view);
        textViewName = view.findViewById(R.id.info_text);
        foregroundLinearLayout = view.findViewById(R.id.photo);
        timetext = view.findViewById(R.id.time);
        liketext = view.findViewById(R.id.like);
        textcolor = view.findViewById(R.id.textcolor);
        textViewName.setText(tourList.get(position).Name);
        Glide.with(mCtx).load(tourList.get(position).link).centerCrop().into(foregroundLinearLayout);
        //Picasso.get().load(tour.link).into(new Target() {
        //@Override
        //public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        //holder.foregroundLinearLayout.setBackground(new BitmapDrawable(bitmap));
        //holder.foregroundLinearLayout.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //}

        //@Override
        //public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        //Log.d("TAG", "FAILED");
        //}
        //@Override
        //public void onPrepareLoad(Drawable placeHolderDrawable) {
        //Log.d("TAG", "Prepare Load");
        //}
        //});
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference userpoints  = FirebaseDatabase.getInstance().getReference("user").child(uid);
        userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(tourList.get(position).Name)){
                    userpoints.child(tourList.get(position).Name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("like")){
                                Boolean isFollowing = (Boolean) dataSnapshot.child("like").getValue();
                                sparkButton.setChecked(isFollowing);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        textcolor.setBackgroundColor(Color.parseColor(tourList.get(position).color));
        timetext.setText(Long.toString(tourList.get(position).time));
        liketext.setText(Long.toString(tourList.get(position).time));
        foregroundLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, Gps.class);
                intent.putExtra("title", textViewName.getText().toString());
                mCtx.startActivity(intent);
            }
        });
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, Gps.class);
                intent.putExtra("title", textViewName.getText().toString());
                mCtx.startActivity(intent);
            }
        });

        sparkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference userpoints  = FirebaseDatabase.getInstance().getReference("user").child(uid);
                ToursLike registerDatabase = new ToursLike(true);
                if(buttonState){
                    userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(tourList.get(position).Name)) {
                                userpoints.child(tourList.get(position).Name).setValue(registerDatabase);
                                userpoints.keepSynced(true);
                            }
                            else{
                                userpoints.child(tourList.get(position).Name).child("like").setValue(true);
                                userpoints.keepSynced(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userpoints.child(tourList.get(position).Name).child("like").setValue(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        container.addView(view,0);
        return view;
    }

    @Override
    public int getCount() {
        return tourList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}

