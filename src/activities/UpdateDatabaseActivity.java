package activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import async.DownloadBooksAction;
import async.FactoryResetAction;
import async.ProgressiveAction;
import esgi.bmb2.R;
import fragments.ProgressFragment;
import globals.Globals;

public class UpdateDatabaseActivity extends Activity {

	private static final String tag = "UpdateDatabaseActivity";
	private ProgressFragment _progressFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_database);
		_progressFragment = null;
	}
	
	@Override
	protected void onDestroy() {
		//emulate a cancel click to request a cancel of any executing action
		//onCancelClick();
		
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		showProgressFragment();
		super.onResume();
	}
	
	public void onResetFactoryClick(View v){
		startNewAction(new FactoryResetAction());
	}
	
	public void onDownloadClick(View v){
		
		Dialog quantityDialog = createQuantityDialog();
		quantityDialog.show();
	}
	
	public void startNewAction(ProgressiveAction action){
		
		_progressFragment.setCancelVisible(true);
		Globals g = Globals.getInstance();
		
		if(null == g.databaseAction || !g.databaseAction.isRunning()){
			
			Log.v(tag,"starting new action");
			g.databaseAction = action;
			
		}else{
			
			Log.v(tag,"an action is still running, cannot create new one");
			return;
		}
		
		g.databaseAction.setProgressFragment(_progressFragment);
		g.databaseAction.launch();
	}
	
	public void showProgressFragment(){
		
		//inflate the fragment and put it on the activity
		
		ProgressFragment frag = new ProgressFragment();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		int containerId = R.id.activityupdatedatabase_fragcontainer;
		if(null != fragmentManager.findFragmentById(containerId)){
			fragmentTransaction.replace(containerId, frag);
			Log.v(tag, "replaced fragment");
		}else{
			fragmentTransaction.add(containerId, frag);
			Log.v(tag, "inserted fragment");
		}
		fragmentTransaction.commit();
		fragmentManager.executePendingTransactions();
		
		//the fragment is visible, add the listener for it's cancel button
		
		frag.setOnCancelClickListener(new OnCancelClickListener());
		_progressFragment = frag;
		
		//the first time the fragment is shown, hide the cancel button
		Globals g = Globals.getInstance();
		if(null == g.databaseAction)
			_progressFragment.setCancelVisible(false);
		
		//update any pending action with the new fragment
		if(null != g.databaseAction)
			g.databaseAction.setProgressFragment(_progressFragment);
		
	}
	
	public void onCancelClick(){
		Globals g = Globals.getInstance();
		
		if(null == g.databaseAction){
			Log.v(tag,"null action, cannot request stop");
			return;
		}
		
		if(! g.databaseAction.isRunning()){
			Log.v(tag,"current action is not running, cannot request stop");
			return;
		}
		
		Log.v(tag,"requesting cancel of action");
		g.databaseAction.requestStop();
	}
	
	@SuppressLint("InflateParams")
	private Dialog createQuantityDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Set the view
		
		LayoutInflater inflater = getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.dialog_quantity_to_dl, null);
		builder.setView(dialogView);
		
		//find child views

		final SeekBar seekbar  =  (SeekBar)  dialogView.findViewById(R.id.dialogquantitytodl_scrollbar);
		final TextView quantity = (TextView) dialogView.findViewById(R.id.dialogquantitytodl_bookqty);
		
		//set the seekbar range 0..500 and default value
		
		seekbar.setMax(500);
		seekbar.setProgress(200);
		
		//add seekbar listener
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar s) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar s) {
			}
			
			@Override
			public void onProgressChanged(SeekBar s, int progress, boolean user) {
				quantity.setText(""+progress);
			}
		});	
		
		// Add the buttons
		
		builder.setPositiveButton(R.string.dialogquantitytodl_positive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dint, int id) {
				int quantity = seekbar.getProgress();
				startNewAction(new DownloadBooksAction(quantity));
			}
		});
			
		builder.setNegativeButton(R.string.dialogquantitytodl_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do nothing, simply dismiss the dialog
			}
		});
		
		// Create the AlertDialog
		
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
	private class OnCancelClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			onCancelClick();
		}
	}
}
