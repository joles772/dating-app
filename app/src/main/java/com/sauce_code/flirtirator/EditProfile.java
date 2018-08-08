package com.sauce_code.flirtirator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    Firebase myBase;
    String myUserId = "bigsteve";
    Bitmap b = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spnSex = (Spinner)findViewById(R.id.spnSex);
        String[] sexes = new String[]{"Male", "Female"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexes);
        spnSex.setAdapter(sexAdapter);

        Spinner spnAge = (Spinner)findViewById(R.id.spnAge);
        String[] ages = new String[82];

        for(int i = 0; i < ages.length; i++){
            ages[i] = i + 18 + "";
        }

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ages);
        spnAge.setAdapter(ageAdapter);
    }

    protected void onStart(){
        super.onStart();

        myBase = new Firebase("https://fiery-heat-7972.firebaseio.com/" + myUserId);

        final TextView txtFirst = (TextView) findViewById(R.id.txtFirst);
        final TextView txtLast = (TextView) findViewById(R.id.txtLast);
        final TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        final Spinner spnSex = (Spinner)findViewById(R.id.spnSex);
        final Spinner spnAge = (Spinner)findViewById(R.id.spnAge);
        final ImageView imgPic = (ImageView) findViewById(R.id.imgPic);

        myBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String first = snapshot.child("first").getValue() + "";
                txtFirst.setText(first);

                String last = snapshot.child("last").getValue() + "";
                txtLast.setText(last);

                String description = snapshot.child("description").getValue() + "";
                txtDescription.setText(description);

                if ((snapshot.child("sex").getValue() + "").equals("Female"))
                    spnSex.setSelection(1);

                spnAge.setSelection(Integer.parseInt(snapshot.child("age").getValue() + "") - 18);

                String image = snapshot.child("image").getValue() + "";
                byte[] converted = decodeImage(image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(converted, 0, converted.length);
                if (b == null) imgPic.setImageBitmap(bitmap);
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
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

    public void saveOnClick(View v){
        final TextView txtFirst = (TextView) findViewById(R.id.txtFirst);
        final TextView txtLast = (TextView) findViewById(R.id.txtLast);
        final TextView txtDescription = (TextView) findViewById(R.id.txtDescription);
        final Spinner spnSex = (Spinner)findViewById(R.id.spnSex);
        final Spinner spnAge = (Spinner)findViewById(R.id.spnAge);
        final ImageView imgPic = (ImageView)findViewById(R.id.imgPic);

        myBase.child("first").setValue(txtFirst.getText() + "");
        myBase.child("last").setValue(txtLast.getText() + "");
        myBase.child("description").setValue(txtDescription.getText() + "");
        myBase.child("sex").setValue(spnSex.getSelectedItem().toString() + "");
        myBase.child("age").setValue(spnAge.getSelectedItem().toString() + "");

        Bitmap bitmap = ((BitmapDrawable)imgPic.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        myBase.child("image").setValue(encodeImage(byteArray) + "");

        finish();
    }

    public void backOnClick(View v){
        finish();
    }

    public static String encodeImage(byte[] imageByteArray) {
        return new String(com.firebase.client.utilities.Base64.encodeBytes(imageByteArray));
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        byte[] temp = new byte[0];
        try {
            temp = com.firebase.client.utilities.Base64.decode(imageDataString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void addImageOnClick(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = (ImageView) findViewById(R.id.imgPic);
                imageView.setImageBitmap(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
