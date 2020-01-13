package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText usernameEt, userStatusEt;
    private CircleImageView profileImageView;
    private Button updateButton;
    ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootReference;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String currentUserId;
    private Uri filePath;
    private Uri imageDownLoadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootReference = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        initFields();

        getUserInformation();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageUploadToFirebase();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });


    }

    private void selectImageUploadToFirebase() {
        // define implicit intent to mobile gelary
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        imageIntent,
                        "Image select from here....")
                , PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            // get the Uri of data
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToStorage() {
        if (filePath != null) {
            showProgressDialog("Uploading");

            final StorageReference ref = storageReference.child("profile_image/" + currentUserId );

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, "Image uploaded to firebase Storege.", Toast.LENGTH_SHORT).show();
                            // get the download url of the image
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageDownLoadUrl = uri;
                                    uploadImageUrlToDatabase(imageDownLoadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMsg = e.getMessage().toString();
                            Toast.makeText(UpdateProfileActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void uploadImageUrlToDatabase(Uri imageDownloadUrl) {
        rootReference.child("Users")
                .child(currentUserId)
                .child("profile_image")
                .setValue(imageDownloadUrl.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateProfileActivity.this, "Image url saved to database.", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = task.getException().toString();
                            Toast.makeText(UpdateProfileActivity.this, "Error: "+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserInformation() {

        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")
                            && dataSnapshot.hasChild("status") && dataSnapshot.hasChild("profile_image")) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userImage = dataSnapshot.child("profile_image").getValue().toString();

                        usernameEt.setText(userName);
                        userStatusEt.setText(userStatus);
                        Picasso.get().load(userImage).into(profileImageView);

                    } else if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")) {
                        usernameEt.setText(dataSnapshot.child("name").getValue().toString());
                        userStatusEt.setText(dataSnapshot.child("status").getValue().toString());
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Set profile info first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateProfile() {

        String userName = usernameEt.getText().toString();
        String userStatus = userStatusEt.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please Enter user name.", Toast.LENGTH_SHORT).show();
            usernameEt.requestFocus();
        } else if (TextUtils.isEmpty(userStatus)) {
            Toast.makeText(this, "Please enter yur status.", Toast.LENGTH_SHORT).show();
            userStatusEt.requestFocus();
        } else {
            //calling the uploadImageImage method
            uploadImageToStorage();
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", userName);
            profileMap.put("status", userStatus);

            rootReference.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated succesfully..", Toast.LENGTH_SHORT).show();
                        sentUserToMainActivity();
                    } else {
                        String errorMsg = task.getException().toString();
                        Toast.makeText(UpdateProfileActivity.this, "Error : " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    private void sentUserToMainActivity() {

        Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    private void initFields() {
        usernameEt = findViewById(R.id.username_et_id);
        userStatusEt = findViewById(R.id.userstatus_et_id);
        profileImageView = findViewById(R.id.user_pic_imageview_id);
        updateButton = findViewById(R.id.update_profile_button_id);

        progressDialog = new ProgressDialog(this);
    }

    private void showProgressDialog(String msg) {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage(msg +". . .");
        progressDialog.show();
    }
}
