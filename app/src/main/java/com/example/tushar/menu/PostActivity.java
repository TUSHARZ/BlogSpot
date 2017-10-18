package com.example.tushar.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostActivity extends ActionBarActivity {
	private final static int PERMISSION_REQUEST_CODE = 777;
private ImageButton image;
    private Uri galleryuri=null;

    private EditText title,description;
    private Button button;

  //  SetupPage setup;


    private DatabaseReference data,users;
    private ProgressDialog progresss;
    private StorageReference storage;
    private static final int GALLERY_REQUEST=1;
    private String name,desc;
    public FirebaseAuth auth;
    private FirebaseUser user;
    public FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!checkPermission()) requestPermissions();

        setContentView(R.layout.activity_post);
        image=(ImageButton)findViewById(R.id.imageButton);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(auth.getCurrentUser()==null){
            Toast.makeText(this,"Logged out",Toast.LENGTH_LONG).show();
        }
      //  users=FirebaseDatabase.getInstance().getReference().child("Users");


        title=(EditText)findViewById(R.id.editText3);

       users=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
  getSupportActionBar().setDisplayShowHomeEnabled(true);
        description=(EditText)findViewById(R.id.editText2) ;
        storage=FirebaseStorage.getInstance().getReference();
        data= FirebaseDatabase.getInstance().getReference().child("Blog");
        button=(Button)findViewById(R.id.button) ;
        progresss=new ProgressDialog(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,GALLERY_REQUEST);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
       listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if((firebaseAuth.getCurrentUser()==null))
               {

                    Intent loginintent= new Intent(PostActivity.this,LoginActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
               }



    }
        };
        data.keepSynced(true);
        users.keepSynced(true);

       auth.addAuthStateListener(listener);
    }
    private void startPosting(){
        progresss.setMessage("Posting...");

         name=title.getText().toString();
         desc=description.getText().toString();
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(desc)&&galleryuri!=null){
            progresss.show();
            StorageReference sref=storage.child("Blog_image").child(galleryuri.getLastPathSegment());
            sref.putFile(galleryuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadurl=taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost=data.push();
                    users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("Title").setValue(name);
                            newPost.child("Description").setValue(desc);
                            newPost.child("Image").setValue(downloadurl.toString());
                            newPost.child("uid").setValue(user.getUid());
                            newPost.child("name").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Post Successful",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PostActivity.this,MainActivity.class));

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });


                   //  newPost.child("Profile").setValue(setup.getDownloaduri());
                    progresss.dismiss();
                }
            });

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
             galleryuri=data.getData();
        CropImage.activity(galleryuri)
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(16,9).setFixAspectRatio(true).setAutoZoomEnabled(false).setMaxCropResultSize(1280,720).setRequestedSize(1280,720)
                .start(this);}

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

	/**
	 * Checks the current state of the permissions needed.
	 *
	 * @return true if granted  and false if not.
	 */
	private boolean checkPermission() {
		int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		return permissionState == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Requests the necessary permissions.
	 */
	private void requestPermissions() {
		String[] permissions = new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		};
		ActivityCompat.requestPermissions(this,
				permissions,
				PERMISSION_REQUEST_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0) {
				// True if read external storage permission was granted, false if not.
				boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				// True if write external storage permission was granted, false if not.
				boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
			} else {
				//Permission denied
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
