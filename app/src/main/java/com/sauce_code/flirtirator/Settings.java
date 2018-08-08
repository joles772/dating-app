package com.sauce_code.flirtirator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Settings extends AppCompatActivity {

    private Firebase myBase;
    String myUserId = "";
    String myOs = "";
    String mySeeking = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);
        Bundle b = getIntent().getExtras();

        myUserId = b.getString("id") + "";

        Spinner spnSeeking = (Spinner)findViewById(R.id.spnSeeking);
        String[] sexes = new String[]{"Male", "Female"};
        ArrayAdapter<String> seekingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        spnSeeking.setAdapter(seekingAdapter);

        Spinner spnOS = (Spinner)findViewById(R.id.spnOS);
        String[] oses = new String[]{"Windows", "Mac", "Linux"};

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, oses);
        spnOS.setAdapter(ageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart(){
        super.onStart();

        myBase = new Firebase("https://fiery-heat-7972.firebaseio.com/" + myUserId);

        final Spinner spnOs = (Spinner) findViewById(R.id.spnOS);
        final Spinner spnSeeking = (Spinner) findViewById(R.id.spnSeeking);

        myBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myOs = snapshot.child("os").getValue() + "";

                if ((snapshot.child("seeking").getValue() + "").equals("Female"))
                    spnSeeking.setSelection(1);

                if ((snapshot.child("os").getValue() + "").equals("Mac"))
                    spnOs.setSelection(1);

                else if ((snapshot.child("os").getValue() + "").equals("Linux"))
                    spnOs.setSelection(2);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onClickSave(View view){
        final Spinner spnOs = (Spinner)findViewById(R.id.spnOS);
        final Spinner spnSeeking = (Spinner)findViewById(R.id.spnSeeking);

        myBase.child("os").setValue(spnOs.getSelectedItem().toString() + "");
        myBase.child("seeking").setValue(spnSeeking.getSelectedItem().toString() + "");


        finish();
    }

}
