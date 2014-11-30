call adb install -r "app\build\outputs\apk\app-debug-unaligned.apk"
adb shell am start -n uk.co.tekkies.readings/uk.co.tekkies.readings.activity.ReadingsActivity 
pause