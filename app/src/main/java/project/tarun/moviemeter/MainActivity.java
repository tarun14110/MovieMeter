package project.tarun.moviemeter;


/**
 * Created by tarun on 5/4/16.
 */
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }


        if(id==R.id.action_about){
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.about_message), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 10);
            toast.show();

        }

        return super.onOptionsItemSelected(item);
    }
}
