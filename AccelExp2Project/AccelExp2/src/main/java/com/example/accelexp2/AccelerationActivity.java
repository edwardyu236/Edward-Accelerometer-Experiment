package com.example.accelexp2;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Color;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.widget.TextView;

public class AccelerationActivity extends Activity {
    private TextView xResult;
    private TextView yResult;
    private TextView zResult;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float x, y, z;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

        xResult = (TextView) findViewById(R.id.x_result);
        yResult = (TextView) findViewById(R.id.y_result);
        zResult = (TextView) findViewById(R.id.z_result);
        xResult.setText("No result yet");
        yResult.setText("No result yet");
        zResult.setText("No result yet");
    }

    private void refreshDisplay() {
        String xOutput = String.format("x is: %f", x);
        String yOutput = String.format("y is: %f", y);
        String zOutput = String.format("z is: %f", z);

        xResult.setText(xOutput);
        if (x < -1) {
            xResult.setTextColor(Color.RED);
        } else if (x > 1) {
            xResult.setTextColor(Color.GREEN);
        } else {
            xResult.setTextColor(Color.BLACK);
        }

        yResult.setText(yOutput);
        if (y < -1) {
            yResult.setTextColor(Color.RED);
        } else if (y > 1) {
            yResult.setTextColor(Color.GREEN);
        } else {
            yResult.setTextColor(Color.BLACK);
        }

        zResult.setText(zOutput);
        if (z < -1) {
            zResult.setTextColor(Color.RED);
        } else if (z > 1) {
            zResult.setTextColor(Color.GREEN);
        } else {
            zResult.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, sensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(accelerationListener);
        super.onStop();
    }

    private SensorEventListener accelerationListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            refreshDisplay();
        }

    };
}