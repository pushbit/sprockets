package net.sf.sprockets.sample.app;

import net.sf.sprockets.app.VersionedApplication;
import net.sf.sprockets.preference.Prefs;
import net.sf.sprockets.sample.R;

public class SamplesApplication extends VersionedApplication {
    @Override
    public void onVersionChanged(int oldCode, String oldName, int newCode, String newName) {
        if (oldCode == 0) {
            Prefs.putString(this, "pref", getString(R.string.some_text));
        }
    }
}
