package edu.santarosa.szcgat.thorn;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ThornDatabase extends SQLiteOpenHelper {

	private static ThornDatabase thornDB = null;

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "thorn.db";
	private static final String TABLE_GIF = "gifs";

	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_FILENAME = "gif_filename";

	private ThornDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static ThornDatabase getInstance(Context context) {
		if (thornDB == null) {
			thornDB = new ThornDatabase(context);
		}
		return thornDB;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE = "create table " + TABLE_GIF + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_FILENAME
				+ " text not null);";
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("thorn", "Upgrading DB from " + oldVersion + " to " + newVersion);
		db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_GIF);
		onCreate(db);
	}

	public Gif addGif(String filename) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues newGif = new ContentValues();
		newGif.put(COLUMN_FILENAME, filename);

		long id = db.insert(TABLE_GIF, null, newGif);
		db.close();

		return new Gif(id, filename);
	}

	public Gif getGif(long id) {
		String[] allColumns = { COLUMN_ID, COLUMN_FILENAME };

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_GIF, allColumns, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursorToGif(cursor);
	}

	public void deleteGif(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GIF, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}

	public List<Gif> getAllGifs() {
		String[] allColumns = { COLUMN_ID, COLUMN_FILENAME };

		SQLiteDatabase db = this.getWritableDatabase();

		List<Gif> gifUris = new ArrayList<Gif>();
		Cursor cursor = db.query(TABLE_GIF, allColumns, null, null, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			gifUris.add(cursorToGif(cursor));
			cursor.moveToNext();
		}

		cursor.close();
		db.close();
		return gifUris;
	}

	private Gif cursorToGif(Cursor cursor) {
		Gif gif = new Gif(cursor.getLong(0), cursor.getString(1));
		return gif;
	}

}