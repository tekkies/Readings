call adb install -r "%1" 
ECHO ErrorLevel=%ERRORLEVEL%
adb shell am start -n uk.co.tekkies.readings/uk.co.tekkies.readings.ReadingsActivity 
adb shell am start -n uk.co.tekkies.readings/uk.co.tekkies.readings.activity.ReadingsActivity
ping -n %2 127.0.0.1
adb shell screencap -p /mnt/sdcard/sc.png
adb pull /mnt/sdcard/sc.png "%1.png"