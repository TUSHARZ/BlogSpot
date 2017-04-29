package com.example.tushar.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText name,email,password;
    private Button Register;
    private FirebaseAuth auth;
    private DatabaseReference mdata;
    private ProgressDialog progress;
    private CheckBox cb;
    Boolean cbc=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        cb=(CheckBox)findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               cbc=b;
            }
        });
        Register=(Button)findViewById(R.id.Register);
        auth=FirebaseAuth.getInstance();
        mdata= FirebaseDatabase.getInstance().getReference().child("Users");
        progress=new ProgressDialog(this);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Register();
            }
        });
    }
    public void Register(){
        String emails=email.getText().toString();
        String passwords=password.getText().toString();
        final String names=name.getText().toString();
        if(!TextUtils.isEmpty(names)&&!TextUtils.isEmpty(emails)&&!TextUtils.isEmpty(passwords)&&cbc==true){
            progress.setMessage("SIGNING UP..");
            progress.show();
       auth.createUserWithEmailAndPassword(emails,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   String uid=auth.getCurrentUser().getUid();
                   DatabaseReference data=mdata.child(uid);
                   data.child("name").setValue(names);
                   data.child("Image").setValue("image");
                   progress.dismiss();
                   Intent mainintent=new Intent(RegisterActivity.this,SetupPage.class);
                   startActivity(mainintent);


               }

           }
       }) ;

    }}
}
