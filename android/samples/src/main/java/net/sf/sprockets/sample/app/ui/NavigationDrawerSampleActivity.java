package net.sf.sprockets.sample.app.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import net.sf.sprockets.app.ui.NavigationDrawerActivity;
import net.sf.sprockets.sample.R;

import butterknife.InjectView;

import static android.view.Gravity.START;

public class NavigationDrawerSampleActivity extends NavigationDrawerActivity {
    @InjectView(R.id.root)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        setDrawerLayout(mDrawerLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.openDrawer(START);
            }
        }, 500L);
    }
}
