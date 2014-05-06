package se.haffatuben;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.OpenableColumns;
import android.util.Log;

/**
 * Database helper class to receive stations from the SQLite database
 * in assets folder. Because Android will not read databases directly from
 * the assets folder the database is first copied over to internal storage
 * if the database doesn't already exist there.
 * 
 * @author jonas
 *
 */
public class StationsAdapter {
	private static final String DATABASE_NAME = "stations.db";
	private static final String TABLE_NAME = "stations";
	private static final int DATABASE_VERSION = 2;
	private static final String TAG = "StationsAdapter";
	
	private Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public StationsAdapter(Context context) {
		this.context = context;
		if (!databaseExists(context)) {
			createDatabase(context);
		}
		this.dbHelper = new DatabaseHelper(context);
		this.db = this.dbHelper.getReadableDatabase();
	}
	
	/**
	 * Returns path to the data directory where the database is stored
	 * @return
	 */
	private static String getDbPath(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= 4.2) {
			return context.getApplicationInfo().dataDir + "/databases/";
		}
		return "/data/data/" + context.getPackageName() + "/databases/";
	}
	
	/**
	 * Create a new database on internal storage. This method
	 * will first check if the database exists before doing any
	 * changes. If no database exists the database from assets will
	 * be copied to internal storage.
	 * 
	 * @param context
	 */
	public static void createDatabase(Context context) {
		if (!databaseExists(context)) {
			try {
				copyDatabase(context);
			} catch (IOException e) {
				throw new Error("Failed to copy database.", e);
			}
		} else {
			Log.d(TAG, "Database exists");
		}
	}
	
	/**
	 * Check if database exists in data directory
	 * @return
	 */
	public static boolean databaseExists(Context context) {
		File db = new File(getDbPath(context) + DATABASE_NAME);
		return db.exists();
	}
	
	/**
	 * Delete database from data directory
	 */
	public static void deleteDatabase(Context context) {
		File file = new File(getDbPath(context));
		file.delete();
	}
	
	/**
	 * Copy database from assets folder to the data directory
	 */
	private static void copyDatabase(Context context) throws IOException {
		// check if databases directory exists and create it if it doesn't
		File d = new File(getDbPath(context));
		if (!d.exists()) { d.mkdir(); }
		
		String out = getDbPath(context) + DATABASE_NAME;
		InputStream input = context.getAssets().open(DATABASE_NAME);
		OutputStream output = new FileOutputStream(out);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
		output.flush();
		output.close();
		input.close();
		
		Log.d(TAG, "Created database");
	}
	
	/**
	 * Closes the database connection
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Returns a list of stations from the database that matched stationQuery
	 * @param stationQuery search term
	 * @return list of stations
	 */
	public List<Station> getStations(String stationQuery) {
		List<Station> matches = new ArrayList<Station>();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE name_upper LIKE ? LIMIT 3";
		stationQuery = "%" + stationQuery + "%";
		try {
			stationQuery = new String(stationQuery.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return matches;
		}
		try {
			Cursor cursor = db.rawQuery(query, new String []{ stationQuery.toUpperCase(Locale.US) });
			while (cursor.moveToNext()) {
				String id = Integer.toString(cursor.getInt(0));
				String name = cursor.getString(1);
				double lat = cursor.getDouble(2);
				double lng = cursor.getDouble(3);
				matches.add(new Station(name, id, lat, lng));
			}
		} catch (SQLException e) {
			Log.e(TAG, "Failed to query database");
		}
		return matches;
	}
	
	/**
	 * SQLite connection helper. This method will call createDatabase
	 * if the database doesn't already exist. If the database is old
	 * (i.e. the version doesn't match with DATABASE_VERSION) the old
	 * database will be deleted and replaced with a new one using
	 * createDatabase.
	 * 
	 * @author jonas
	 *
	 */
	public class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * Create a new database
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			createDatabase(context);
		}

		/**
		 * Delete old database and replace it with a new one
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			deleteDatabase(context);
			createDatabase(context);
		}
	}
}
