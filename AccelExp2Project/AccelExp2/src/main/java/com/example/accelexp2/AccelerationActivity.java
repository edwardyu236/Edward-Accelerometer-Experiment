package com.example.accelexp2;

        import android.app.Activity;
        import android.content.ClipData;
        import android.content.ClipboardManager;
        import android.content.Context;
        import android.graphics.Color;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;

public class AccelerationActivity extends Activity {
    private TextView xResult;
    private TextView yResult;
    private TextView zResult;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float x, y, z;

    private static final String TAG = "AccelerationActivity";
    private boolean logging = false;
    private Button logButton;

    private String logString;
    private Button copyButton;


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

        logging = false;
        logButton = (Button) findViewById(R.id.log_button);
        logButton.setText("Enable Logging");
        logString = "";

        copyButton = (Button) findViewById(R.id.copy_button);
        copyButton.setText("Copy Log");
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

    private void log() {
        if (logging)
        {
            Log.i(TAG, x + ", " + y + ", " + z);
            logString = logString + x + ", " + y + ", " + z + "\n";
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
            log();
        }

    };

    public void toggleLogging(View view) {
        if (logging) {

            logging = false;
            logButton.setText("Enable Logging");
            Log.i(TAG, "...Stopping Logging");

            copyButton.setText("Copy Log");

        } else {
            // reset logString
            logString = "";

            logging = true;
            logButton.setText("Disable Logging");
            Log.i(TAG, "Starting Logging...");

            copyButton.setText("Log Copying is Disabled");
        }
    }

    public void copyLog(View view) {
        if (!logging) {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip =
                    ClipData.newPlainText("log", "==Begin Log==\n" + logString + "==End Log==");
            clipboard.setPrimaryClip(clip);
            Log.i(TAG, "Copied!");
        }
    }

}