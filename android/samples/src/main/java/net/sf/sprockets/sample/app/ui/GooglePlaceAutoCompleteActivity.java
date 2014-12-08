package net.sf.sprockets.sample.app.ui;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Toast;

import net.sf.sprockets.app.ui.SprocketsActivity;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.sample.R;
import net.sf.sprockets.widget.GooglePlaceAutoComplete;
import net.sf.sprockets.widget.GooglePlaceAutoComplete.OnPlaceClickListener;

import butterknife.InjectView;

import static android.view.Gravity.CENTER;
import static android.widget.Toast.LENGTH_LONG;

public class GooglePlaceAutoCompleteActivity extends SprocketsActivity {
    @InjectView(R.id.place)
    GooglePlaceAutoComplete place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_place_auto_complete);
        place.setOnPlaceClickListener(new OnPlaceClickListener() {
            @Override
            public void onPlaceClick(AdapterView<?> parent, Prediction place, int position) {
                /* normally do something with the clicked place, but here just toast it */
                Toast toast = Toast.makeText(GooglePlaceAutoCompleteActivity.this,
                        getString(R.string.place, place.getName(), place.getTypes()), LENGTH_LONG);
                toast.setGravity(CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
