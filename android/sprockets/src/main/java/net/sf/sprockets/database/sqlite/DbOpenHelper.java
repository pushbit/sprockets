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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.database.sqlite;

import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;

import static org.apache.commons.io.Charsets.UTF_8;

/**
 * SQLiteOpenHelper that executes raw resource SQL scripts to create and upgrade the database. By
 * default, the {@code R.raw.create_tables} and {@code R.raw.upgrade_tables_v}<b>{@code X}</b>
 * resources will be used, where <b>{@code X}</b> is the version of the database that is being
 * upgraded. You can also specify custom raw resource names with the overloaded constructors.
 * <p>
 * For example, if the database is currently at version 2 and a user installs an app update that
 * requires database version 4, and the default {@code upgradeResBaseName} of
 * {@code "upgrade_tables_v"} is being used, then the {@code R.raw.upgrade_tables_v2} and
 * {@code R.raw.upgrade_tables_v3} scripts will be executed to bring the existing database up to the
 * version 4 schema.
 * </p>
 * <p>
 * All statements in the resource scripts must end with a semicolon. If you have a nested statement
 * that ends with a semicolon, such as when creating a trigger, you can add an empty comment after
 * the nested statement semicolon, so that the unfinished outer statement is not executed at that
 * point. For example:
 * </p>
 * <pre>{@code
 * CREATE TRIGGER foo_updated AFTER UPDATE ON foo
 * BEGIN
 *     UPDATE foo SET bar = 'baz' WHERE _id = OLD._id;--
 * END;
 * }</pre>
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    private final Context mContext;
    /**
     * Resource ID for the create tables script.
     */
    private int mCreate;
    /**
     * Base name for the upgrade scripts. The version number of the database being upgraded will be
     * appended to this name.
     */
    private String mUpgrade;

    /**
     * Use the default create and upgrade resources, {@code R.raw.create_tables} and
     * {@code R.raw.upgrade_tables_v}<b>{@code X}</b>.
     *
     * @see SQLiteOpenHelper#SQLiteOpenHelper(Context, String, SQLiteDatabase.CursorFactory, int)
     */
    public DbOpenHelper(Context context, String name, int version) {
        this(context, name, version, 0, null);
    }

    /**
     * Use the raw resources for the create and upgrade scripts. See the class description for how
     * {@code upgradeResBaseName} is used.
     *
     * @param createResId        can be 0 to use the default resource name
     * @param upgradeResBaseName can be null to use the default resource name
     * @see SQLiteOpenHelper#SQLiteOpenHelper(Context, String, SQLiteDatabase.CursorFactory, int)
     */
    public DbOpenHelper(Context context, String name, int version, int createResId,
                        String upgradeResBaseName) {
        this(context, name, null, version, null, createResId, upgradeResBaseName);
    }

    /**
     * Use a custom CursorFactory and/or DatabaseErrorHandler.
     *
     * @see #DbOpenHelper(Context, String, int, int, String)
     */
    public DbOpenHelper(Context context, String name, CursorFactory factory, int version,
                        DatabaseErrorHandler errorHandler, int createResId,
                        String upgradeResBaseName) {
        super(context, name, factory, version, errorHandler);
        mContext = context.getApplicationContext();
        mCreate = createResId;
        mUpgrade = upgradeResBaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Resources res = mContext.getResources();
        if (mCreate == 0) {
            mCreate = res.getIdentifier("create_tables", "raw", mContext.getPackageName());
        }
        try {
            execScript(db, res, mCreate);
        } catch (IOException e) {
            throw new RuntimeException("creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Resources res = mContext.getResources();
        if (mUpgrade == null) {
            mUpgrade = "upgrade_tables_v";
        }
        for (int i = oldVersion; i < newVersion; i++) { // execute the upgrade scripts [old, new)
            try {
                execScript(db, res,
                        res.getIdentifier(mUpgrade + i, "raw", mContext.getPackageName()));
            } catch (IOException e) {
                throw new RuntimeException("upgrading database", e);
            }
        }
    }

    /**
     * Execute the statements in the resource script on the database. Each statement must end with a
     * semicolon.
     */
    private void execScript(SQLiteDatabase db, Resources res, int script) throws IOException {
        LineIterator lines = IOUtils.lineIterator(res.openRawResource(script), UTF_8);
        StringBuilder sql = new StringBuilder(2048); // enough capacity for a long statement
        try { // read each (potentially multi-line) statement and execute them one at a time
            while (lines.hasNext()) {
                String line = lines.next().trim();
                int length = line.length();
                if (length > 0) {
                    sql.append(line).append("\n");
                    if (line.charAt(length - 1) == ';') { // statement loaded
                        db.execSQL(sql.toString());
                        sql.setLength(0); // reset builder for a new statement
                    }
                }
            }
        } finally {
            lines.close();
        }
    }
}
