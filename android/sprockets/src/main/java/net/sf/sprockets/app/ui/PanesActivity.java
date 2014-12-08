/*
 * Copyright 2013 pushbit <pushbit@gmail.com>
 * 
 * This file is part of Sprockets.
 * 
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.app.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import net.sf.sprockets.R;
import net.sf.sprockets.app.Fragments;
import net.sf.sprockets.util.Elements;

import static butterknife.ButterKnife.findById;

/**
 * Manages two fragment panes. When the screen configuration is large enough, both panes are
 * displayed together. Otherwise the panes are displayed in a {@link ViewPager}.
 * <p>
 * You must either provide your own layout that includes a version with the ViewPager and another
 * with two panes, by calling {@link #setContentView(int, int, int, int)}, or you may call
 * {@link #setDefaultContentView()} to use the library's default layout for the panes.
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/PanesSampleActivity.java" target="_blank">Sample Implementation</a>
 * </p>
 */
public abstract class PanesActivity extends SprocketsActivity {
    /**
     * Tag for the first fragment.
     */
    private static final String PANE_1 = PanesActivity.class.getName() + ".pane_1";
    /**
     * Tag for the second fragment.
     */
    private static final String PANE_2 = PanesActivity.class.getName() + ".pane_2";
    private static final String[] sPanes = {PANE_1, PANE_2};

    /**
     * Use the default layout for the panes, {@code R.layout.panes}.
     */
    public void setDefaultContentView() {
        setContentView(R.layout.panes, R.id.panes, R.id.pane1, R.id.pane2);
    }

    /**
     * Use your own layout for the panes.
     *
     * @param pagerId {@code R.id} value for the ViewPager in the single pane layout
     * @param pane1Id {@code R.id} value for the first pane in the multi-pane layout
     * @param pane2Id {@code R.id} value for the second pane in the multi-pane layout
     */
    public void setContentView(int layoutResId, int pagerId, int pane1Id, int pane2Id) {
        setContentView(layoutResId);
        Fragment pane1 = findFragmentByPane(1);
        Fragment pane2 = findFragmentByPane(2);
        ViewPager pager = findById(this, pagerId);
        /* do we need to move the fragments between the single and multi-pane layouts? */
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = null;
        if (pane2 == null) {
            pane2 = getFragment(2);
        } else if (pane2.getId() != (pager != null ? pagerId : pane2Id)) {
            ft = fm.beginTransaction().remove(pane2); // remove in reverse to preserve indices
        }
        if (pane1 == null) {
            pane1 = getFragment(1);
        } else if (pane1.getId() != (pager != null ? pagerId : pane1Id)) {
            if (ft == null) {
                ft = fm.beginTransaction();
            }
            ft.remove(pane1);
        }
        if (ft != null) {
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions(); // force removes so can add to a different container
        }
        /* add the fragments to the panes */
        if (pager != null) {
            pager.setAdapter(new PanesAdapter(pane1, pane2));
        } else {
            ft = null;
            if (pane1.getId() != pane1Id) {
                ft = Fragments.open(this).add(pane1Id, pane1, PANE_1);
            }
            if (pane2.getId() != pane2Id) {
                if (ft == null) {
                    ft = Fragments.open(this);
                }
                ft.add(pane2Id, pane2, PANE_2);
            }
            if (ft != null) {
                ft.commitAllowingStateLoss();
            }
        }
    }

    /**
     * Get the fragment to be displayed in the pane.
     *
     * @param pane must be 1 or 2
     */
    public abstract Fragment getFragment(int pane);

    /**
     * Get the fragment that is displayed in the pane.
     *
     * @param pane must be 1 or 2
     * @return null if a fragment hasn't been added to the pane yet
     */
    public <T extends Fragment> T findFragmentByPane(int pane) {
        String tag = Elements.get(sPanes, pane - 1);
        return tag != null ? (T) getFragmentManager().findFragmentByTag(tag) : null;
    }

    /**
     * Adds fragments to the pager.
     */
    private class PanesAdapter extends PagerAdapter {
        private final Fragment mPane1;
        private final Fragment mPane2;
        private FragmentTransaction mFt;
        private Fragment mPrimary;

        private PanesAdapter(Fragment pane1, Fragment pane2) {
            mPane1 = pane1;
            mPane2 = pane2;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment item = position == 0 ? mPane1 : mPane2;
            if (item.getId() != container.getId()) {
                if (mFt == null) {
                    mFt = getFragmentManager().beginTransaction();
                }
                mFt.add(container.getId(), item, sPanes[position]);
            }
            if (item != mPrimary) {
                item.setMenuVisibility(false);
                item.setUserVisibleHint(false);
            }
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (mPrimary != object) {
                if (mPrimary != null) { // hide old
                    mPrimary.setMenuVisibility(false);
                    mPrimary.setUserVisibleHint(false);
                }
                mPrimary = (Fragment) object;
                if (mPrimary != null) { // show new
                    mPrimary.setMenuVisibility(true);
                    mPrimary.setUserVisibleHint(true);
                }
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            if (mFt != null) {
                mFt.commitAllowingStateLoss();
                mFt = null;
                getFragmentManager().executePendingTransactions();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }
    }
}
