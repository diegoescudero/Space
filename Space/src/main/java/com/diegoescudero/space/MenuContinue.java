package com.diegoescudero.space;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MenuContinue extends Activity {
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_continue);

        initLayout();
        initListeners();
    }

    private void initLayout() {
        continueButton = (Button)findViewById(R.id.continueButton);
    }

    private void initListeners() {
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuContinue.this, GameController.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
