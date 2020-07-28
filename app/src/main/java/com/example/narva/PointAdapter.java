package com.example.narva;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ToursViewHolder>{

    private Context contex;
    private List<PointReader> tourList;
    Dialog dialog;
    public int currentnumber=1;
    int pos;
    public float meters;
    public String uid;
    public String title;
    public int pathindicator = 0,latitudesize;
    public int pointsofuser;
    public TextView points;
    public Context context1;
    public Scrollinterface scrollinterface;


    public PointAdapter(Context contex, List<PointReader> tourList,Scrollinterface scrollinterface){
        this.tourList = tourList;
        this.contex=contex;
        this.scrollinterface=scrollinterface;
    }

    @NonNull
    @Override
    public ToursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(contex).inflate(R.layout.map_recycleview, parent, false);
        ToursViewHolder holder = new ToursViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull PointAdapter.ToursViewHolder holder, int position) {
        PointReader pointAdapter = tourList.get(position);
        holder.textViewName.setText(pointAdapter.name);
        holder.number.setText((pointAdapter.number.toString()));
        Glide.with(contex).load(pointAdapter.image).centerCrop().into(holder.foregroundLinearLayout);
        dialog = new Dialog(contex);
        dialog.setContentView(R.layout.hint_text_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        holder.foregroundLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = tourList.get(holder.getAdapterPosition()).getNumber().intValue();
                if(pos == currentnumber){
                    TextView name = dialog.findViewById(R.id.text_header);
                    TextView text = dialog.findViewById(R.id.hint_text);
                    TextView number = dialog.findViewById(R.id.number);
                    ImageButton close = dialog.findViewById(R.id.close);
                    name.setText(tourList.get(holder.getAdapterPosition()).getName());
                    text.setText(tourList.get(holder.getAdapterPosition()).getText());
                    number.setText(tourList.get(holder.getAdapterPosition()).getNumber().toString());
                    Button next = dialog.findViewById(R.id.next);
                    dialog.show();
                    if(pathindicator+1 == latitudesize){
                        next.setText("Finish");
                    }
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentnumber = currentnumber +1;
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference userpoints = FirebaseDatabase.getInstance().getReference("user").child(uid).child(title);
                            userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String status = dataSnapshot.child("tours").getValue(String.class);
                                    Log.d("TAG","asdasdasdas");
                                    if (user.isAnonymous()) {
                                        pointsofuser = pointsofuser;
                                    }
                                    else if (status.equals("done")) {
                                        pointsofuser = pointsofuser;
                                    }
                                    else {
                                        pointsofuser = pointsofuser + 5;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            points.setText(Integer.toString(pointsofuser));
                            dialog.dismiss();
                            scrollinterface.scroll();

                        }
                    });

                }
                else if(pos > currentnumber){
                    Toast.makeText(contex,"Before that see previous location", Toast.LENGTH_LONG).show();
                }
                else if(pos < currentnumber){
                    Toast.makeText(contex,"You already been on that posistion", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(contex,"You need to be 50 meters near with place", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = tourList.get(holder.getAdapterPosition()).getNumber().intValue();
                if(pos ==currentnumber){
                    TextView name = dialog.findViewById(R.id.text_header);
                    TextView text = dialog.findViewById(R.id.hint_text);
                    TextView number = dialog.findViewById(R.id.number);
                    ImageButton close = dialog.findViewById(R.id.close);
                    Button next = dialog.findViewById(R.id.next);
                    name.setText(tourList.get(holder.getAdapterPosition()).getName());
                    text.setText(tourList.get(holder.getAdapterPosition()).getText());
                    number.setText(tourList.get(holder.getAdapterPosition()).getNumber().toString());
                    if(pathindicator+1 == latitudesize){
                        next.setText("Finish");
                    }
                    dialog.show();
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentnumber = currentnumber +1;
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference userpoints = FirebaseDatabase.getInstance().getReference("user").child(uid).child(title);
                            userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String status = dataSnapshot.child("tours").getValue(String.class);
                                    if (user.isAnonymous()) {
                                        pointsofuser = pointsofuser;
                                    }
                                    else if (status.equals("done")) {
                                        pointsofuser = pointsofuser;
                                    }
                                    else {
                                        pointsofuser = pointsofuser + 5;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            points.setText(Integer.toString(pointsofuser));
                            dialog.dismiss();
                            scrollinterface.scroll();
                        }
                    });
                }
                else if(pos > currentnumber){
                    Toast.makeText(contex,"Before that see previous location", Toast.LENGTH_LONG).show();
                }
                else if(pos < currentnumber){
                    Toast.makeText(contex,"You already been on that posistion", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(contex,"You need to be 50 meters near with place", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Picasso.get().load(pointAdapter.image).into(new Target() {
            //@Override
            //public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //holder.foregroundLinearLayout.setBackground(new BitmapDrawable(bitmap));
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
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public class ToursViewHolder extends RecyclerView.ViewHolder {
        ImageView foregroundLinearLayout;
        TextView textViewName,number;
        ImageButton hint;
        public ToursViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.info_text);
            foregroundLinearLayout = itemView.findViewById(R.id.photo);
            hint = itemView.findViewById(R.id.show_hint);
            number = itemView.findViewById(R.id.number);
        }
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
