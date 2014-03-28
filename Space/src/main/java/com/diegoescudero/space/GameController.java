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

        initLayout();
        initListeners();
        initSensors();

        gameModel = new GameModel(this);
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
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (Math.abs(currentX - x) > NOISE) {
            currentX = Math.abs(currentX - x);
//            Log.d("x", Float.toString(currentX));

        }

        if (Math.abs(currentY - y) > NOISE) {
            currentY = Math.abs(currentY - y);
//            Log.d("y", Float.toString(currentY));
        }

        if (Math.abs(currentZ - z) > NOISE) {
            currentZ = Math.abs(currentZ - z);
//            Log.d("z", Float.toString(currentZ));
        }

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
