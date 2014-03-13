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
import android.view.View;
import android.widget.ImageButton;

public class GameController extends Activity implements SensorEventListener {

    private GameView gameView;
    private GameModel gameModel;

    private ImageButton optionsButton;
    private GameThread gameThread;

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

        gameView = new GameView(this);
        gameModel = new GameModel(this);

        gameThread = new GameThread(gameModel, gameView);
        gameThread.setRunning(true);
        gameThread.start();
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

    private void startGameThread() {
        if (gameThread != null) {
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    private void pauseGameThread() {
        if (gameThread != null) {
            gameThread.setRunning(false);
            gameThread.interrupt();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (Math.abs(currentX - x) > NOISE) {
            currentX = Math.abs(currentX - x);
            Log.d("x", Float.toString(currentX));

        }

        if (Math.abs(currentY - y) > NOISE) {
            currentY = Math.abs(currentY - y);
            Log.d("y", Float.toString(currentY));
        }

        if (Math.abs(currentZ - z) > NOISE) {
            currentZ = Math.abs(currentZ - z);
            Log.d("z", Float.toString(currentZ));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ignored
    }
}
