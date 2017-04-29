package com.example.tushar.menu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class singleblog extends AppCompatActivity {
DatabaseReference dref;
    FirebaseAuth auth;
    private TextView title,description;
    private ImageView image;
    private Button btn;
    private String uid=null;
    public Context ctx;
    public  String postkey=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleblog);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
   ctx=getApplicationContext();
         postkey=getIntent().getExtras().getString("postkey");
        dref= FirebaseDatabase.getInstance().getReference().child("Blog");
        auth=FirebaseAuth.getInstance();
        title=(TextView)findViewById(R.id.textView5);
        description=(TextView)findViewById(R.id.textView6);
        btn=(Button)findViewById(R.id.button4);

        image=(ImageView)findViewById(R.id.imageView2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dref.child(postkey).removeValue();
                startActivity(new Intent(singleblog.this,MainActivity.class));
            }
        });

dref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.hasChild(postkey)){
            String titler=dataSnapshot.child(postkey).child("Title").getValue().toString();
            String desc=dataSnapshot.child(postkey).child("Description").getValue().toString();
            String imager=dataSnapshot.child(postkey).child("Image").getValue().toString();
             uid=dataSnapshot.child(postkey).child("uid").getValue().toString();
            title.setText(titler);
            description.setText(desc);
            Picasso.with(ctx).load(imager).into(image);
            if(auth.getCurrentUser().getUid().equals(uid)){
                btn.setVisibility(View.VISIBLE);


            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
    }
}
