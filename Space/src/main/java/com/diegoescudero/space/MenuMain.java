package com.diegoescudero.space;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuMain extends Activity {

    private Button playButton;
    private Button upgradesButton;
    private Button trophiesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);

        initLayout();
        initListeners();
    }

    private void initLayout() {
        playButton = (Button)findViewById(R.id.play);
        upgradesButton = (Button)findViewById(R.id.upgrades);
        trophiesButton = (Button)findViewById(R.id.trophies);
    }

    private void initListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMain.this, GameController.class);
                startActivity(intent);
            }
        });

        upgradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMain.this, MenuUpgrades.class);
                startActivity(intent);
            }
        });

        trophiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuMain.this, MenuTrophies.class);
                startActivity(intent);
            }
        });
    }
}
