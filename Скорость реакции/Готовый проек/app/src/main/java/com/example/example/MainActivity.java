package com.example.example;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView heart;

    private DisplayMetrics metrics;

    private RelativeLayout.LayoutParams params;
    private Button restart;

    private int currentApiVersion;

    private ArrayList<Long> currentClickTime = new ArrayList<>();
    private ArrayList<Double> reactionTimes = new ArrayList<>();

    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDisplayMetrics();
        setFullscreenMode();
        findViews();
        setImageView();
    }

    private void getDisplayMetrics(){
        metrics = getResources().getDisplayMetrics();
    }

    private void setFullscreenMode() {
        currentApiVersion = Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            });
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setImageView() {
        params.topMargin = Math.abs(new Random().nextInt(metrics.heightPixels) - heart.getHeight());
        params.leftMargin = Math.abs(new Random().nextInt(metrics.widthPixels) - heart.getWidth());
        heart.setLayoutParams(params);
        heart.setVisibility(View.VISIBLE);
    }

    private void findViews() {
        RelativeLayout relativeLayout = findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(this);

        restart = findViewById(R.id.restart);
        restart.setOnClickListener(v -> {
            isGameOver = false;
            restart.setVisibility(View.INVISIBLE);
            setImageView();
        });

        heart = new ImageView(this);
        heart.setTag("play_img");
        heart.setImageDrawable(getDrawable(R.drawable.health));
        params = new RelativeLayout
                .LayoutParams(metrics.heightPixels / 7, metrics.heightPixels / 7);
        heart.setLayoutParams(params);
        heart.setOnClickListener(this);

        relativeLayout.addView(heart);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null){
            currentClickTime.add(System.nanoTime());
            calculateReactionTimes();
            v.setVisibility(View.INVISIBLE);
            setImageView();
        } else {
            heart.setVisibility(View.INVISIBLE);
            gameOver();
        }
    }

    private void calculateAvg() {
        if (reactionTimes.size() > 1){
            double avg = reactionTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .getAsDouble();

            showAvgTime(avg);
        } else {
            if (reactionTimes.size() > 0){
                showAvgTime(0);
            }
        }
    }



    private void calculateReactionTimes() {
        if (currentClickTime.size() < 2){
            currentClickTime.add(currentClickTime.get(0));
        }
        double reactionTime = (currentClickTime.get(currentClickTime.size() - 1)
                - currentClickTime.get(currentClickTime.size() - 2)) * Math.pow(10, -9);

        reactionTimes.add(reactionTime);
        currentClickTime.remove(0);
    }

    private void showAvgTime(double sum) {
        if (!isGameOver){
            Toast.makeText(this, String.format(Locale.getDefault(),
                    "Среднее время реакции: %.3f",
                    sum), Toast.LENGTH_SHORT).show();
        }
    }

    private void gameOver() {
        calculateAvg();

        restart.setVisibility(View.VISIBLE);

        currentClickTime.clear();
        reactionTimes.clear();

        isGameOver = true;
    }
}
