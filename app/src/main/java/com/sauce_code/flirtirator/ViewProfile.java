package com.sauce_code.flirtirator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;

public class ViewProfile extends AppCompatActivity {

    private Firebase myBase;
    String myUserId = "";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);

        Bundle b = getIntent().getExtras();

        myUserId = b.getString("id") + "";

        if (!b.getBoolean("edit")) {
            Button btnEdit = (Button) findViewById(R.id.action_edit_profile);
//            btnEdit.setEnabled(false);
//            btnEdit.setVisibility(View.INVISIBLE);
        }
        else {
            Button btnChat = (Button) findViewById(R.id.btnChat);
            btnChat.setEnabled(false);
            btnChat.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        switch (item.getItemId()) {

            case R.id.action_edit_profile:
                intent = new Intent(ViewProfile.this, EditProfile.class);
                intent.putExtra("id", myUserId + "");
                startActivity(intent);
                break;

            default:
                super.onOptionsItemSelected(item);
                break;

        }

        return true;
    }

    protected void onStart(){
        super.onStart();

        myBase = new Firebase(getResources().getString(R.string.firebase_url) + myUserId);

        final TextView txtName = (TextView) findViewById(R.id.txtMessage);
        final TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        final TextView txtSexAge = (TextView) findViewById(R.id.txtSexAge);
        final ImageView imgPic = (ImageView) findViewById(R.id.imgPic);

        myBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("first").getValue() + " " + snapshot.child("last").getValue();
                txtName.setText(name);

                String description = snapshot.child("description").getValue() + "";
                txtDescription.setText(description);

                String sex = snapshot.child("sex").getValue() + ", " + snapshot.child("age").getValue() + " years old";
                txtSexAge.setText(sex);

                String image = snapshot.child("image").getValue() + "";
                byte[] converted = decodeImage(image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(converted, 0, converted.length);
                imgPic.setImageBitmap(bitmap);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String encodeImage(byte[] imageByteArray) {
        return new String(com.firebase.client.utilities.Base64.encodeBytes(imageByteArray));
    }

    public static byte[] decodeImage(String imageDataString) {
        byte[] temp = new byte[0];
        try {
            temp = com.firebase.client.utilities.Base64.decode(imageDataString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void onClickStartChat(View view) {
        Intent intent = new Intent(ViewProfile.this, Chat.class);
        intent.putExtra("id", myUserId + "");
        startActivity(intent);
    }

}
