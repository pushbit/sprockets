package net.sf.sprockets.sample.app.ui;

import android.app.Activity;
import android.os.Bundle;

import net.sf.sprockets.sample.R;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }
}
