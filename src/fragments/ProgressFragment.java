package fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import esgi.bmb2.R;

public class ProgressFragment extends Fragment {
	
	private static final String tag = "ProgressFragment";
	private TextView _infosText;
	private ProgressBar _progressBar;
	private Button _cancelButton;
	
	public ProgressFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View ret = inflater.inflate(R.layout.frag_progress, container, false);
			
		_infosText = (TextView) ret.findViewById(R.id.fragprogress_infostv);
		_progressBar = (ProgressBar) ret.findViewById(R.id.fragprogress_pbar);
		_cancelButton = (Button) ret.findViewById(R.id.fragprogress_cancelbt);
		
		return ret;
	}
	
	public void setProgressbarPct(final int percent){
		
		Activity main = getActivity();
		
		if(null == main){
			Log.v(tag,"null activity, cannot set progressbar pct");
			return;
		}
		
		main.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_progressBar.setProgress(percent);
			}
		});
	}
	
	public void setProgressbarInfinite(boolean b){
		_progressBar.setIndeterminate(b);
	}
	
	public void setTextInfos(final String txt){
		
		Activity main = getActivity();
		
		if(null == main){
			Log.v(tag,"null activity, cannot set progressbar pct");
			return;
		}
		
		main.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_infosText.setText(txt);
			}
		});
	}
	
	public void setOnCancelClickListener(OnClickListener listener){
		_cancelButton.setOnClickListener(listener);
	}
	
	public void setCancelVisible(boolean visible){
		if(visible){
			_cancelButton.setVisibility(Button.VISIBLE);
		}else{
			_cancelButton.setVisibility(Button.INVISIBLE);
		}
	}
}
