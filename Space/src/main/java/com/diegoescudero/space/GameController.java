package com.diegoescudero.space;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;

public class GameController extends Activity implements SensorEventListener {

    private GameView gameView;
    private GameModel gameModel;
    private GameThread gameThread;

    private ImageButton optionsButton;

    private SensorManager sManager;
    private Sensor accelerometer;
    private final float NOISE = (float)2.0;

    private float currentX = 0;
    private float currentY = 0;
    private float currentZ = 0;

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
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initLayout() {
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
                Intent intent =  new Intent(GameController.this, Options.class);
                startActivity(intent);
            }
        });
    }

    private void initSensors() {
        sManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float y = event.values[1];
        gameModel.setTiltDirection(y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ignored
    }

    private void startGameThread() {
        //if (gameThread == null) {
            gameThread = new GameThread(gameModel, gameView);


            //}

        //if (gameThread.getRunning() == false) {
            gameThread.setRunning(true);
            gameThread.start();
        //}
    }

    private void pauseGameThread() {
        gameThread.setRunning(false);
        gameThread.interrupt();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
//            startGameThread();
        }
        else {
            pauseGameThread();
        }
    }
}
