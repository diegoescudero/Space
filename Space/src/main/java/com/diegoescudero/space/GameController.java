package com.diegoescudero.space;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class GameController extends Activity implements SensorEventListener {

    private GameView gameView;
    private GameModel gameModel;
    private GameThread gameThread = null;

    private LinearLayout leftSwipe;

    private ImageButton optionsButton;

    private SensorManager sManager;
    private Sensor accelerometer;
    private float currentTilt = 0;

    private int playerShots = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);

        gameModel = new GameModel(this);

        initLayout();
        initListeners();
        initSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void initLayout() {
        leftSwipe = (LinearLayout)findViewById(R.id.leftSwipe);
        gameView = (GameView)findViewById(R.id.gameView);
        optionsButton = (ImageButton)findViewById(R.id.optionsButton);

        SurfaceHolder holder = gameView.getHolder();
        if (holder != null) {
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    startGameThread();
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    boolean retry = true;
//                    Log.d("killed", "the surface is destroyed");

                    gameThread.setRunning(false);
                    while (retry) {
                        try {
                            gameThread.join();
                            retry = false;
                        } catch (InterruptedException e) {
                            //nothing
                        }
                    }
                }
            });
        }
    }

    private void initListeners() {
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(GameController.this, MenuOptions.class);
                startActivity(intent);
                finish();
            }
        });

        leftSwipe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        playerShots++;
                        return true;
                }

                return false;
            }
        });
    }

    private void initSensors() {
        sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float y = event.values[1];
        currentTilt = y;
//        gameModel.updateShipVelocity(y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void startGameThread() {
//        if (gameThread == null) {
            gameThread = new GameThread(gameModel, gameView, this);
//        }

        gameThread.setRunning(true);
        gameThread.start();
    }

    private void pauseGameThread() {
        gameThread.setRunning(false);
        gameThread.interrupt();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            startGameThread();
        }
        else {
            pauseGameThread();
        }
    }

    public float getCurrentTilt() {
        return currentTilt;
    }

    public int getPlayerShots() {
        int temp = playerShots;
        playerShots = 0;

        return temp;
    }
}
