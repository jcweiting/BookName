package com.example.second_book_exchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {

    private ImageView launch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_acitivy);

        initView();

        Handler handler = new Handler();

        // 延遲兩秒換頁至HomePage
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // 2秒後才會執行裡面code
                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2000);

    }

    private void initView() {

        launch = findViewById(R.id.launch_page);
    }
}