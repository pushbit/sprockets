/*
 * Copyright 2014 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.widget;

import android.database.DataSetObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;

import java.util.Collections;
import java.util.HashSet;

/**
 * Forwards {@link OnScrollListener} events to the added listeners.
 */
public class ListScrollListeners extends HashSet<OnScrollListener> implements OnScrollListener {
    /**
     * Add listeners later.
     */
    public ListScrollListeners() {
    }

    /**
     * Start with these listeners.
     */
    public ListScrollListeners(OnScrollListener... listeners) {
        super(listeners.length);
        Collections.addAll(this, listeners);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (OnScrollListener listener : this) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int first, int visible, int total) {
        for (OnScrollListener listener : this) {
            listener.onScroll(view, first, visible, total);
        }
    }

    /**
     * Registers itself as a DataSetObserver for the list's adapter.
     */
    public static abstract class ObservingScrollListener extends DataSetObserver
            implements OnScrollListener {
        private boolean mObserving;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (!mObserving) {
                Adapter adapter = view.getAdapter();
                if (adapter != null) {
                    adapter.registerDataSetObserver(this);
                    mObserving = true;
                }
            }
        }
    }

    /**
     * Approves or rejects requests from OnScrollListeners to execute their onScroll implementation.
     */
    public interface OnScrollApprover {
        /**
         * Return true to approve the listener's request to execute its onScroll implementation.
         */
        boolean onScroll(OnScrollListener listener, AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount);
    }
}
