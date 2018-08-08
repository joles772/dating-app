package com.sauce_code.flirtirator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    @Override
    public void onStart() {
        super.onStart();
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(myFirebaseRef.getAuth().getUid()).child("seeking").getValue() == null) {
                    Firebase uid = myFirebaseRef.child(myFirebaseRef.getAuth().getUid());
                    uid.child("age").setValue("18");
                    uid.child("description").setValue("description");
                    uid.child("first").setValue("First");
                    uid.child("image").setValue(snapshot.child("defaultImage").getValue());
                    uid.child("last").setValue("Last");
                    uid.child("os").setValue("Windows");
                    uid.child("seeking").setValue("Male");
                    uid.child("sex").setValue("Female");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showStatus(View view) {
        Toast.makeText(getApplicationContext(), "User ID: " + myFirebaseRef.getAuth().getUid() + ", Provider: " + myFirebaseRef.getAuth().getProvider(),
                Toast.LENGTH_SHORT).show();
    }
}
