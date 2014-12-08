/*
 * Copyright 2013-2014 pushbit <pushbit@gmail.com>
 * 
 * This file is part of Sprockets.
 * 
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.test;

import static net.sf.sprockets.google.Places.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Place.Photo;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;

import org.junit.Test;

public class PlacesTest {
	@Test
	public void testNearbySearch() throws IOException {
		Response<List<Place>> resp =
				Places.nearbySearch(new Params().location(40.758897, -73.985126).keyword("pizza"));
		assertEquals(OK, resp.getStatus());
		List<Place> places = resp.getResult();
		assertNotNull(places);
		assertTrue(places.size() > 0);
		Place place = places.get(0);
		assertTrue(place.getName().length() > 0);
	}

	@Test
	public void testTextSearch() throws IOException {
		Response<List<Place>> resp =
				Places.textSearch(new Params().query("pizza near times square"));
		assertEquals(OK, resp.getStatus());
		List<Place> places = resp.getResult();
		assertNotNull(places);
		assertTrue(places.size() > 0);
		Place place = places.get(0);
		assertTrue(place.getName().length() > 0);
	}

	@Test
	public void testRadarSearch() throws IOException {
		Response<List<Place>> resp =
				Places.radarSearch(new Params().location(40.758897, -73.985126).keyword("pizza"));
		assertEquals(OK, resp.getStatus());
		List<Place> places = resp.getResult();
		assertNotNull(places);
		assertTrue(places.size() > 0);
		Place place = places.get(0);
		assertTrue(place.getPlaceId().getId().length() > 0);
	}

	@Test
	public void testAutocomplete() throws IOException {
		Response<List<Prediction>> resp = Places.autocomplete(
				new Params().location(40.758897, -73.985126).query("john's piz"));
		assertEquals(OK, resp.getStatus());
		List<Prediction> predictions = resp.getResult();
		assertNotNull(predictions);
		assertTrue(predictions.size() > 0);
		Prediction prediction = predictions.get(0);
		assertTrue(prediction.getName().length() > 0);
	}

	@Test
	public void testQueryAutocomplete() throws IOException {
		Response<List<Prediction>> resp = Places.queryAutocomplete(
				new Params().location(40.758897, -73.985126).query("pizza near tim"));
		assertEquals(OK, resp.getStatus());
		List<Prediction> predictions = resp.getResult();
		assertNotNull(predictions);
		assertTrue(predictions.size() > 0);
		Prediction prediction = predictions.get(0);
		assertTrue(prediction.getName().length() > 0);
	}

	@Test
	public void testDetails() throws IOException {
		/* search */
		Response<List<Place>> search = Places.textSearch(
				new Params().query("pizza near times square"));
		assertEquals(OK, search.getStatus());
		List<Place> places = search.getResult();
		assertNotNull(places);
		assertTrue(places.size() > 0);
		Place place = places.get(0);
		assertTrue(place.getPlaceId().getId().length() > 0);
		/* details */
		Response<Place> details = Places.details(new Params().placeId(place.getPlaceId().getId()));
		assertEquals(OK, details.getStatus());
		place = details.getResult();
		assertNotNull(place);
		assertTrue(place.getName().length() > 0);
	}

	@Test
	public void testPhoto() throws IOException {
		Response<List<Place>> search = Places.textSearch(
				new Params().query("pizza near times square"));
		assertEquals(OK, search.getStatus());
		List<Place> places = search.getResult();
		assertNotNull(places);
		assertTrue(places.size() > 0);
		boolean tested = false;
		for (Place place : places) {
			List<Photo> photos = place.getPhotos();
			if (photos != null && photos.size() > 0) {
				Photo photo = photos.get(0);
				assertNotNull(photo);
				assertTrue(photo.getReference().length() > 0);
				Response<InputStream> resp = Places.photo(
						new Params().reference(photo.getReference()).maxWidth(160).maxHeight(120));
				assertEquals(OK, resp.getStatus());
				InputStream in = resp.getResult();
				assertNotNull(in);
				byte[] b = new byte[8192];
				while (in.read(b) != -1) {
				}
				in.close();
				tested = true;
				break;
			}
		}
		assertTrue(tested);
	}
}
