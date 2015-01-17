package net.sf.sprockets.sample.app.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.sf.sprockets.sample.R;

public class SamplesActivity extends ListActivity {
    private static final int[] sLabels = {R.string.navigation_drawer,
            R.string.panes_activity, R.string.fading_action_bar_scroll_listener,
            R.string.floating_header_scroll_listener, R.string.parallax_view_scroll_listener,
            R.string.translate_image_page_change_listener, R.string.google_place_auto_complete,
            R.string.google_places_loader, R.string.sprockets_preference_fragment};
    private static final Class<?>[] sActivities = {NavigationDrawerSampleActivity.class,
            PanesSampleActivity.class, FadingActionBarActivity.class,
            FloatingHeaderActivity.class, ParallaxViewActivity.class, TranslateImageActivity.class,
            GooglePlaceAutoCompleteActivity.class, GooglePlacesLoaderActivity.class,
            SettingsActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item);
        for (int label : sLabels) {
            adapter.add(getString(label));
        }
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        startActivity(new Intent(this, sActivities[position]));
    }
}
