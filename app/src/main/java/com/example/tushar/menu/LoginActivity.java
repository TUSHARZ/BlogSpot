package com.example.tushar.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
   private EditText email,password;
    private Button login,newaccount;
    private FirebaseAuth auth;
    private ConnectivityManager cm;
    private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference data;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.editText6);
        password=(EditText)findViewById(R.id.editText7);
        login=(Button)findViewById(R.id.button2);
        cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE) ;
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if(!(ni!=null&&ni.isConnected()==true)){
            new AlertDialog.Builder(this).setTitle("No Internet Available").setMessage("Please Connect to Internet").setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                }
            }).create().show();


        }
        pd=new ProgressDialog(this);
        pd.setMessage("Logging in ....");
        auth=FirebaseAuth.getInstance();

        data= FirebaseDatabase.getInstance().getReference().child("Users");
        data.keepSynced(true);
        newaccount=(Button)findViewById(R.id.button3);
        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerintent=new Intent(LoginActivity.this,RegisterActivity.class);
                registerintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerintent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
                        }






        });

    }

    private void signin() {
        String emails=email.getText().toString();
        String passwords=password.getText().toString();
        if(!TextUtils.isEmpty(emails)&&!TextUtils.isEmpty(passwords)){
            pd.show();
        auth.signInWithEmailAndPassword(emails,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    checkuser();



                }else
                    Toast.makeText(LoginActivity.this,"Please Create a New Account",Toast.LENGTH_LONG).show();
                    pd.dismiss();

            }
        });
    }

}
public void checkuser(){
    final String uid=auth.getCurrentUser().getUid();
    data.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChild(uid)){
                Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(mainIntent);
            }else{
                Intent setupIntent=new Intent(LoginActivity.this,SetupPage.class);
                startActivity(setupIntent);}

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


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
