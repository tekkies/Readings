**⚠️ This repository is no longer maintained. Development has moved to [https://github.com/ophelimos/Readings](https://github.com/ophelimos/Readings). Please visit the new repository for the latest updates.**

# Daily Bible Reading #

<a href="http://goo.gl/dHdjhS">
<img class="alignright" alt="Get it on Google Play" src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" width="129" height="45" />
</a>


Beta build available in the G+ community [(http://goo.gl/6yw8X9)](http://goo.gl/6yw8X9)


![alt tag](https://raw.github.com/tekkies/Readings/master/Screenshots%20in%20nexus.png)

# Code #

In the code you should find working (not necessarily textbook) examples of

* Google Esspresso BDD tests
* Goole App Analytics
* ViewPager
* Service 
  * Background MP3 playing
  * Audio-focus awareness
* Notifications
* Content provider client
  * The [sister app](https://play.google.com/store/apps/details?id=uk.co.tekkies.plugin.kjv) provides content.

# Build #

Build using Android Studio - Just open a project and point to this folder.

You do not need to clone the submodules.  It contains content I prefer not to share.  Without it, alternative sample content is shown (from the licensed folder).

## Libraries ##

Add libGoogleAnalyticsServices.jar to app/libs folder (create folder if required).  [Download](https://developers.google.com/analytics/devguides/collection/android/resources)

In Android Studio, the tree, go to app/libs, right click on  libGoogleAnalyticsServices.jar and click "Add as Library...".

Click Synchronise (Ctrl-Alt-Y) the project and build.

## Test automation ##
To run tests, simply right-click on app and choose "Run all tests"

When you run tests, (app/src/androidTest), you will need to edit the test configuration and set the "Specific instrumentation runner" to
com.google.android.apps.common.testing.testrunner.GoogleInstrumentationTestRunner for Espresso to drive the UI/BDD tests.

# Next #



1. Any whim that takes my fancy: RxJava? Jacoco? Google Play Game Services?
2. See http://creadings.uservoice.com/forums/172482-readings
