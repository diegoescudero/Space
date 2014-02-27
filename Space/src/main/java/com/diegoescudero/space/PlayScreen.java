package com.diegoescudero.space;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PlayScreen extends Activity {

    private GameView gameView;
    private ImageButton optionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);

        initLayout();
        initListeners();
    }


    private void initLayout() {
        gameView = (GameView)findViewById(R.id.gameView);
        optionsButton = (ImageButton)findViewById(R.id.optionsButton);
    }

    private void initListeners() {
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(PlayScreen.this, Options.class);
                startActivity(intent);
            }
        });
    }

}
