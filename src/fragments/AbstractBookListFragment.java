package fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import database.BookDatabase;
import database.BookDefinition;
import esgi.bmb2.R;
import globals.Globals;

public abstract class AbstractBookListFragment extends Fragment{
	
	private static final String tag = "AbstractBookListFragment";
	private int _layout;
	private ListView _listView;
	private BookDatabase _booksDb;
	private CursorAdapter _cursorAdapter;
//	private String _sqlWhere;
//	private String _sqlOrderBy;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setSqlOrderByClause(null);
//		setSqlWhereClause(null);
		_booksDb = new BookDatabase(getActivity());
		Cursor          booksCursor = getCustomCursor();
		String[]        from        = { "title", "author" };
		int[]           to          = { R.id.list_item_book_title, R.id.list_item_book_author };
		_cursorAdapter              = new SimpleCursorAdapter(getActivity(), R.layout.list_item_book, booksCursor, from, to, 0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		_booksDb.closeDb();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View fragView = inflater.inflate(_layout, container, false);
		
		_listView = (ListView) getActivity().findViewById(R.id.activity_book_list_lview);
		_listView.setAdapter(_cursorAdapter);
		_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long bookId) {
				showEditBookPopup(bookId);
			}
		});
		
		registerForContextMenu(_listView);
		return fragView;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.book_list_popup_long_press, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    
		switch (item.getItemId()) {
	       	
	    	case R.id.book_list_popup_menu_edit:
	    		showEditBookPopup(info.id);
	            return true;
	            
	        case R.id.book_list_popup_menu_delete:
	            _booksDb.deleteBook(info.id);
	            refreshBookList();
	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void setLayout(int layout){
		_layout = layout;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		refreshBookList();
	}
	
	public String getSqlWhereClause(){
		Globals g = Globals.getInstance();
		if(null == g.listWhereClause)
			return "";
		
		return g.listWhereClause;
	}
	
	public String getSqlOrderByClause(){
		Globals g = Globals.getInstance();
		if(null == g.listOrderByClause)
			return "";
		
		return g.listOrderByClause;
	}
	
	public void setSqlWhereClause(String where){
		Globals.getInstance().listWhereClause = where;
	}
	
	public void setSqlOrderByClause(String orderBy){
		Globals.getInstance().listOrderByClause = orderBy;
	}
	
	public Cursor getCustomCursor(){
		String sql = "SELECT _id,title,author FROM books " + getSqlWhereClause() + " " + getSqlOrderByClause();
		
		Log.v(tag, "sql: " + sql);
		
		Cursor cur = _booksDb.rawQuery(sql);
		return cur;
	}
	
	public void refreshBookList(){
		_cursorAdapter.changeCursor(getCustomCursor());
	}
	
	@SuppressLint("InflateParams")
	public void showEditBookPopup(long bookId){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Set the view
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.dialog_edit_book, null);
		builder.setView(dialogView);
		
		//Pre-fill the author and title fields
		
		final BookDefinition book = _booksDb.getBook(bookId);
		final EditText titleText = (EditText) dialogView.findViewById(R.id.activity_book_list_popup_nb_title);
		final EditText authorText = (EditText) dialogView.findViewById(R.id.activity_book_list_popup_nb_author);
		titleText.setText(book.getTitle());
		authorText.setText(book.getAuthor());
		
		// Add the buttons
		
		builder.setPositiveButton(R.string.dialogeditbook_positive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dint, int id) {
				BookDatabase db = new BookDatabase(getActivity());
				book.setTitle(titleText.getText().toString());
				book.setAuthor(authorText.getText().toString());
				db.updateBook(book);
				db.closeDb();
				refreshBookList();
			}
		});
		
		builder.setNegativeButton(R.string.dialogeditbook_negative, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//do nothing
			}
		});
		
		// Create the AlertDialog
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void onResetFilters(){
	}
}
