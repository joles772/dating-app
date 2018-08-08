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

/**
 * Allows the user to get a new email sent to his email address.
 */
public class ForgotPassword extends AppCompatActivity {

    /**
     * The database.
     */
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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
     * Sends a new password to the chosen email address and sends the user to the login activity.
     * </br>
     * If a firebase error occurs, there will be a toast and the email progress will be stopped and
     * the user will remain on the activity
     * @param view the current view
     */
    public void resetPassword(View view) {

        String email = ((EditText) findViewById(R.id.reset_password_email)).getText().toString();

        myFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // password reset email sent
                Toast.makeText(getApplicationContext(), "A new password has been sent to " + ((EditText) findViewById(R.id.reset_password_email)).getText().toString(),
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
