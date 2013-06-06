Sprockets
=========

Sprockets is a Java development library that currently provides the following features:

* Java interface for the [Google Places API](https://developers.google.com/places/)
* Misc. supporting classes (e.g. Substring, DayOfWeek)

Google Places API
-----------------

Full support for Place Search, Details, Photos, Autocomplete, and Query Autocomplete requests, including all parameters and returned fields.  Getting a list of places can be as easy as:

```java
List<Place> places = Places.textSearch(new Params().query("pizza near willis tower")).getResult();
```

More detailed searches can include lat/long with radius, specific types of places, keywords, price range, places that are open now, etc.  For each returned place, you can also retrieve its full details, reviews, photos, and events.

The Google Places API can return a lot of information about each place and most of the time you probably won't need every detail.  For maximum performance and minimum memory usage, you can specify which fields you want and limit the number of results.

```java
List<Place> places = Places.nearbySearch(new Params().location(47.60567, -122.3315).radius(5000)
        .keyword("swimming").openNow().maxResults(5), NAME, VICINITY, RATING, PHOTOS).getResult();
```

See the [Sprockets Javadoc][1] to learn more.

[1]: http://pushbit.github.io/sprockets/apidocs/index.html?net/sf/sprockets/google/Places.html

Start using Sprockets
---------------------

Sprockets is available in Maven Central.

```xml
<dependency>
    <groupId>net.sf.sprockets</groupId>
    <artifactId>sprockets</artifactId>
    <version>0.0.0</version>
</dependency>
```

If you're not using Maven, you can download the library jar and its dependencies below.

* [Sprockets 0.0.0](http://search.maven.org/remotecontent?filepath=net/sf/sprockets/sprockets/0.0.0/sprockets-0.0.0.jar)
* [Commons Configuration](https://commons.apache.org/proper/commons-configuration/download_configuration.cgi)
* [Commons Lang 2.x](https://commons.apache.org/proper/commons-lang/download_lang.cgi) (not 3.x)
* [Commons Logging](https://commons.apache.org/proper/commons-logging/download_logging.cgi)
* [Gson](https://code.google.com/p/google-gson/)
* [Guava](https://code.google.com/p/guava-libraries/)

Configuring Sprockets
-------------

Before you can successfully call any of the Google Places API methods, you must first add your [Google API key][2] to the library configuration.  See the [Sprockets][3] class description for instructions on configuring the library settings.

[2]: https://code.google.com/apis/console/
[3]: http://pushbit.github.io/sprockets/apidocs/index.html?net/sf/sprockets/Sprockets.html

Coming soon(ish)...
-------------------

* Android library project
    * Google Place search widget with autocomplete and results with photos
    * ImageView loader with caching, resizing and cropping on disk, and automatic view updates when the source changes
