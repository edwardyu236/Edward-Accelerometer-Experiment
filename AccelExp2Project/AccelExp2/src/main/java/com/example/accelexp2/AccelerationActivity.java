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

        import java.util.Date;

public class AccelerationActivity extends Activity {
    private TextView xAccelResult;
    private TextView yAccelResult;
    private TextView zAccelResult;

    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private float accelX, accelY, accelZ;

    private static final String TAG = "AccelerationActivity";
    private boolean logging = false;
    private Button logButton;
    private Time time;

    private String logString;
    private String initialTime;


    private long systemTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);

        // set up sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

        // set up the text views
        xAccelResult = (TextView) findViewById(R.id.x_accel_result);
        yAccelResult = (TextView) findViewById(R.id.y_accel_result);
        zAccelResult = (TextView) findViewById(R.id.z_accel_result);
        xAccelResult.setText("Ax is: ±x.xxx");
        yAccelResult.setText("Ay is: ±x.xxx");
        zAccelResult.setText("Az is: ±x.xxx");

        // set up for logging
        logging = false;
        logButton = (Button) findViewById(R.id.log_button);
        logButton.setText("Enable Logging");
        logString = "";

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationListener, accelerationSensor,
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

    public void toggleLogging(View view) {
        if (logging) {

            logging = false;
            logButton.setText("Enable Logging");
            Toast.makeText(getApplicationContext(),
                    "Stopping Logging", Toast.LENGTH_SHORT).show();

        } else {
            // reset logs
            logString = "";
            logging = true;
            logButton.setText("Disable Logging");
            Log.i(TAG, "Starting Logging...");
            Toast.makeText(getApplicationContext(),
                    "Starting Logging", Toast.LENGTH_SHORT).show();

            initialTime = (new Date()).getTime() + "";

        }
    }

}