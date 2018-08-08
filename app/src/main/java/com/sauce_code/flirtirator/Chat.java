package com.sauce_code.flirtirator;

import android.content.Context;
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

public class Chat extends AppCompatActivity {

    String idKey1 = "";
    String idKey2 = "";
    String chatId = "";

    int myId = 0;

    private Firebase myBase;

    ArrayList<String> messages;

    String img1 = "";
    String img2 = "";

    int numMessages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase.setAndroidContext(this);

        myBase = new Firebase(getResources().getString(R.string.firebase_url));

        Bundle b = getIntent().getExtras();

        idKey1 = myBase.getAuth().getUid().toString() + "/" + b.getString("id");
        idKey2 = b.getString("id") + "/" +  myBase.getAuth().getUid().toString();

    }

    protected void onStart(){
        super.onStart();

        myBase = new Firebase(getResources().getString(R.string.firebase_url) + "chats");

        final TextView txtMsg = (TextView) findViewById(R.id.txtMsg);
        final ImageView imgPic = (ImageView) findViewById(R.id.imgPic);
        final ListView lstMessages = (ListView) findViewById(R.id.lstMessages);

        final Bundle b = getIntent().getExtras();


        myBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String id1 = (String)snapshot.child("idKey1").child("active").getValue();
                String id2 = (String)snapshot.child("idKey2").child("active").getValue();

                if(id2 != null){
                    chatId = id2;
                    myId = 2;
                }
                else{
                    chatId = id1;
                    myId = 1;
                }
                numMessages = (int)snapshot.getChildrenCount();
                img1 = snapshot.child(myBase.getAuth().getUid().toString()).child("image").getValue().toString();
                img2 = snapshot.child( b.getString("id")).child("image").getValue().toString();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Firebase myBase2 = new Firebase(getResources().getString(R.string.firebase_url) + chatId);

        myBase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    messages.add(snapshot.getValue().toString());
                }

                MessageAdapter futureAdapter = new MessageAdapter(Chat.this, R.layout.match, messages);
                lstMessages.setAdapter(futureAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    public class MessageAdapter extends ArrayAdapter<String> {

        ArrayList<String> messages;
        Context con;
        int res;

        public MessageAdapter(Context context, int resource, ArrayList<String> messages) {
            super(context, resource, messages);
            this.messages = messages;
            this.con = context;
            this.res = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String msg = messages.get(position);

            if (convertView == null){
                LayoutInflater inflator=LayoutInflater.from(con);
                convertView=inflator.inflate(res,parent,false);
            }

            if(!(myId + "").equals(msg.charAt(0) + "")){
                ImageView imgPic = (ImageView)convertView.findViewById(R.id.imgPic);

                byte[] converted = decodeImage(img2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(converted, 0, converted.length);
                imgPic.setImageBitmap(bitmap);

                TextView txtMsg = (TextView) findViewById(R.id.txtMsg);
                txtMsg.setText(msg.substring(2));
            }

            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public void sendMessageOnClick(View v){
        TextView txtMyMsg = (TextView) findViewById(R.id.txtMyMsg);
        Firebase myBase2 = new Firebase(getResources().getString(R.string.firebase_url) + chatId);

        myBase2.child(numMessages + "").setValue(myId + ":" + txtMyMsg.getText().toString());
    }
}
