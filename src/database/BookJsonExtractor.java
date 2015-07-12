
package database;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class BookJsonExtractor {

	private static final String tag = "BooksJSON";

	/**
	 * Builds an array of books, based on the given json file data.
	 * 
	 * @param jsonData
	 *            - The JSON to parse
	 * @return - An ArayLisy of BookDefinition. BookDefinition is a <br>
	 *         structure class which contains the title and the author.
	 */
	public ArrayList<BookDefinition> extractBooksFromJson(String jsonData) {
		
		JSONObject obj = null;
		JSONArray records = null;

		try {
			obj = new JSONObject(jsonData);
			records = obj.getJSONArray("records");
		} catch (Exception e) {
			Log.e(tag, "error when trying to get root json objects");
			e.printStackTrace();
			return null;
		}

		// fill the list with nice books

		ArrayList<BookDefinition> list = 
				new ArrayList<BookDefinition>(records.length());

		for (int i = 0; i < records.length(); i++) {

			String title = null;
			String author = null;

			try {

				JSONObject book = records.getJSONObject(i);
				JSONObject fields = book.getJSONObject("fields");
				title = fields.getString("titre");
				author = fields.getString("auteur");

			} catch (Exception e) {
				Log.e(tag, "exception when trying to parse json for object "
						+ i);
				e.printStackTrace();
			}

			if (null == author)
				author = "<Auteur Inconnu>";

			if (null == title)
				title = "<Titre Inconnu>";

			Log.v(tag, "book " + i + ":");
			Log.v(tag, "  author: " + author);
			Log.v(tag, "  title:  " + title);

			list.add(new BookDefinition(title, author));
		}

		return list;
	}
}
