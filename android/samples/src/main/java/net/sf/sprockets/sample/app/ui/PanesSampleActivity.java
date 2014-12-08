package net.sf.sprockets.sample.app.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.sf.sprockets.app.ui.PanesActivity;
import net.sf.sprockets.app.ui.SprocketsFragment;
import net.sf.sprockets.sample.R;

import icepick.Icicle;

public class PanesSampleActivity extends PanesActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultContentView();
    }

    @Override
    public Fragment getFragment(int pane) {
        return PaneFragment.newInstance(pane);
    }

    public static class PaneFragment extends SprocketsFragment {
        @Icicle
        int mPane;

        private static PaneFragment newInstance(int pane) {
            PaneFragment frag = new PaneFragment();
            frag.mPane = pane;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
            View view = inflater.inflate(R.layout.pane, container, false);
            ((TextView) view.findViewById(R.id.text)).setText(getString(R.string.pane, mPane));
            return view;
        }
    }
}
