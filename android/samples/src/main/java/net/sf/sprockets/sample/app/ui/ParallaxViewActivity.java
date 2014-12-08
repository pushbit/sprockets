package net.sf.sprockets.sample.app.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import net.sf.sprockets.sample.R;
import net.sf.sprockets.widget.ParallaxViewScrollListener;

public class ParallaxViewActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parallax_view);
        setListAdapter(ArrayAdapter.createFromResource(this, R.array.numbers, R.layout.list_item));
        getListView().setOnScrollListener(
                new ParallaxViewScrollListener(findViewById(R.id.parallax), 0.3f));
    }
}
