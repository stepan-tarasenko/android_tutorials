package com.example.fruitsninja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relativeLayout;
    private Button restart;

    private Thread gameThread;

    private DisplayMetrics metrics;

    private int points = 0;

    private int[] images = {
            R.drawable.banana,
            R.drawable.cherry,
            R.drawable.kiwi,
            R.drawable.watermelon,
            R.drawable.pineapple
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        getDisplayMetrics();

        startGameThread();
    }

    private void startGameThread() {
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        while (true) {
                            final ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setOnClickListener(MainActivity.this);
                            imageView.setImageDrawable(getDrawable(images[new Random().nextInt(images.length)]));
                            RelativeLayout.LayoutParams params =
                                    new RelativeLayout.LayoutParams(
                                            metrics.heightPixels / 10,
                                            metrics.heightPixels / 10
                                    );
                            params.topMargin = new Random().nextInt(metrics.heightPixels - imageView.getWidth());
                            params.leftMargin = new Random().nextInt(metrics.heightPixels - imageView.getWidth());

                            imageView.setLayoutParams(params);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    relativeLayout.addView(imageView);
                                }
                            });
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
        });
        gameThread.start();
    }

    private void findViews() {
       relativeLayout = findViewById(R.id.recycler_view);
       restart = findViewById(R.id.restart);
       restart.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               restartGame();
           }
       });
       relativeLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MainActivity.this, "Points: " + points, Toast.LENGTH_SHORT).show();
               gameThread.interrupt();
               restart.setVisibility(View.VISIBLE);
           }
       });
    }

    private void restartGame() {
        points = 0;
        startGameThread();
        restart.setVisibility(View.INVISIBLE);
    }

    private void getDisplayMetrics() {
        metrics = getResources().getDisplayMetrics();
    }

    @Override
    public void onClick(View v) {
        relativeLayout.removeView(v);
        points++;
        Log.e(getClass().getName(), "" + points);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
