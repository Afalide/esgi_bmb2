
package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import esgi.bmb2.R;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    
    public void onButtonUpdateDatabaseClicked(View v){
		Intent i = new Intent(this, UpdateDatabaseActivity.class);
		startActivity(i);
	}
	
	public void onButtonBooksListClicked(View v){
		Intent i = new Intent(this, BookListActivity.class);
		startActivity(i);
	}
}
