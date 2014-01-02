# Daily Bible Reading #

<a href="http://goo.gl/dHdjhS">
<img class="alignright" alt="Get it on Google Play" src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" width="129" height="45" />
</a>


Beta build available in the G+ community [(http://goo.gl/6yw8X9)](http://goo.gl/6yw8X9)


![alt tag](https://raw.github.com/tekkies/Readings/master/Screenshots%20in%20nexus.png)


# Build #

Build using [Eclipse/ADT](http://developer.android.com/sdk/installing/installing-adt.html) - Just import the project into the Eclipse workspace.

You do not need to clone the submodules.  It contains content I prefer not to share.  Without it, alternative sample content is shown (from the licensed folder).

## Libraries ##

Add libGoogleAnalyticsServices.jar to libs folder.  [Download](https://developers.google.com/analytics/devguides/collection/android/resources)

[](http://developer.android.com/sdk/installing/installing-adt.html "Eclipse/ADT")

# ToDo #

## Passage Activity ##
- Remove summary action bar icon
## Reading Activity ##
- Add about-transaltion action bar icon to reading activity
- Add basic play button to open media
- Basic handling of translations
 - 1 installed at a time?
 - dropdown on 
## Open source ##
- Move summary text into content provider (or submodule)
- Move reading plan into submodule
 - Create alternative fallback reading plan
## General ##
- day/night
  - http://stackoverflow.com/questions/2651360/how-to-provide-animation-when-calling-another-activity-in-android
## Future ##
- Chain MP3 playing
  - Autoscroll?
  - TTS announce 
- NIV
 - Get install stats for leverge for content provider?
- Customise passage list icons
 - Checkbox for read
  - Sync
