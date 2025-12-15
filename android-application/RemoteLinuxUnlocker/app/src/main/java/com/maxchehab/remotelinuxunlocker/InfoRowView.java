package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class InfoRowView extends LinearLayout {

    private final TextView helpText;

    public InfoRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_info_row, this, true);
        FrameLayout container = findViewById(R.id.contentContainer);
        ImageView infoBtn = findViewById(R.id.infoBtn);
        helpText = findViewById(R.id.helpText);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InfoRowView);
        String info = a.getString(R.styleable.InfoRowView_infoText);
        int contentLayout = a.getResourceId(R.styleable.InfoRowView_contentLayout, 0);
        a.recycle();
        if (info != null) {
            helpText.setText(info);
        } else {
            infoBtn.setVisibility(GONE);
        }
        if (contentLayout != 0) {
            LayoutInflater.from(context).inflate(contentLayout, container, true);
        }
        infoBtn.setOnClickListener(v -> {
            helpText.setVisibility(helpText.getVisibility() == VISIBLE ? GONE : VISIBLE);
        });
    }
}
