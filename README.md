Edward-Accelerometer-Experiment
===============================

*New!!*

*Uploading to online database now enabled!*

Simple Android experimental app for motion sensor data with following functionality:
- displays accelerometer readings in x, y, &amp; z directions
- displays gyroscope readings in x, y, &amp; z directions
- enable/disable logging
- readings can be shown in LogCat within the IDE
- while logging, the data is automatically uploaded to online database with description for easier retrival
- after finished logging, the logs can be copied to the Android system clipboard
- after finished logging, the logs can be saved to a Comma Separated File in the `/accelexp2` directory (where the directory ends up, whether in SD card or internal storage, depends on the device's configuration)

*Known Issues*
- Auto-rotation can cause the app to not work as expected
- MTP connection over USB could delay showing the created log files
- Graphing (largely commented out) still can't work without either bogging down the app or being stuck on intial pieces of data
