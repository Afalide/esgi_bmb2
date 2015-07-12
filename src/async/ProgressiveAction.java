package async;

import esgi.bmb2.R;
import fragments.ProgressFragment;
import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;



public abstract class ProgressiveAction extends AsyncTask<Void, Integer, Boolean> {
	
	private ProgressFragment _frag;
	
	public abstract void backgroundRun();
	
	
////////////////////////////////////////////////////////////////////////
	
	public ProgressFragment getProgressFragment(){
		return _frag;
	}
	
////////////////////////////////////////////////////////////////////////	
	
	public void launch(){
		
		Void params[] = null;
		execute(params);
	}
	
////////////////////////////////////////////////////////////////////////
		
	public void requestStop(){
		cancel(false);
	}

////////////////////////////////////////////////////////////////////////

	public void setProgressFragment(ProgressFragment frag){
		_frag = frag;
	}

////////////////////////////////////////////////////////////////////////

	public boolean isRunning(){
		if(    AsyncTask.Status.PENDING == getStatus() 
			|| AsyncTask.Status.RUNNING == getStatus()){
			
			return true;
		}
		return false;
	}
	
////////////////////////////////////////////////////////////////////////

	@Override
	protected Boolean doInBackground(Void... params) {
		backgroundRun();
		
		if(isCancelled()){
			Activity act = _frag.getActivity();
			if(null != act){
				Resources res = act.getResources();
				_frag.setTextInfos(res.getString(R.string.fragprogress_actioncancelled));
			}
		}
		
		_frag.setProgressbarInfinite(false);
			
		return null;
	}
	
	
}
