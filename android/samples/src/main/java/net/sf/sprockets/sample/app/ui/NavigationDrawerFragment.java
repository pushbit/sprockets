package net.sf.sprockets.sample.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import net.sf.sprockets.app.ui.BaseNavigationDrawerFragment;
import net.sf.sprockets.sample.R;

import static android.widget.Toast.LENGTH_SHORT;

public class NavigationDrawerFragment extends BaseNavigationDrawerFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            /* provide the navigation drawer items and highlight an imaginary selected one */
            setItems(R.array.nav_items).setSelectedItemResId(R.string.nav_item2);
            showSettings(true).showHelp(true).showFeedback(true);
        }
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id, int resId) {
        super.onListItemClick(list, view, position, id, resId);
        /* normally navigate to the selected item, but here just toast it */
        Toast.makeText(a, resId, LENGTH_SHORT).show();
    }
}
