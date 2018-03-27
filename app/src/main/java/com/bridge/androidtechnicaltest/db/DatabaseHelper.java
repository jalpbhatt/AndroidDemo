package com.bridge.androidtechnicaltest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bridge.androidtechnicaltest.model.PupilDetails;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getName();
	private static final boolean DEBUG = true;

	private static final String DATABASE_NAME = "bridgetest.db";
	private static final int DATABASE_VERSION = 1;

	private static final String[] TABLES = {"Pupils"};
	private static final String[][] COLUMNS = {
		{"PupilId INTEGER", "Name TEXT", "Country TEXT", "Image TEXT", "Latitude DOUBLE", "Longitude DOUBLE"},
	};

	public static final String TABLE_NAME = "Pupils";

	public static final String COLUMN_PUPIL_ID = "PupilId";
	public static final String COLUMN_PUPIL_NAME = "Name";
	public static final String COLUMN_PUPIL_COUNTRY = "Country";
	public static final String COLUMN_PUPIL_lATITUDE = "Latitude";
	public static final String COLUMN_PUPIL_lONGITUDE = "Longitude";
	public static final String COLUMN_PUPIL_IMAGE = "Image";


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		for (int i = 0; i < TABLES.length; i++) {
			String table = TABLES[i];
			StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
			sb.append(table);
			sb.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT");

			for (int j = 0; j < COLUMNS[i].length; j++) {
				sb.append(",");
				sb.append(COLUMNS[i][j]);
			}
			sb.append(")");

			db.execSQL(sb.toString());
		}
	}

	private void dropTables(SQLiteDatabase db) {
		for (String table : TABLES) {
			String sql = "DROP TABLE IF EXISTS " + table;
			db.execSQL(sql);
		}
	}

	public void addPupil(PupilDetails details) {

		// Get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// Create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(COLUMN_PUPIL_ID, details.getPupilId());
		values.put(COLUMN_PUPIL_NAME, details.getName());
		values.put(COLUMN_PUPIL_COUNTRY, details.getCountry());
		values.put(COLUMN_PUPIL_IMAGE, details.getImage());
		values.put(COLUMN_PUPIL_lATITUDE, details.getLatitude());
		values.put(COLUMN_PUPIL_lONGITUDE, details.getLongitude());

		// Insert
		long newRowId = db.insert(TABLE_NAME,
				null,
				values);

		if (DEBUG) {
            Log.d(TAG, "Data inserted for pupil = " + details.getName() +
                    " is successful = " + (newRowId > 0 ? true : false));
		}
		// close the db
		db.close();

	}

	public boolean deletePupil(PupilDetails pupilDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowDeleteIdx = db.delete(TABLE_NAME, COLUMN_PUPIL_ID + " = ?",
                new String[]{String.valueOf(pupilDetails.getPupilId())});

        if (DEBUG) {
            Log.d(TAG, "Data deleted for pupil = " + pupilDetails.getName() +
                    " is successful = " + (rowDeleteIdx > 0 ? true : false));

            db.close();
        }
        return (rowDeleteIdx > 0 ? true : false);
    }

	public List<PupilDetails> getAllPupilList() {
		List<PupilDetails> pupilList = new ArrayList<PupilDetails>();

		String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_PUPIL_ID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

        PupilDetails details = null;
		if (cursor.moveToFirst()) {
			do {
				details = new PupilDetails();
				details.setPupilId(cursor.getInt(cursor.getColumnIndex(COLUMN_PUPIL_ID)));
				details.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PUPIL_NAME)));
				details.setCountry(cursor.getString(cursor.getColumnIndex(COLUMN_PUPIL_COUNTRY)));
				details.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_PUPIL_IMAGE)));
				details.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_PUPIL_lATITUDE)));
				details.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_PUPIL_lONGITUDE)));
                pupilList.add(details);
			} while (cursor.moveToNext());
		}

        // close db connection
        db.close();

		return pupilList;
	}

	public boolean hasData() {
		return  (DatabaseUtils.queryNumEntries(getReadableDatabase(),
				TABLE_NAME) > 0 ? true : false);
	}
}
