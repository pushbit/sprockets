package net.sf.sprockets.sample.app.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import net.sf.sprockets.sample.R;
import net.sf.sprockets.widget.FadingActionBarScrollListener;

import static android.view.Window.FEATURE_ACTION_BAR_OVERLAY;

public class FadingActionBarActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_ACTION_BAR_OVERLAY); // should probably be in theme
        getActionBar().setDisplayShowHomeEnabled(false);
        setListAdapter(ArrayAdapter.createFromResource(this, R.array.numbers, R.layout.list_item));
        getListView().setOnScrollListener(new FadingActionBarScrollListener(this, true, true, 10));
    }
}
