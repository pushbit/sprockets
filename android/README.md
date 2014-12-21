Sprockets for Android [![Maven Central][5]][6] [![Android Arsenal][3]][4]
=========================================================================

Extend base components, use widgets, call utility methods, and reference common resources.

* [Features](#features)
* [Install](#install)
* [Javadoc][1]
    * See also Sprockets for Java [Javadoc][2]

Features
--------

Below is a sample of the available classes and resources. See the [Javadoc][1] for the complete reference.

* app
    * [VersionedApplication][100]
        * Implement onVersionChanged to be notified when the app runs with a new version for the first time.
    * [NavigationDrawerActivity][101]
        * Manages the ActionBar during navigation drawer events.
    * [BaseNavigationDrawerFragment][102]
        * Set items from an array, highlight selected item, and respond to clicks.
    * [PanesActivity][103]
        * Manages two fragment panes that are either displayed next to each other or in a ViewPager, depending on screen size.
    * [SprocketsPreferenceFragment][104]
        * Sets preference values as their summary.
* content
    * [DbContentProvider][200]
        * ContentProvider with a SQLite database back end that implements all common database operations and notifies observers of changes.
    * [GooglePlacesLoader][201]
        * Loader that sends requests to the Google Places API and provides the responses.
    * [LocalCursorLoader][202]
        * Provides the current location to implementations before performing the cursor query.
* database.sqlite
    * [DbOpenHelper][300]
        * Executes raw resource SQL scripts to create and upgrade the database.
* location
    * [Locations][400]
        * Get the current location in one method call.
* preference
    * [Prefs][500]
        * Get and set SharedPreferences values in one method call.
* widget
    * [GooglePlaceAutoComplete][600]
        * AutoCompleteTextView that provides local suggestions from the Google Places API.
    * [FadingActionBarScrollListener][601]
        * Fades the ActionBar title and background from transparent to opaque while scrolling down the list.
    * [FloatingHeaderScrollListener][602]
        * Slides a View that floats above your list header(s) up and down along with the scrolling of the list.
    * [ParallaxViewScrollListener][603]
        * Synchronises the scrolling of a View with a ListView, at a speed relative to the list scrolling speed.
* res
    * drawable
        * card
            * ViewGroup background for a card with automatic content padding and background highlighting when selected.
    * dimen
        * cards_parent_margin, cards_sibling_margin, text_card_max_width, min_touch_size
        * padding{,_tiny,_small,_medium,_large}
    * string
        * add, edit, delete, refresh, search, share, done, discard, cancel, etc.
    * style
        * Card.Title, Card.Title.Light, Card.Text, Card.Text.Medium, etc.

Install
-------

(Requires *Android Support Repository* and *Google Repository* in the SDK Manager.)

[Sample build.gradle](samples/build.gradle)

1\. Add the dependency.

```groovy
    compile 'net.sf.sprockets:sprockets-android:2.0.0'
```

2\. Ensure the `buildTypes` have `minifyEnabled true`, download [sprockets-rules.pro][10], and add it to `proguardFiles`.

3\. Tell ProGuard to ignore duplicate files.

```groovy
    packagingOptions {
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/services/javax.annotation.processing.Processor'
    }
```

4\. Ensure AndroidManifest.xml is set up for Google Play Services. Within `<application>`:

```xml
    <meta-data
        android:name="com.google.android.gms.version"
        android:value="@integer/google_play_services_version"/>
```

5\. (Optional) Download [sprockets.xml][11] to `src/main/resources/`.

* If you will use Google APIs, add your [Google API key][12].

[1]: https://pushbit.github.io/sprockets/android/apidocs/
[2]: https://pushbit.github.io/sprockets/java/apidocs/
[3]: https://img.shields.io/badge/Android%20Arsenal-Sprockets-brightgreen.svg?style=flat
[4]: https://android-arsenal.com/details/1/1243
[5]: https://img.shields.io/maven-central/v/net.sf.sprockets/sprockets-android.svg
[6]: https://search.maven.org/#search|ga|1|g%3Anet.sf.sprockets%20a%3Asprockets-android

[10]: https://raw.githubusercontent.com/pushbit/sprockets/master/android/sprockets/sprockets-rules.pro
[11]: https://raw.githubusercontent.com/pushbit/sprockets/master/java/src/main/resources/net/sf/sprockets/sprockets.xml
[12]: https://console.developers.google.com/

[100]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/app/VersionedApplication.html
[101]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/app/ui/NavigationDrawerActivity.html
[102]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/app/ui/BaseNavigationDrawerFragment.html
[103]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/app/ui/PanesActivity.html
[104]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/app/ui/SprocketsPreferenceFragment.html

[200]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/content/DbContentProvider.html
[201]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/content/GooglePlacesLoader.html
[202]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/content/LocalCursorLoader.html

[300]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/database/sqlite/DbOpenHelper.html

[400]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/location/Locations.html

[500]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/preference/Prefs.html

[600]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/widget/GooglePlaceAutoComplete.html
[601]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/widget/FadingActionBarScrollListener.html
[602]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/widget/FloatingHeaderScrollListener.html
[603]: https://pushbit.github.io/sprockets/android/apidocs/index.html?net/sf/sprockets/widget/ParallaxViewScrollListener.html
