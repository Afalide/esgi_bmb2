package async;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import database.BookDatabase;
import database.BookDefinition;
import database.BooksJsonParser;
import esgi.bmb2.R;

public class FactoryResetAction extends ProgressiveAction {

	private static final String tag = "FactoryResetAction";

	@Override
	public void backgroundRun() {
		
		Activity activity = getProgressFragment().getActivity();
		if(null == activity){
			Log.v(tag,"activity is null, cannot start reset action");
			return;
		}
		
		// open the file

		Resources res = activity.getResources();
		BufferedReader reader = null;

		try {
			InputStream is = res.getAssets().open("opendata.json");
			reader = new BufferedReader(new InputStreamReader(is));
		} catch (Exception e) {
			Log.e(tag, "failed to open restore file");
			e.printStackTrace();
			return;
		}

		// process the file

		StringBuilder jsonData = new StringBuilder();

		while (true) {

			if(isCancelled())
				return;
			
			try {
				String str = reader.readLine();
				if (null == str)
					break;
	
				jsonData.append(str);

			} catch (Exception e) {
				Log.e(tag, "error while reading restore file");
				e.printStackTrace();
				return;
			}
		}

		// get a set of books

		BooksJsonParser parser = new BooksJsonParser();
		ArrayList<BookDefinition> books = parser.extractBooksFromJson(jsonData.toString());

		if (null == books) {
			Log.e(tag, "failed to extract json from file");
			return;
		}

		// erase the books table

		BookDatabase db = new BookDatabase(activity);
		db.truncateDatabase();

		// build the books database

		String resettingBooks = res.getString(R.string.activityupdatedatabase_progressreset);
		int totalBooks = books.size();
		int i = 0;

		for (BookDefinition book : books) {
			
			if(isCancelled()){
				db.closeDb();
				return;
			}
			
			db.insertNewBook(book.getTitle(), book.getAuthor());
			getProgressFragment().setTextInfos(resettingBooks + (++i) + '/' + totalBooks);
			getProgressFragment().setProgressbarPct((i*100)/totalBooks);
		}
		
		db.closeDb();
		getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressdone));
		return;
	}
}
