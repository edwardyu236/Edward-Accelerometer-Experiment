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
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.GraphView.GraphViewData;
        import com.jjoe64.graphview.GraphViewSeries;
        import com.jjoe64.graphview.LineGraphView;

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
    private String initialTime;

    private Button copyButton;
    private Button gyroCopyButton;
    private Button saveButton;
    private Button gyroSaveButton;

    private GraphView graphView;
    private LinearLayout graphContainingLayout;
    private GraphViewSeries accelXSeries;
    private GraphViewSeries accelYSeries;
    private GraphViewSeries accelZSeries;
    private GraphViewSeries gyroXSeries;
    private GraphViewSeries gyroYSeries;
    private GraphViewSeries gyroZSeries;

    private long systemTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);

        // set up sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // set up the text views
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

        // set up for logging
        logging = false;
        logButton = (Button) findViewById(R.id.log_button);
        logButton.setText("Enable Logging");
        logString = "";
        gyroLogString = "";

        // set up the buttons for copying and saving
        copyButton = (Button) findViewById(R.id.accel_copy_button);
        copyButton.setText("Copy Accel Log");
        gyroCopyButton = (Button) findViewById(R.id.gyro_copy_button);
        gyroCopyButton.setText("Copy Gyro Log");

        saveButton = (Button) findViewById(R.id.accel_save_button);
        saveButton.setText("Save Accel Log");
        gyroSaveButton = (Button) findViewById(R.id.gyro_save_button);
        gyroSaveButton.setText("Save Gyro Log");

        accelXSeries = new GraphViewSeries(new GraphViewData[]{});
        accelYSeries = new GraphViewSeries(new GraphViewData[]{});
        accelZSeries = new GraphViewSeries(new GraphViewData[]{});
        gyroXSeries = new GraphViewSeries(new GraphViewData[]{});
        gyroYSeries = new GraphViewSeries(new GraphViewData[]{});
        gyroZSeries = new GraphViewSeries(new GraphViewData[]{});

        graphView = new LineGraphView(this, "Graph");
        graphView.addSeries(accelXSeries);
        graphView.addSeries(accelYSeries);
        graphView.addSeries(accelZSeries);
        graphContainingLayout = (LinearLayout) findViewById(R.id.graphContainingLayout);
        graphContainingLayout.addView(graphView);

//        new Thread() {
//            public void run() {
//                Network.sendSQL("select * from gsensor");
//            }
//        }.start();

//        new Thread() {
//            public void run() {
//                Network.sendSQL("INSERT INTO asensor (st, x, y, z, ht, description)\n" +
//                        "VALUES ('stTestOnAndroid','xTest','yTest','zTest','htTest','desTestOnAndroid');");
//                Network.sendSQL("INSERT INTO gsensor (st, x, y, z, ht, description)\n" +
//                        "VALUES ('stTestOnAndroid','xTest','yTest','zTest','htTest','desTestOnAndroidGYRO');");
//            }
//        }.start();


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
            systemTime = (new Date()).getTime();
            time = new Time();
            time.setToNow();
            // TODO: fix the graph!
//            accelXSeries.appendData(new GraphViewData(systemTime, accelX), false, 1000000);
//            accelYSeries.appendData(new GraphViewData(systemTime, accelY), false, 1000000);
//            accelZSeries.appendData(new GraphViewData(systemTime, accelZ), false, 1000000);
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
            new Thread() {
                public void run() {
                    String formattedTime = time.format("%Y-%m-%d %H:%M:%S");
                    Log.i(TAG, formattedTime + "(accel): " + accelX + ", " + accelY + ", " + accelZ);
                    String systemTimeString = systemTime + "";
                    logString = logString + systemTimeString + "," + accelX + "," + accelY + "," + accelZ + ","
                            + formattedTime + "\n";
                    Network.addToAccelerometerDatabase(systemTimeString, accelX + "", accelY + "", accelZ + "", formattedTime, initialTime);
                }
            }.start();

        }
    }

    private void gyroscopeLog() {
        if (logging)
        {
            new Thread() {
                public void run() {
                    String formattedTime = time.format("%Y-%m-%d %H:%M:%S");
                    Log.i(TAG, formattedTime + "(gyro): " + gyroX + ", " + gyroY + ", " + gyroZ);
                    String systemTimeString = (new Date()).getTime() + "";
                    gyroLogString = gyroLogString + systemTimeString + ","
                            + gyroX + ", " + gyroY + ", " + gyroZ + ","
                            + formattedTime + "\n";
                    Network.addToGyroscopeDatabase(systemTimeString, accelX + "", accelY + "", accelZ + "", formattedTime, initialTime);
                }
            }.start();

        }
    }

    public void toggleLogging(View view) {
        if (logging) {

            logging = false;
            logButton.setText("Enable Logging");
            Toast.makeText(getApplicationContext(),
                    "Stopping Logging", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getApplicationContext(),
                    "Starting Logging", Toast.LENGTH_SHORT).show();

            initialTime = (new Date()).getTime() + "";

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
                    "System Time (milliseconds),x,y,z,Human-Readable Time\n" + logString);
            clipboard.setPrimaryClip(clip);
            Log.i(TAG, "Copied Accel Log!");
            Toast.makeText(getApplicationContext(),
                    "Acceleration Log Copied!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Log copying is disabled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyGyroscopeLog(View view) {
        if (!logging) {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("log",
                    "System Time (milliseconds),x,y,z,Human-Readable Time\n" + gyroLogString);
            clipboard.setPrimaryClip(clip);
            Log.i(TAG, "Copied Gyro Log!");
            Toast.makeText(getApplicationContext(),
                    "Gyroscope Log Copied!", Toast.LENGTH_SHORT).show();
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