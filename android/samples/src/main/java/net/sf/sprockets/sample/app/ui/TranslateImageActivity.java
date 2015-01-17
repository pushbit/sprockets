package net.sf.sprockets.sample.app.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.sf.sprockets.app.ui.SprocketsActivity;
import net.sf.sprockets.app.ui.SprocketsFragment;
import net.sf.sprockets.sample.R;
import net.sf.sprockets.view.TranslateImagePageChangeListener;

import butterknife.InjectView;
import icepick.Icicle;

public class TranslateImageActivity extends SprocketsActivity {
    @InjectView(R.id.image)
    ImageView mImage;
    @InjectView(R.id.pager)
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_image);
        mPager.setAdapter(new Adapter());
        mPager.setOnPageChangeListener(new TranslateImagePageChangeListener(mPager, mImage));
    }

    private class Adapter extends FragmentPagerAdapter {
        private Adapter() {
            super(getFragmentManager());
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position + 1);
        }

    }

    public static class PageFragment extends SprocketsFragment {
        @InjectView(R.id.page)
        TextView mPageView;
        @Icicle
        int mPage;

        private static PageFragment newInstance(int page) {
            PageFragment frag = new PageFragment();
            frag.mPage = page;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
            return inflater.inflate(R.layout.page_number, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mPageView.setText(getString(R.string.page_n, mPage));
        }
    }
}
