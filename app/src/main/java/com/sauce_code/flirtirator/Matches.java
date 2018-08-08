package com.sauce_code.flirtirator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class Matches extends AppCompatActivity {

    String myUserId = "bigsteve";
    private Firebase myBase;
    private String mySeeking = "";
    private String myOs = "";
    private String mySex = "";
    ArrayList<User> matchList;

    /**
     * The database.
     */
    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matches, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        switch (item.getItemId()) {

            case R.id.action_settings:
                intent = new Intent(Matches.this, Settings.class);
                intent.putExtra("id", myUserId + "");
                startActivity(intent);
                break;

            case R.id.action_view_profile:
                intent = new Intent(Matches.this, ViewProfile.class);
                intent.putExtra("id", myUserId + "");
                intent.putExtra("edit", true);
                startActivity(intent);
                break;

            case R.id.action_log_out:
                myFirebaseRef.unauth();
                intent = new Intent(Matches.this, Login.class);
                startActivity(intent);
                finish();
                break;

            default:
                super.onOptionsItemSelected(item);
                break;

        }

        return true;
    }

    protected void onStart(){
        super.onStart();

        matchList = new ArrayList<User>();

        myFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        Firebase myUserBase = new Firebase(getResources().getString(R.string.firebase_url) + myUserId);

        myUserBase.addValueEventListener(new ValueEventListener() {
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

                mySeeking = snapshot.child("seeking").getValue() + "";
                myOs = snapshot.child("os").getValue() + "";
                mySex = snapshot.child("sex").getValue() + "";
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User u;
                //System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey() + "";
                    System.out.println(postSnapshot.child("sex").getValue() + "");
                    System.out.println(mySeeking + "");
                    //System.out.println(mySeeking + "-");
                    if ((postSnapshot.child("sex").getValue() + "").equals(mySeeking + "") && (postSnapshot.child("seeking").getValue() + "").equals(mySex + "") && (postSnapshot.child("os").getValue() + "").equals(myOs + "") && (postSnapshot.getKey() + "").equals(myUserId + "") == false) { //add key!=userid
                        u = new User();
                        u.id = key + "";
                        u.name = postSnapshot.child("first").getValue() + " " + postSnapshot.child("last").getValue() + "";
                        u.age = postSnapshot.child("age").getValue() + "";
                        u.image = postSnapshot.child("image").getValue() + "";
                        matchList.add(u);
                        System.out.println(key + "- YES");
                    }
                    //System.out.println(key + "");
                    //System.out.println(snapshot.child("bigsteve").child("sex").getValue() + "");
                    //System.out.println(mySeeking + "");
                    //System.out.println("+");
                    //System.out.println("titties");
                }
                System.out.println(matchList.size());
                ListView lstMatches = (ListView) findViewById(R.id.lstMatches);
                UserAdapter futureAdapter = new UserAdapter(Matches.this, R.layout.match, matchList);
                lstMatches.setAdapter(futureAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //An adapter class to display my custom layout described in meetings_info.xml
    public class UserAdapter extends ArrayAdapter<User> {

        ArrayList<User> matches;
        Context con;
        int res;

        public UserAdapter(Context context, int resource, ArrayList<User> matches) {
            super(context, resource, matches);
            this.matches = matches;
            this.con = context;
            this.res = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            User u = matches.get(position);

            if (convertView == null){
                LayoutInflater inflator=LayoutInflater.from(con);
                convertView=inflator.inflate(res,parent,false);
            }

            TextView txtName = (TextView)convertView.findViewById(R.id.txtMessage);
            txtName.setText("  " + u.name + "");

            TextView txtAge = (TextView)convertView.findViewById(R.id.txtAge);
            txtAge.setText("  " + u.age + " years old");

            ImageView imgPic = (ImageView)convertView.findViewById(R.id.imgPic);

            byte[] converted = decodeImage(u.image);
            Bitmap bitmap = BitmapFactory.decodeByteArray(converted, 0, converted.length);
            imgPic.setImageBitmap(bitmap);


            Button b1 = (Button)convertView.findViewById(R.id.button4);
            b1.setTag(u.id + ""); //The tag is used as an ID parameter to edit the right meeting

            return convertView;
        }

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

    public void onClickViewProfile(View view){
        String id = view.getTag().toString();
        Intent intent = new Intent(Matches.this, ViewProfile.class);
        intent.putExtra("id", id + "");
        intent.putExtra("edit", false);
        startActivity(intent);
    }

}
