package com.example.narva;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Slider extends AppCompatActivity {

    private ViewPager mPager;
    private int[] layouts = {R.layout.plan, R.layout.discovery,R.layout.connect};
    private SliderAdapter slider;
    private LinearLayout mDotsLayout;
    private TextView[]mDots;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        hideNavigationBan();
        mPager = (ViewPager)findViewById(R.id.viewPager);
        mDotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        slider = new SliderAdapter (layouts,this);
        mPager.setAdapter(slider);
        addDotsIndicator(0);
        mPager.addOnPageChangeListener(viewListener);
        button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });
    }
    public void addDotsIndicator(int  posistion){
        mDots = new TextView[3];
        mDotsLayout.removeAllViews();
        for (int i= 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;",1));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#00ffffff"));
            mDots[i].setBackground(getResources().getDrawable(R.drawable.ring));

            mDotsLayout.addView(mDots[i]);
        }
        if (mDots.length > 0){
            mDots[posistion].setTextColor(Color.parseColor("#ffffff"));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public void openMain(){
            Intent intent = new Intent(this, nav.class);
            startActivity(intent);
            finish();

    }
    public void hideNavigationBan(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationBan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideNavigationBan();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        hideNavigationBan();
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBan();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideNavigationBan();
    }
}