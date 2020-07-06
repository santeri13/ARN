package com.example.narva;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.util.List;


public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ToursViewHolder>{

    private Context contex;
    private List<PointReader> tourList;
    Dialog dialog;
    public int currentnumber=1;
    int pos;
    public float meters;
    public String uid;


    public PointAdapter(Context contex, List<PointReader> tourList){
        this.tourList = tourList;
        this.contex=contex;
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
                if(pos == currentnumber&& meters<=50){
                    TextView name = dialog.findViewById(R.id.text_header);
                    TextView text = dialog.findViewById(R.id.hint_text);
                    TextView number = dialog.findViewById(R.id.number);
                    ImageButton close = dialog.findViewById(R.id.close);
                    name.setText(tourList.get(holder.getAdapterPosition()).getName());
                    text.setText(tourList.get(holder.getAdapterPosition()).getText());
                    number.setText(tourList.get(holder.getAdapterPosition()).getNumber().toString());
                    dialog.show();
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
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
                if(pos ==currentnumber&& meters<=50){
                    TextView name = dialog.findViewById(R.id.text_header);
                    TextView text = dialog.findViewById(R.id.hint_text);
                    TextView number = dialog.findViewById(R.id.number);
                    ImageButton close = dialog.findViewById(R.id.close);
                    Button next = dialog.findViewById(R.id.next);
                    name.setText(tourList.get(holder.getAdapterPosition()).getName());
                    text.setText(tourList.get(holder.getAdapterPosition()).getText());
                    number.setText(tourList.get(holder.getAdapterPosition()).getNumber().toString());
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
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
        TextView textViewName;
        ImageButton hint;
        TextView number;
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
