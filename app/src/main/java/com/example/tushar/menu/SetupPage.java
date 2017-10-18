package com.example.tushar.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.example.tushar.menu.R.id.imageView;

public class SetupPage extends AppCompatActivity {
    private Button finish;
    private ImageButton profile;
    private EditText names;
    private FirebaseAuth auths;
    int galleryRequest=1;



    Uri galleryuri=null;
    private DatabaseReference datas;
    private StorageReference sref;
    private ProgressDialog pd;
    public String downloaduri=null;

    private static int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);
        finish=(Button)findViewById(R.id.button3);
        names=(EditText)findViewById(R.id.editText);
        auths=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setMessage("SUBMITTING...");
        pd.setCanceledOnTouchOutside(false);
        profile=(ImageButton)findViewById(R.id.imageButton3);
        datas= FirebaseDatabase.getInstance().getReference().child("Users");
        sref= FirebaseStorage.getInstance().getReference().child("Profiles");
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence cameraOptions[] = new CharSequence[] {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(SetupPage.this);
                builder.setTitle("Choose an option");
                builder.setItems(cameraOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        else if (which == 1){
                            Intent gallery=new Intent();
                            gallery.setAction(Intent.ACTION_GET_CONTENT);
                            gallery.setType("image/*");
                            gallery.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(gallery,galleryRequest);
                        }
                    }
                });
                builder.show();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String profilee=auths.getCurrentUser().getUid();
               final String name=names.getText().toString();
                if(!(TextUtils.isEmpty(name)&&galleryuri==null))

                pd.show();

                StorageReference filepath=sref.child(galleryuri.getLastPathSegment());
               filepath.putFile(galleryuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         downloaduri=taskSnapshot.getDownloadUrl().toString();

                        DatabaseReference dref=datas.child(profilee);
                        dref.child("name").setValue(name);
                       dref.child("Image").setValue(downloaduri);


                        pd.dismiss();

                        startActivity(new Intent(SetupPage.this,MainActivity.class));

                    }
                });

            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SetupPage.this,SetupPage.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri camUri = data.getData();
            CropImage.activity(camUri)
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setFixAspectRatio(true).setAutoZoomEnabled(false)
                    .start(this);
        }

        if(requestCode==galleryRequest&&resultCode==RESULT_OK){
            galleryuri=data.getData();
            CropImage.activity(galleryuri)
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).setFixAspectRatio(true).setAutoZoomEnabled(false)
                    .start(this);}
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profile.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


        }
    }

