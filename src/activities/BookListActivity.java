package activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import database.BookDatabase;
import esgi.bmb2.R;
import fragments.AbstractBookListFragment;
import fragments.BookListAllFragment;
import fragments.BookListFilterFragment;
import fragments.BookListSortFragment;
import globals.Globals;

public class BookListActivity extends Activity {

	private static final String tag =   "BookListActivity";
	private int 						_currentTab;
	private AbstractBookListFragment 	_currentFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
		
//		_btTabAll    = (Button) findViewById(R.id.activitybooklist_taballbt);
//		_btTabFilter = (Button) findViewById(R.id.activitybooklist_tabfilterbt);
//		_btTabSort   = (Button) findViewById(R.id.activitybooklist_tabsortbt);
		
		if(null == savedInstanceState){
			this.setTab(1);
		}else{
			if(! savedInstanceState.containsKey("tab")){
				this.setTab(1);
			}else{
				_currentTab = savedInstanceState.getInt("tab");
				this.setTab(_currentTab);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_book_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		
		case R.id.menubooklist_newbook:
			onMenuNewBookClick();
			return true;
			
		case R.id.menubooklist_resetfilters:
			resetFilters();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", _currentTab);
	}
	
	public void setTab(int tab){
		
		if(_currentTab == tab)
			return;
		
		_currentTab = tab;
		
		Log.v(tag, "settab " + tab);
		AbstractBookListFragment frag = null;
		
		switch (tab) {
		
		case 1:
			frag = new BookListAllFragment();
			break;
		
		case 2:
			frag = new BookListFilterFragment();
			break;

		case 3:
			frag = new BookListSortFragment();
			break;
			
		default:
			frag = new BookListAllFragment();
			break;
		}

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		int containerId = R.id.fragment_container;
		
		if(null != fragmentManager.findFragmentById(containerId)){
			fragmentTransaction.replace(containerId, frag);
		}else{
			fragmentTransaction.add(containerId, frag);
		}
		
		fragmentTransaction.commit();
		fragmentManager.executePendingTransactions();
		
		_currentFragment = frag;
		Log.v(tag, "set fragment " + frag);
	}
	
	public void onTabAllClick(View v){
		setTab(1);
	}
	
	public void onTabFilterClick(View v){
		setTab(2);
	}
	
	public void onTabSortClick(View v){
		setTab(3);
	}
	
	public void resetFilters(){
		Globals g = Globals.getInstance();
		g.fragFilterId = 0;
		g.fragFilterText = "";
		g.fragSortId = 0;
		_currentFragment.setSqlWhereClause(null);
		_currentFragment.setSqlOrderByClause(null);
		_currentFragment.onResetFilters();
	}
	
	@SuppressLint("InflateParams")
	public void onMenuNewBookClick(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Set the view
		
		LayoutInflater inflater = getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.dialog_edit_book, null);
		final Activity context = this;
		builder.setView(dialogView);
		
		// Add the buttons
		
		builder.setPositiveButton(R.string.dialogeditbook_positive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dint, int id) {
				BookDatabase db = new BookDatabase(context);
				EditText titleText = (EditText) dialogView.findViewById(R.id.activity_book_list_popup_nb_title);
				EditText authorText = (EditText) dialogView.findViewById(R.id.activity_book_list_popup_nb_author);
				db.insertNewBook(titleText.getText().toString(), authorText.getText().toString());
				db.closeDb();
				updateListView();
			}
		});
			
		builder.setNegativeButton(R.string.dialogeditbook_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do nothing, simply dismiss the dialog
			}
		});
		
		// Create the AlertDialog
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public void updateListView(){
		_currentFragment.refreshBookList();
	}
}
