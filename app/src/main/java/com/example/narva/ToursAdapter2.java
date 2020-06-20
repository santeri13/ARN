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

public class ToursAdapter2 extends RecyclerView.Adapter<ToursAdapter2.ToursViewHolder> {

    private Context mCtx;
    private List<TourReader2> tourList;

    public ToursAdapter2(Context mCtx, List<TourReader2> tourList){
        this.mCtx = mCtx;
        this.tourList = tourList;
    }
    @NonNull
    @Override
    public ToursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycleview_tours2, parent, false);
        return new ToursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToursViewHolder holder, int position) {
        TourReader2 tour = tourList.get(position);
        holder.textViewName.setText(tour.Name);
        Glide.with(mCtx).load(tour.link).centerCrop().into(holder.foregroundLinearLayout);
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
                if(dataSnapshot.hasChild(tour.Name)){
                    userpoints.child(tour.Name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("like")){
                                Boolean isFollowing = (Boolean) dataSnapshot.child("like").getValue();
                                holder.sparkButton.setChecked(isFollowing);
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
        holder.textcolor.setBackgroundColor(Color.parseColor(tour.color));
        holder.timetext.setText(Long.toString(tour.time));
        holder.liketext.setText(Long.toString(tour.like));
        holder.foregroundLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, Gps.class);
                intent.putExtra("title", holder.textViewName.getText().toString());
                mCtx.startActivity(intent);
            }
        });
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, Gps.class);
                intent.putExtra("title", holder.textViewName.getText().toString());
                mCtx.startActivity(intent);
            }
        });

        holder.sparkButton.setEventListener(new SparkEventListener() {
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
                            if (!dataSnapshot.hasChild(tour.Name)) {
                                userpoints.child(tour.Name).setValue(registerDatabase);
                                userpoints.keepSynced(true);
                            }
                            else{
                                userpoints.child(tour.Name).child("like").setValue(true);
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
                            userpoints.child(tour.Name).child("like").setValue(false);
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

    }
    @Override
    public int getItemCount() {
        return tourList.size();
    }

    class ToursViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, timetext, liketext;
        ImageView foregroundLinearLayout;
        CardView cardView;
        SparkButton sparkButton;
        LinearLayout textcolor;
        public ToursViewHolder(@NonNull View itemView) {
            super(itemView);
            sparkButton = itemView.findViewById(R.id.spark_button);
            cardView = itemView.findViewById(R.id.card_view);
            textViewName = itemView.findViewById(R.id.info_text);
            foregroundLinearLayout = itemView.findViewById(R.id.photo);
            timetext = itemView.findViewById(R.id.time);
            liketext = itemView.findViewById(R.id.like);
            textcolor = itemView.findViewById(R.id.textcolor);

        }
    }
}

