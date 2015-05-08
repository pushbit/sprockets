Sprockets for Java [![Maven Central][9]][10]
============================================

Query the [Google Places API][1] and [Google Street View Image API][2] in Java.

* Features
    * [Google Places API](#google-places-api)
    * [Google Street View Image API](#google-street-view-image-api)
* [Install](#install)
* [Javadoc][3]

Google Places API
-----------------

Full support for Place Search, Details, Photos, Autocomplete, and Query Autocomplete requests, including all parameters and returned fields.  Getting a list of places can be as simple as:

```java
Places.textSearch(new Params().query("pizza near willis tower")).getResult();
```

More detailed searches can include lat/long with radius, specific types of places, keywords, price range, places that are open now, etc.  For each returned place, you can also retrieve its full details, reviews, and photos.

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

Install
-------

1\. Add the dependency.

```xml
	<dependency>
		<groupId>net.sf.sprockets</groupId>
		<artifactId>sprockets</artifactId>
		<version>2.4.0</version>
	</dependency>
```

2\. Add your [Google API key][5] to [sprockets.xml][4] and place it in the root of your application classpath (e.g. `src/` in a standard project or `src/main/resources/` in a Maven project).  See the [Sprockets][6] class description for more information about configuring the library settings.

[1]: https://developers.google.com/places/webservice/
[2]: https://developers.google.com/maps/documentation/streetview/
[3]: https://pushbit.github.io/sprockets/apidocs/
[4]: https://raw.githubusercontent.com/pushbit/sprockets/master/src/main/resources/net/sf/sprockets/sprockets.xml
[5]: https://console.developers.google.com/
[6]: https://pushbit.github.io/sprockets/apidocs/index.html?net/sf/sprockets/Sprockets.html
[7]: https://pushbit.github.io/sprockets/apidocs/index.html?net/sf/sprockets/google/Places.html
[8]: https://pushbit.github.io/sprockets/apidocs/index.html?net/sf/sprockets/google/StreetView.html
[9]: https://img.shields.io/maven-central/v/net.sf.sprockets/sprockets.svg
[10]: https://search.maven.org/#search|ga|1|g%3Anet.sf.sprockets%20a%3Asprockets
