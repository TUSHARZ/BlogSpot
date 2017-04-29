package com.example.tushar.menu;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;




public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageButton image;
    private RecyclerView view;
  DatabaseReference mdata,data;
   public FirebaseAuth auth;
    private Boolean like=false;

    ConnectivityManager cm;
    SwipeRefreshLayout refreshLayout;


    public FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
       FirebaseUser cu=auth.getCurrentUser();
        cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        if(cu!=null){
        if(!(info!=null&&info.isConnected()==true)){
            Toast.makeText(this,"No Internet Available , Swipe down to Refresh",Toast.LENGTH_LONG).show();
        }else {Toast.makeText(this,"Loading Data..\nPlease Wait",Toast.LENGTH_LONG).show();}}

     refreshLayout=(SwipeRefreshLayout)findViewById(R.id.re);
      refreshLayout.setOnRefreshListener(this);

        mdata=FirebaseDatabase.getInstance().getReference().child("Blog");
       view=(RecyclerView)findViewById(R.id.recycler_view);

        data=FirebaseDatabase.getInstance().getReference().child("Users");
        data.keepSynced(true);


        mdata.keepSynced(true);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if((firebaseAuth.getCurrentUser()==null))
                {

                    Intent loginintent= new Intent(MainActivity.this,LoginActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
            }
        };



        view.setLayoutManager(new LinearLayoutManager(this));
        checkuser();

    }

    @Override
    protected void onStart() {
        super.onStart();



        auth.addAuthStateListener(authStateListener);


        FirebaseRecyclerAdapter<Blog,blogviewholder> adapter= new FirebaseRecyclerAdapter<Blog, blogviewholder>(
                Blog.class,
                R.layout.bloglist,
                blogviewholder.class,
                mdata


        ) {
            @Override
            protected void populateViewHolder(blogviewholder viewHolder, Blog model, int position) {
                final DatabaseReference likey=FirebaseDatabase.getInstance().getReference();
                likey.keepSynced(true);
              final String postkey=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setlike(postkey);
                viewHolder.setname(model.getName());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(MainActivity.this,singleblog.class);
                        i.putExtra("postkey",postkey);
                        startActivity(i);
                    }
                });
                viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like=true;
                           likey.child("Likes").addValueEventListener(new ValueEventListener() {


                                @Override

                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(like){
                                    if(dataSnapshot.hasChild(postkey)){
                                         likey.child("Likes").child(postkey).child(auth.getCurrentUser().getUid()).removeValue();
                                        like=false;


                                    }else {

                                        likey.child("Likes").child(postkey).child(auth.getCurrentUser().getUid()).setValue("Random");
                                        like=false;


                                    }
                                }}

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                }
                            });



                        }

                });
            }
        };
        view.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m=getMenuInflater();
        m.inflate(R.menu.newmenu,menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        startActivity(new Intent(MainActivity.this,MainActivity.class));
    }

    public static class blogviewholder extends RecyclerView.ViewHolder{
  View mview;
        ImageButton btn;
        FirebaseAuth auth;
        DatabaseReference datal;

        public blogviewholder(View itemView) {
            super(itemView);
            mview=itemView;
            btn=(ImageButton)mview.findViewById(R.id.like_btn);
            auth=FirebaseAuth.getInstance();
            datal=FirebaseDatabase.getInstance().getReference().child("Likes");

        }

        public void setTitle(String title){
            TextView titlee=(TextView)mview.findViewById(R.id.textView);
            titlee.setText(title);
        }
        public void setDescription(String Description){
            TextView descc=(TextView)mview.findViewById(R.id.textView2);
            descc.setText(Description);
        }
        public void setImage(final Context ctx, final String imagee){
            final ImageView image=(ImageView) mview.findViewById(R.id.imageView);
            Picasso.with(ctx).load(imagee).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(imagee).into(image);
                }
            });

        }
        public void setname(String name){
            TextView namee=(TextView)mview.findViewById(R.id.name);
            namee.setText(name);
        }
        public void setlike(final String key){
            datal.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(key)){
                        btn.setImageResource(R.drawable.likeupw);


                    }
                    else {
                        btn.setImageResource(R.drawable.likeup);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.men:


                startActivity(new Intent(MainActivity.this, PostActivity.class));

                break;

            case R.id.logout:
                Logout();
                break;

            default:
        break;}
                return super.onOptionsItemSelected(item);
        }

 public void checkuser(){
     if(!(auth.getCurrentUser()==null)){

     final String uid=auth.getCurrentUser().getUid();
     data.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             if(!dataSnapshot.hasChild(uid)){
                 Intent setupIntent=new Intent(MainActivity.this,SetupPage.class);
                 startActivity(setupIntent);
             }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
 }}

    private void Logout() {
        auth.signOut();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("EXIT!").setMessage("You really want to Exit").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();



    }
}
