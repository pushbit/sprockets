package net.sf.sprockets.sample.app.ui;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.sprockets.content.GooglePlacesLoader;
import net.sf.sprockets.google.LocalPlacesParams;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places.Response;
import net.sf.sprockets.sample.R;
import net.sf.sprockets.widget.GooglePlacesAdapter;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static net.sf.sprockets.google.Places.Request.NEARBY_SEARCH;

public class GooglePlacesLoaderActivity extends ListActivity
        implements LoaderCallbacks<Response<List<Place>>> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new GooglePlacesAdapter() {
            @Override
            public View getView(int position, Place place, View convertView, ViewGroup parent) {
                TextView view = (TextView) (convertView != null ? convertView
                        : getLayoutInflater().inflate(R.layout.list_item, parent, false));
                view.setText(place.getName());
                return view;
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Response<List<Place>>> onCreateLoader(int id, Bundle args) {
        return new GooglePlacesLoader<>(this, NEARBY_SEARCH, new LocalPlacesParams(this));
    }

    @Override
    public void onLoadFinished(Loader<Response<List<Place>>> loader, Response<List<Place>> resp) {
        if (resp != null) {
            ((GooglePlacesAdapter) getListAdapter()).swapPlaces(resp.getResult());
        } else {
            Toast.makeText(this, R.string.places_load_fail, LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Response<List<Place>>> loader) {
        ((GooglePlacesAdapter) getListAdapter()).swapPlaces(null);
    }
}
