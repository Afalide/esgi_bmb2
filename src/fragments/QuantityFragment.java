
package fragments;

import esgi.bmb2.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class QuantityFragment extends Fragment {

	private SeekBar _seekbar;
	private TextView _quantity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//inflate the fragment
		
		super.onCreateView(inflater, container, savedInstanceState);
		View ret = inflater.inflate(R.layout.dialog_quantity_to_dl, container, false);
		
		//find child views
		
//		_seekbar  = (SeekBar)  ret.findViewById(R.id.fragquantitytodl_scrollbar);
//		_quantity = (TextView) ret.findViewById(R.id.fragquantitytodl_bookqty);
		
		//set the seekbar range 0..500
		
		_seekbar.setMax(500);
		
		//add seekbar listener
		
		_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar s) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar s) {
			}
			
			@Override
			public void onProgressChanged(SeekBar s, int progress, boolean user) {
				_quantity.setText(""+progress);
			}
		});	
		
		return ret;
	}
	
	public int getValue(){
		return _seekbar.getProgress();
	}
}
