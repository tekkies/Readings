
set DELAY=5

echo %DELAY%

adb uninstall uk.co.tekkies.readings

for %%f in ("%HOMEPATH%\Dropbox\Android\Readings\uk.co.tekkies.readings.api14\*.apk") DO call _upgrade.bat "%%f" %DELAY%
REM for %%f in ("%HOMEPATH%\Dropbox\Android\Readings\uk.co.tekkies.readings.api3\*.apk") DO call adb install -r "%%f" & adb shell am start -n uk.co.tekkies.readings/uk.co.tekkies.readings.ReadingsActivity & ping -n %DELAY% 127.0.0.1