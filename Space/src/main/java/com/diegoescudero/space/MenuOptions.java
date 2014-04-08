package com.diegoescudero.space;

import android.app.Activity;
import android.os.Bundle;

public class MenuOptions extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_options);
    }


    public void onBackPressed() {
        finish();
    }
}
