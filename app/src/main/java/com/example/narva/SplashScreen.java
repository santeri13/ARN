package com.example.narva;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBan();
        Intent intent = new Intent(SplashScreen.this, MainActivity.class );
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
        hideNavigationBan();
        super.onStart();
    }

    @Override
    protected void onPause() {
        hideNavigationBan();
        super.onPause();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        hideNavigationBan();
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        hideNavigationBan();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        hideNavigationBan();
        super.onRestart();
    }
}