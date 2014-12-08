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

package net.sf.sprockets.view;

import android.view.ActionMode;

/**
 * Starts {@link ActionMode}s and can hide them and then restore them with the same instance state.
 */
public interface ActionModePresenter {
    /**
     * If an ActionMode is currently showing, hide it.
     *
     * @return true if an ActionMode was hidden
     */
    boolean hideActionMode();

    /**
     * If an ActionMode was previously hidden, restore it with the same instance state.
     *
     * @return true if an ActionMode was restored
     */
    boolean restoreActionMode();
}
