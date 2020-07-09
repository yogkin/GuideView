package com.tfish.guideview;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private View textView1;
    private View textView2;
    private View textView3;

    private HighLightGuideView highLightGuideViewFirst;
    private HighLightGuideView highLightGuideViewSecond;
    private HighLightGuideView highLightGuideViewThird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView1 = findViewById(R.id.tv_1);
        textView2 = findViewById(R.id.tv_2);
        textView3 = findViewById(R.id.tv_3);
        initGride();
        highLightGuideViewFirst.show();
    }

    private void initGride() {
         highLightGuideViewFirst = HighLightGuideView.buildHomePageFirst(this, textView1,textView2,textView3);

    }
}