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
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.PrintWriter;
        import java.util.Date;

public class AccelerationActivity extends Activity {
    private TextView xAccelResult;
    private TextView yAccelResult;
    private TextView zAccelResult;
    private TextView xGyroResult;
    private TextView yGyroResult;
    private TextView zGyroResult;

    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private Sensor gyroscopeSensor;
    private float accelX, accelY, accelZ, gyroX, gyroY, gyroZ;

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

        xAccelResult = (TextView) findViewById(R.id.x_accel_result);
        yAccelResult = (TextView) findViewById(R.id.y_accel_result);
        zAccelResult = (TextView) findViewById(R.id.z_accel_result);
        xAccelResult.setText("Ax is: ±x.xxx");
        yAccelResult.setText("Ay is: ±x.xxx");
        zAccelResult.setText("Az is: ±x.xxx");
        xGyroResult = (TextView) findViewById(R.id.x_gyro_result);
        yGyroResult = (TextView) findViewById(R.id.y_gyro_result);
        zGyroResult = (TextView) findViewById(R.id.z_gyro_result);
        xGyroResult.setText("Gx is: ±x.xxx");
        yGyroResult.setText("Gy is: ±x.xxx");
        zGyroResult.setText("Gz is: ±x.xxx");

        logging = false;
        logButton = (Button) findViewById(R.id.log_button);
        logButton.setText("Enable Logging");


        logString = "";
        gyroLogString = "";

        copyButton = (Button) findViewById(R.id.accel_copy_button);
        copyButton.setText("SII: Copying is Disabled");
        gyroCopyButton = (Button) findViewById(R.id.gyro_copy_button);
        gyroCopyButton.setText("SII: Copying is Disabled");

        saveButton = (Button) findViewById(R.id.accel_save_button);
        saveButton.setText("Save Accel Log");

