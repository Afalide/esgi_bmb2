
package fragments;

import java.util.Locale;

import esgi.bmb2.R;
import globals.Globals;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class BookListFilterFragment extends AbstractBookListFragment{

	public static final String tag = "BookListFilterFragment";
//	private int _currentFilterId;
	private EditText _searchBox;
	private Spinner _spinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		setLayout(R.layout.frag_book_list_filter);
		View ret = super.onCreateView(inflater, container, savedInstanceState);
		
//		setSqlWhereClause("WHERE author like '%bbbb%'");
//		setSqlOrderByClause(null);
		
		_searchBox = (EditText) ret.findViewById(R.id.fragbooklistfilter_searchbox);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fragbooklistfilter_spinnerstrings, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		_spinner = (Spinner) ret.findViewById(R.id.fragbooklistfilter_spinner);
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(new SpinnerListener());
		
		Button goButton = (Button) ret.findViewById(R.id.fragbooklistfilter_gobutton);
		goButton.setOnClickListener(new GoButtonListener());
		
		//restore the fragment with it's previous values
		
		Globals g = Globals.getInstance();
		
		if(null != g.fragFilterText)
			_searchBox.setText(g.fragFilterText);
		
		_spinner.setSelection(g.fragFilterId);
		setFilter(g.fragFilterId);
		
		return ret;
	}
	
	public void setFilter(int filterId){
		
		if(null == _searchBox){
			Log.e(tag,"searchBox is null");
			return;
		}
		
		Log.v(tag,"setting filter " + filterId);
		
		String search = _searchBox.getText().toString().trim().toLowerCase(Locale.getDefault());
		
		switch (filterId) {
		case 1:
			//title
			setSqlWhereClause("where title like '%"+ search +"%'");
			break;

		case 2:
			//author
			setSqlWhereClause("where author like '%"+ search +"%'");
			break;
			
		default:
			setSqlWhereClause(null);
			break;
		}

		Globals g = Globals.getInstance();
		g.fragFilterId = filterId;
		g.fragFilterText = search;
		
		refreshBookList();
	}
	
	@Override
	public void onResetFilters() {
		super.onResetFilters();
		_searchBox.setText("");
		_spinner.setSelection(0);
		setFilter(0);
	}
	
	class SpinnerListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			Log.v(tag, "selected filter id " + id + " at pos " + pos);
			setFilter(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.v(tag, "nothing selected in filter spinner");
		}
	}
	
	class GoButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			setFilter(Globals.getInstance().fragFilterId);
		}
	}

}
