REM Turn off auto-rotation
adb shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0
REM Rotate to landscape
adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1
REM Test
adb shell am instrument -w uk.co.tekkies.readings.test/com.google.android.apps.common.testing.testrunner.GoogleInstrumentationTestRunner | tee espresso-results.txt
