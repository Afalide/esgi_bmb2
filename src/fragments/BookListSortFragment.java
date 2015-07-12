
package fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import esgi.bmb2.R;
import globals.Globals;

public class BookListSortFragment extends AbstractBookListFragment{
	
	private static final String tag = "BookListSortFragment";
	private Spinner _spinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		setLayout(R.layout.frag_book_list_sort);
		View ret = super.onCreateView(inflater, container, savedInstanceState);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fragbooklistfilter_spinnerstrings, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		_spinner = (Spinner) ret.findViewById(R.id.fragbooklistsort_sortbyspinner);
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(new SpinnerListener());
		
//		setSqlWhereClause(null);
//		setSqlOrderByClause(null);
//		setSort(0);
		
		_spinner.setSelection(Globals.getInstance().fragSortId);
		
		//setSort(Globals.getInstance().fragSortId);
		
		refreshBookList();
		return ret;
	}
	
	public void setSort(int sortId){

		switch (sortId) {
		case 1:
			//title
			setSqlOrderByClause("ORDER BY title");
			break;

		case 2:
			//author
			setSqlOrderByClause("ORDER BY author");
			break;
			
		default:
			setSqlOrderByClause(null);
			break;
		}
		
		Globals.getInstance().fragSortId = sortId;
		
		refreshBookList();
	}
	
	@Override
	public void onResetFilters() {
		super.onResetFilters();
		_spinner.setSelection(0);
		setSort(0);
	}
	
	class SpinnerListener implements AdapterView.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			Log.v(tag, "selected sort id " + id + " at pos " + pos);
			setSort(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.v(tag, "nothing selected in sort spinner");
		}
	}
}
