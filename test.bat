REM uninstall before test
call un
REM Turn off auto-rotation
adb shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0
REM Rotate to landscape
adb shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1
REM TDD
call gradlew :App:testDebug
if %errorlevel% neq 0 exit /b %errorlevel%
REM BDD
call gradlew app:connectedCheck
if %errorlevel% neq 0 exit /b %errorlevel%