        gyroSaveButton = (Button) findViewById(R.id.gyro_save_button);
        gyroSaveButton.setText("Save Gyro Log");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, accelerationSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME);

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
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];
            time = new Time();
            time.setToNow();
            refreshAccelDisplay();
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
            refreshGyroDisplay();
            gyroscopeLog();
        }

    };

    private void refreshAccelDisplay() {
        String xOutput = String.format("Ax is: %.3f", accelX);
        String yOutput = String.format("Ay is: %.3f", accelY);
        String zOutput = String.format("Az is: %.3f", accelZ);

        xAccelResult.setText(xOutput);
        if (accelX < -1) {
            xAccelResult.setTextColor(Color.RED);
        } else if (accelX > 1) {
            xAccelResult.setTextColor(Color.GREEN);
        } else {
            xAccelResult.setTextColor(Color.BLACK);
        }

        yAccelResult.setText(yOutput);
        if (accelY < -1) {
            yAccelResult.setTextColor(Color.RED);
        } else if (accelY > 1) {
            yAccelResult.setTextColor(Color.GREEN);
        } else {
            yAccelResult.setTextColor(Color.BLACK);
        }

        zAccelResult.setText(zOutput);
        if (accelZ < -1) {
            zAccelResult.setTextColor(Color.RED);
        } else if (accelZ > 1) {
            zAccelResult.setTextColor(Color.GREEN);
        } else {
            zAccelResult.setTextColor(Color.BLACK);
        }
    }

    private void refreshGyroDisplay() {
        String xOutput = String.format("Gx is: %.3f", gyroX);
        String yOutput = String.format("Gy is: %.3f", gyroY);
        String zOutput = String.format("Gz is: %.3f", gyroZ);

        xGyroResult.setText(xOutput);
        if (gyroX < -1) {
            xGyroResult.setTextColor(Color.RED);
        } else if (gyroX > 1) {
            xGyroResult.setTextColor(Color.GREEN);
        } else {
            xGyroResult.setTextColor(Color.BLACK);
        }

        yGyroResult.setText(yOutput);
        if (gyroY < -1) {
            yGyroResult.setTextColor(Color.RED);
        } else if (gyroY > 1) {
            yGyroResult.setTextColor(Color.GREEN);
        } else {
            yGyroResult.setTextColor(Color.BLACK);
        }

        zGyroResult.setText(zOutput);
        if (gyroZ < -1) {
            zGyroResult.setTextColor(Color.RED);
        } else if (gyroZ > 1) {
            zGyroResult.setTextColor(Color.GREEN);
        } else {
            zGyroResult.setTextColor(Color.BLACK);
        }
    }

    private void log() {
        if (logging)
        {
            String formattedTime = time.format("%Y-%m-%d %H:%M:%S");
            Log.i(TAG, formattedTime + "(accel): " + accelX + ", " + accelY + ", " + accelZ);
            logString = logString + (new Date()).getTime() + "," + accelX + "," + accelY + "," + accelZ + ","
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
            Toast.makeText(getApplicationContext(),
                    "Stopping Logging", Toast.LENGTH_SHORT).show();

            copyButton.setText("SII: Copying is Disabled");
            saveButton.setText("Save Accel Log");
            gyroCopyButton.setText("SII: Copying is Disabled");
            gyroSaveButton.setText("Save Gyro Log");

        } else {
            // reset logs
            logString = "";
            gyroLogString = "";

            logging = true;
            logButton.setText("Disable Logging");
            Log.i(TAG, "Starting Logging...");
            Toast.makeText(getApplicationContext(),
                    "Starting Logging", Toast.LENGTH_SHORT).show();

            copyButton.setText("Log Copying is Disabled");
            saveButton.setText("Log Saving is Disabled");
            gyroCopyButton.setText("Log Copying is Disabled");
            gyroSaveButton.setText("Log Saving is Disabled");
        }
    }

    public void copyAccelerationLog(View view) {
        if (!logging) {
            Toast.makeText(getApplicationContext(),
                    "SII: Copying is Disabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Log copying is disabled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyGyroscopeLog(View view) {
        if (!logging) {
            Toast.makeText(getApplicationContext(),
                    "SII: Copying is Disabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Log copying is disabled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveAccelerationLog(View view) {
        if (!logging) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/accelexp2");
            dir.mkdirs();
            String formattedTime = time.format("%Y-%m-%d_%H.%M.%S");
            Log.i(TAG, "saving: " + formattedTime + "-accel.csv");
            File file = new File(dir, formattedTime +"-accel.csv");

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.print("System Time (milliseconds),x,y,z,Human-Readable Time\n" + logString);
                pw.flush();
                pw.close();
                f.close();
                Log.i(TAG, "Saved Accel Log!");
                Toast.makeText(getApplicationContext(),
                        formattedTime + "-accel.csv saved!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "File not found. Is WRITE_EXTERNAL_STORAGE in manifest? "
                        + "Is USB storage on?");
                Toast.makeText(getApplicationContext(),
                        formattedTime + "-accel.csv Not Found! Check Manifest/USB.",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        "Issue Saving Accel Log!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "Log saving is disabled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveGyroscopeLog(View view) {
        if (!logging) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/accelexp2");
            dir.mkdirs();
            String formattedTime = time.format("%Y-%m-%d_%H.%M.%S");
            Log.i(TAG, "saving: " + formattedTime + "-gyro.csv");
            File file = new File(dir, formattedTime +"-gyro.csv");

            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.print("System Time (milliseconds),x,y,z,Human-Readable Time\n" + gyroLogString);
                pw.flush();
                pw.close();
                f.close();
                Log.i(TAG, "Saved Gyro Log!");
                Toast.makeText(getApplicationContext(),
                        formattedTime + "-gyro.csv saved!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "File not found. Is WRITE_EXTERNAL_STORAGE in manifest? "
                        + "Is USB storage on?");
                Toast.makeText(getApplicationContext(),
                        formattedTime + "-gyro.csv Not Found! Check Manifest/USB.",
                        Toast.LENGTH_SHORT).show();            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        "Issue Saving Gyroscope Log!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                "Log saving is disabled!", Toast.LENGTH_SHORT).show();
        }
    }

}