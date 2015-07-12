
package fragments;

import esgi.bmb2.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookListAllFragment extends AbstractBookListFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		setLayout(R.layout.frag_book_list_all);
		View ret = super.onCreateView(inflater, container, savedInstanceState);
		
//		setSqlWhereClause(null);
//		setSqlOrderByClause(null);
		refreshBookList();
		
		return ret;
	}
	
	@Override
	public void onResetFilters() {
		super.onResetFilters();
		refreshBookList();
	}
}
