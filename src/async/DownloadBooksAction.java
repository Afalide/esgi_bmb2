package async;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import database.BookDatabase;
import database.BookDefinition;
import database.BooksJsonParser;
import esgi.bmb2.R;

public class DownloadBooksAction extends ProgressiveAction {

	private static final String tag = "DownloadBooksAction";
	private int _quantity;
	
	public DownloadBooksAction(int bookQuantity) {
		_quantity = bookQuantity;
		
		if(_quantity <= 0)
			_quantity = 200;
	}
	
	@Override
	public void backgroundRun() {
		
		Activity caller = getProgressFragment().getActivity();
		if(null == caller){
			Log.v(tag,"caller is null, cannot get context for database");
			return;
		}
		
		Resources res = caller.getResources();
		
		//download the json
		
		getProgressFragment().setProgressbarInfinite(true);
		String jsonData = getJsonData(_quantity);
		if(null == jsonData){
			Log.v(tag,"failed to get json data");
			return;
		}
		
		//parse the downloaded JSON data
		
		if(isCancelled())
			return;
		
		getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressjsonparse));
		BooksJsonParser parser = new BooksJsonParser();
		List<BookDefinition> books  = parser.extractBooksFromJson(jsonData);
		if(null == books){
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressjsonparseerror));
			Log.v(tag,"error on json parsing");
			return;
		}
		getProgressFragment().setProgressbarInfinite(false);
		
		//load the books into the database
		
		if(isCancelled())
			return;
		
		String updateBookDb = res.getString(R.string.activityupdatedatabase_progressupdatedb);
		getProgressFragment().setTextInfos(updateBookDb);
		int totalBooks = books.size();
		if(0 == totalBooks){
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressjsonparseerror));
			Log.v(tag,"no book found in json data");
			return;
		}
		
		BookDatabase db = new BookDatabase(caller);
		db.truncateDatabase();
		float pctRatio = 100.0f / (float) totalBooks;
		int i=0;
		
		for(BookDefinition book : books){
			
			if(isCancelled()){
				db.closeDb();
				return;
			}
			
			db.insertNewBook(book.getTitle(), book.getAuthor());
			getProgressFragment().setTextInfos(updateBookDb + i + '/' + totalBooks);
			getProgressFragment().setProgressbarPct((int) ((++i) * pctRatio));
			
			//Log.v(tag,"inserted book " + book.getTitle() + " - " + book.getAuthor());
		}
		
		db.closeDb();
		getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressdone));
		Log.v(tag,"done");		
	}
	
	private String getJsonData(int maxBooks) {
		
		if(maxBooks <= 0)
			maxBooks = 200;
		
		Activity activity = getProgressFragment().getActivity();
		if(null == activity){
			Log.v(tag,"null activity, cannot start Json download");
			return null;
		}
		
		Resources res = activity.getResources();
		StringBuilder ret = new StringBuilder();
		
		try {
			
			String progressDlJson = res.getString(R.string.activityupdatedatabase_progressdljson);
			getProgressFragment().setTextInfos(progressDlJson);
			
			URL url = new URL("http://opendata.paris.fr/api/records/1.0/search?dataset=les-titres-les-plus-pretes&rows=" + maxBooks);
			HttpURLConnection client = (HttpURLConnection) url.openConnection();
			client.setReadTimeout(3000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

			char buffer[] = new char[200];
			int amountRead = 0;
			int totalRead = 0;
			
			while( -1 != (amountRead = reader.read(buffer))){
				
				if(isCancelled())
					return null;
				
				totalRead += amountRead;
				ret.append(buffer, 0, amountRead);
				getProgressFragment().setTextInfos(progressDlJson + totalRead + " bytes");
			}

			client.disconnect();
			reader.close();
			
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressneterror));
			return ret.toString();
		}
		
		catch(SocketTimeoutException timeout) {
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressnettimeout));
			Log.v(tag, "timeout while trying to download data");
			return null;
		}
		
		catch(UnknownHostException uhost){
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressneterror));
			Log.v(tag,"cannot resolve host, check internet connection");
			return null;
		}
		
		catch (Exception e) {
			getProgressFragment().setTextInfos(res.getString(R.string.activityupdatedatabase_progressjsonerror));
			Log.e(tag, "failed to get json data: " + e.getClass().getName());
			e.printStackTrace();
			return null;
		}
	}
}
