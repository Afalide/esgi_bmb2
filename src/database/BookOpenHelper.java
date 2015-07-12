
package database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookOpenHelper extends SQLiteOpenHelper{

	private static final String tag = "BooksSqliteOpenHelper";
	private static final int DBVERSION = 9;
	
	public BookOpenHelper(Context context) {
		super(context, "bmb2.db", null, DBVERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(tag,"first database creation with version " + DBVERSION);
		recreateDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.v(tag,"database upgrade from " + oldVersion + " to " + newVersion);
		recreateDatabase(db);
	}
	
	private void recreateDatabase(SQLiteDatabase db){
		
		// check if the table exists

		Log.v(tag, "checking if the table books already exists");
		Cursor cur = db.rawQuery("select count(*) from sqlite_master where name='books'", null);
		cur.moveToFirst();
		double result = cur.getDouble(0);
		cur.close();
		Log.v(tag, "result: " + result);

		// if the table exists, delete it

		if (0 != result) {
			Log.v(tag, "the table exists, deleting it");
			if(!exec("DROP TABLE books", db)){
				Log.v(tag, "failed to drop table");
				return;
			}
		}else{
			Log.v(tag, "the table doesn't exist");
		}

		// create the table

		Log.v(tag, "creating table");
		if(!exec("CREATE TABLE books (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,author TEXT)", db)){
			Log.v(tag, "failed to create table");
			return;
		}
		
		Log.v(tag, "BMB2 database recreated");
	}
	
	private boolean exec(String sql, SQLiteDatabase db){
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.e(tag, "error on sql execution");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
