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

import java.util.ArrayList;

public class GameController extends Activity implements SensorEventListener {

    private GameView gameView;
    private GameModel gameModel;
    private GameThread gameThread = null;

    private ImageButton pauseButton;

    private SensorManager sManager;
    private Sensor accelerometer;
    private ArrayList<Float> tilts = new ArrayList<Float>();

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
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void initLayout() {
        gameView = (GameView)findViewById(R.id.gameView);
        pauseButton = (ImageButton)findViewById(R.id.optionsButton);

        SurfaceHolder holder = gameView.getHolder();
        if (holder != null) {
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    createGameThread();
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    killThread();
                }
            });
        }
    }

    private void initListeners() {
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(GameController.this, MenuMain.class);
//                startActivity(intent);
                finish();
            }
        });

        gameView.setOnTouchListener(new View.OnTouchListener() {
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
        float x = event.values[0];
        synchronized (tilts) {
            tilts.add(-x);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void killThread() {
        boolean retry = true;
//                    Log.d("killed", "the surface is destroyed");

        gameThread.setRunning(false);
        while (retry) {
            try {
                gameThread.join(0);
                retry = false;
            } catch (InterruptedException e) {
                //do nothing
            }
        }
    }

    private void createGameThread() {
        if (gameThread == null) {
            gameThread = new GameThread(gameModel, gameView, this);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    private void startGameThread() {
        if (gameThread != null) {
            gameThread.setRunning(true);
        }
    }

    private void pauseGameThread() {
        if (gameThread != null) {
            gameThread.setRunning(false);
//            gameThread.interrupt();
        }
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

    public void launchContinueMenu() {
        Intent intent = new Intent(this, MenuContinue.class);
        startActivity(intent);
        finish();
    }

    public float getCurrentTilt() {
        float avg = 0;

        synchronized (tilts) {
            int size = tilts.size();

            for (float f : tilts) {
                avg += f / size;
            }

            tilts.clear();
        }

        return avg;
    }

    public int getPlayerShots() {
        int temp = playerShots;
        playerShots = 0;

        return temp;
    }
}
