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
        import android.text.format.Time;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.PrintWriter;
        import java.util.Date;

public class AccelerationActivity extends Activity {
    private TextView xResult;
    private TextView yResult;
    private TextView zResult;
    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private Sensor gyroscopeSensor;
    private float x, y, z, gyroX, gyroY, gyroZ;

    private static final String TAG = "AccelerationActivity";
    private boolean logging = false;
    private Button logButton;
    private Time time;

    private String logString;
    private String gyroLogString;

    private Button copyButton;
    private Button gyroCopyButton;
    private Button saveButton;
    private Button gyroSaveButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

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
        gyroLogString = "";

        copyButton = (Button) findViewById(R.id.accel_copy_button);
        copyButton.setText("Copy Accel Log");
        gyroCopyButton = (Button) findViewById(R.id.gyro_copy_button);
        gyroCopyButton.setText("Copy Gyro Log");

        saveButton = (Button) findViewById(R.id.accel_save_button);
        saveButton.setText("Save Accel Log");

        gyroSaveButton = (Button) findViewById(R.id.gyro_save_button);
        gyroSaveButton.setText("Save Gyro Log");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, accelerationSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(accelerationListener);
        sensorManager.unregisterListener(gyroscopeListener);
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
            time = new Time();
            time.setToNow();
            refreshDisplay();
            log();
        }

    };

    private SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];
            time = new Time();
            time.setToNow();
            gyroscopeLog();
        }

    };

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

            String formattedTime = time.format("%Y-%m-%d %H:%M:%S");
            Log.i(TAG, formattedTime + "(accel): " + x + ", " + y + ", " + z);
            logString = logString + (new Date()).getTime() + "," + x + "," + y + "," + z + ","
                    + formattedTime + "\n";
        }
    }

    private void gyroscopeLog() {
        if (logging)
        {
            String formattedTime = time.format("%Y-%m-%d %H:%M:%S");
            Log.i(TAG, formattedTime + "(gyro): " + gyroX + ", " + gyroY + ", " + gyroZ);
            gyroLogString = gyroLogString + (new Date()).getTime() + ","
                    + gyroX + ", " + gyroY + ", " + gyroZ + ","
                    + formattedTime + "\n";
        }
    }

    public void toggleLogging(View view) {
        if (logging) {

            logging = false;
            logButton.setText("Enable Logging");
            Log.i(TAG, "...Stopping Logging");

            copyButton.setText("Copy Accel Log");
            saveButton.setText("Save Accel Log");
            gyroCopyButton.setText("Copy Gyro Log");
            gyroSaveButton.setText("Save Gyro Log");

        } else {
            // reset logs
            logString = "";
            gyroLogString = "";

            logging = true;
            logButton.setText("Disable Logging");
            Log.i(TAG, "Starting Logging...");

            copyButton.setText("Log Copying is Disabled");
            saveButton.setText("Log Saving is Disabled");
            gyroCopyButton.setText("Log Copying is Disabled");
            gyroSaveButton.setText("Log Saving is Disabled");
        }
    }

    public void copyAccelerationLog(View view) {
        if (!logging) {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("log",
                    "Time (ms since epoch?),x,y,z,Human Time\n" + logString);
            clipboard.setPrimaryClip(clip);
            Log.i(TAG, "Copied Accel Log!");
        }
    }

    public void copyGyroscopeLog(View view) {
        if (!logging) {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("log",
                    "Time (ms since epoch?),x,y,z,Human Time\n" + gyroLogString);
            clipboard.setPrimaryClip(clip);
            Log.i(TAG, "Copied Gyro Log!");
        }
    }

    public void saveAccelerationLog(View view) {
        if (!logging) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/accelexp2");
            dir.mkdirs();
            Log.i(TAG, "saving: " + time.format("%Y-%m-%d_%H.%M.%S") + "-accel.csv");
            File file = new File(dir, time.format("%Y-%m-%d_%H.%M.%S")+"-accel.csv");

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.print("Time (ms since unix epoch),x,y,z,Human Time\n" + logString);
                pw.flush();
                pw.close();
                f.close();
                Log.i(TAG, "Saved Accel Log!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "File not found. Is WRITE_EXTERNAL_STORAGE in manifest? "
                        + "Is USB storage on?");
                saveButton.setText("Issue Saving");
            } catch (IOException e) {
                saveButton.setText("Issue Saving");
                e.printStackTrace();
            }

        }
    }

    public void saveGyroscopeLog(View view) {
        if (!logging) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/accelexp2");
            dir.mkdirs();
            Log.i(TAG, "saving: " + time.format("%Y-%m-%d_%H.%M.%S") + "-gyro.csv");
            File file = new File(dir, time.format("%Y-%m-%d_%H.%M.%S")+"-gyro.csv");

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.print("Time (ms since unix epoch),x,y,z,Human Time\n" + gyroLogString);
                pw.flush();
                pw.close();
                f.close();
                Log.i(TAG, "Saved Gyro Log!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "File not found. Is WRITE_EXTERNAL_STORAGE in manifest? "
                        + "Is USB storage on?");
                gyroSaveButton.setText("Issue Saving");
            } catch (IOException e) {
                gyroSaveButton.setText("Issue Saving");
                e.printStackTrace();
            }

        }
    }

}