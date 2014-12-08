package net.sf.sprockets.sample.app.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.sf.sprockets.sample.R;
import net.sf.sprockets.widget.FloatingHeaderScrollListener;

public class FloatingHeaderActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_header);
        setListAdapter(ArrayAdapter.createFromResource(this, R.array.numbers, R.layout.list_item));
        ListView view = getListView();
        view.addHeaderView(getLayoutInflater().inflate(R.layout.list_header, view, false));
        view.setOnScrollListener(new FloatingHeaderScrollListener(findViewById(R.id.header)));
    }
}
