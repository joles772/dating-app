package com.sauce_code.flirtirator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * Allows the user to register, choosing an email address and a password.
 */
public class Registration extends AppCompatActivity {

    /**
     * The database.
     */
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    /**
     * Registers an user, using the entered email address and password.<br>
     * If the password check fails or there is an error from Firebase, there will be a toast and the
     * registration will be stopped. The user remains on the acitvity.
     * @param view the current view
     */
    public void register(View view) {

        String email = ((EditText) findViewById(R.id.registration_email)).getText().toString();

        String password1 = ((EditText) findViewById(R.id.registration_password_1)).getText().toString();

        String password2 = ((EditText) findViewById(R.id.registration_password_2)).getText().toString();

        if (!password1.equals(password2)) {
            Toast.makeText(getApplicationContext(), "Passwords don't match!",
                    Toast.LENGTH_SHORT).show();
        } else {
            myFirebaseRef.createUser(email, password1, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Toast.makeText(getApplicationContext(), "Successfully created user account with uid: " + result.get("uid"),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
