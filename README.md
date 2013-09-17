Sprockets
=========

Sprockets is a Java development library that provides a Java interface for the [Google Places][1] and [Google Street View Image][2] APIs.

* Features
    * [Google Places API](#google-places-api)
    * [Google Street View Image API](#google-street-view-image-api)
* [Download and Configure](#download-and-configure)
* [Javadoc][3]

Google Places API
-----------------

Full support for Place Search, Details, Photos, Autocomplete, and Query Autocomplete requests, including all parameters and returned fields.  Getting a list of places can be as simple as:

```java
Places.textSearch(new Params().query("pizza near willis tower")).getResult();
```

More detailed searches can include lat/long with radius, specific types of places, keywords, price range, places that are open now, etc.  For each returned place, you can also retrieve its full details, reviews, photos, and events.

The Google Places API can return a lot of information about each place and most of the time you probably won't need every detail.  For maximum performance and minimum memory usage, you can specify which fields you want and limit the number of results.

```java
Places.nearbySearch(new Params().location(47.60567, -122.3315).radius(5000)
        .keyword("swimming").openNow().maxResults(5),
        NAME, VICINITY, RATING, PHOTOS).getResult();
```

[Places Javadoc][7]

Google Street View Image API
----------------------------

Download a Google Street View Image by supplying a lat/long or location name.

```java
StreetView.image(new Params().location("18 Rue Cujas, Paris, France")).getResult();
```

For fine control of the camera, you can also specify the heading, pitch, and field of view.

```java
StreetView.image(new Params().location(40.748769, -73.985332)
        .heading(210).pitch(33).fov(110)).getResult();
```

[StreetView Javadoc][8]

Download and Configure
----------------------

Sprockets is available in [Maven Central][4].

```xml
<dependency>
    <groupId>net.sf.sprockets</groupId>
    <artifactId>sprockets</artifactId>
    <version>1.0.0</version>
</dependency>
```

Before calling any of the Google Places API methods, you must first add your [Google API key][5] to the library configuration.  See the [Sprockets][6] class description for instructions on configuring the library settings.

[1]: https://developers.google.com/places/
[2]: https://developers.google.com/maps/documentation/streetview/
[3]: http://pushbit.github.io/sprockets/java/apidocs/
[4]: https://search.maven.org/#artifactdetails|net.sf.sprockets|sprockets|1.0.0|jar
[5]: https://code.google.com/apis/console/
[6]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/Sprockets.html
[7]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/google/Places.html
[8]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/google/StreetView.html
