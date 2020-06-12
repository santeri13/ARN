package com.example.narva;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomGridLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;
    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag){
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
