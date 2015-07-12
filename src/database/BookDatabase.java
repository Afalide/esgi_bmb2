
package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Provides useful function to access and modify the books database
 */
public class BookDatabase {

	private static final String tag = "BooksDatabase";
	private SQLiteDatabase _db;

	public BookDatabase(Context cnt) {
		_db = new BookOpenHelper(cnt).getWritableDatabase();
	}
	
	public void closeDb(){
		_db.close();
	}

	public void insertNewBook(String title, String author) {
		
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("author", author);

		long result = _db.insert("books", null, values);
		
		if (-1 == result) {
			Log.e(tag, "failed to insert new book");
			return;
		}

		Log.v(tag, "inserted new book with rowid " + result);
	}

	public Cursor getAllBooks() {
		Cursor ret = _db.rawQuery("SELECT _id,title,author FROM books", null);
		ret.moveToFirst();
		return ret;
	}
	
	public Cursor getBooks(String sql){
		Cursor ret = _db.rawQuery(sql,null);
		if(! ret.moveToFirst()){
			Log.v(tag, "info: cursor is empty");
		}
		return ret;
	}
	
	public Cursor rawQuery(String sql){
		return _db.rawQuery(sql, null);
	}
	
	public BookDefinition getBook(long bookId){
		Cursor cur = getBooks("SELECT _id,title,author FROM books WHERE _id = " + bookId );
		if(cur.getCount() != 1){
			Log.v(tag, "book with id " + bookId + " not found");
			return null;
		}
		
		return new BookDefinition(cur.getString(1), cur.getString(2), bookId);
	}
	
	public boolean updateBook(BookDefinition book){
		
		ContentValues values = new ContentValues();
		values.put("author", book.getAuthor());
		values.put("title", book.getTitle());
		
		if(1 != _db.update("books", values, "_id=?", new String[]{"" + book.getId()})){
			Log.v(tag,"failed to update book with id " + book.getId());
			return false;
		}
			
		return true;
	}
	
	public boolean deleteBook(long bookId){
		if(1 != _db.delete("books", "_id=?", new String[]{""+bookId})){
			Log.v(tag, "book id " + bookId + " not found, cannot delete it from db");
			return false;
		}
		
		return true;
	}
	
	public boolean deleteBook(BookDefinition book){
		return deleteBook(book.getId());
	}
	
	public void truncateDatabase(){
		_db.delete("books", null, null);
	}
}
