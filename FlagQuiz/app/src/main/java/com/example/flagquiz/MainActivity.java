package com.example.flagquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView taskImage;
    private Button [] buttons;

    private String POINTS_FORMAT = "POINTS %s";
    private TextView pointsTextView;

    private int points = 0;

    private int answerButtonArrayIndex;

    private String FLAG_API = "https://www.countryflags.io/%s/flat/64.png";

    private int[] buttonsIds = {
            R.id.answer_0,
            R.id.answer_1,
            R.id.answer_2,
            R.id.answer_3
    };

    private int currentCountry = 0;

    private String[] countryCodes = Locale.getISOCountries();


    private String getCountryByCode(Locale locale) {
        return locale.getDisplayCountry();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setQuestion();
    }

    private void findViews() {
        pointsTextView = findViewById(R.id.points);
        taskImage = findViewById(R.id.task_img);
        buttons = new Button[4];
        buttons[0] = findViewById(buttonsIds[0]);
        buttons[1] = findViewById(buttonsIds[1]);
        buttons[2] = findViewById(buttonsIds[2]);
        buttons[3] = findViewById(buttonsIds[3]);
        buttons[0].setOnClickListener(this);
        buttons[1].setOnClickListener(this);
        buttons[2].setOnClickListener(this);
        buttons[3].setOnClickListener(this);
    }

    private void setQuestion(){
        currentCountry = new Random().nextInt(countryCodes.length);
        Picasso.get().load(String.format(FLAG_API, countryCodes[currentCountry])).into(taskImage);

        //Hint
        Log.e(getClass().getName(), new Locale("ru", countryCodes[currentCountry]).getDisplayCountry());

        answerButtonArrayIndex = new Random().nextInt(buttons.length);
        for (int i = 0; i < buttons.length; i++){
            if (i == answerButtonArrayIndex){
                buttons[i].setText(getCountryByCode(new Locale("ru" , countryCodes[currentCountry])));
            } else {
                int random;
                List<Integer> list = new ArrayList<>();
                list.add(currentCountry);
                while (!list.contains(random = new Random().nextInt(countryCodes.length))){
                    list.add(random);
                    buttons[i].setText(getCountryByCode(new Locale("ru", countryCodes[new Random().nextInt(countryCodes.length)])));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.answer_0:
                checkAnswer(0);
                setQuestion();
                break;
            case R.id.answer_1:
                checkAnswer(1);
                setQuestion();
                break;
            case R.id.answer_2:
                checkAnswer(2);
                setQuestion();
                break;
            case R.id.answer_3:
                checkAnswer(3);
                setQuestion();
                break;
        }
    }

    private void checkAnswer(int i) {
        if (i == answerButtonArrayIndex){
            pointsTextView.setText(String.format(POINTS_FORMAT, ++points));
            Toast.makeText(this, "" + points, Toast.LENGTH_SHORT).show();
        } else {
            pointsTextView.setText(String.format(POINTS_FORMAT, points>0 ? --points : 0));
            Toast.makeText(this, "" + points, Toast.LENGTH_SHORT).show();
        }
    }
}