package com.example.lendapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    private EditText fullName, username, email, phoneNumber;
    private TextView userFullname, DeleteAccount;
    private ImageView profilePicture, coverPicture;
    private ImageButton saveProfileBtn, editProfilePictureBtn, editCoverPictureBtn;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String currentImageType;
    private String userId;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);  // Initialize Firebase
        setContentView(R.layout.edit_profile);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid(); // Get the current user's ID
        Log.d("EditProfile", "Current user ID: " + userId);


        // Initialize views
        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        userFullname = findViewById(R.id.userFullname);
        profilePicture = findViewById(R.id.profile_picture);
        coverPicture = findViewById(R.id.cover_picture);
        saveProfileBtn = findViewById(R.id.saveProfile_btn);
        editProfilePictureBtn = findViewById(R.id.editProfilePicture_btn);
        editCoverPictureBtn = findViewById(R.id.editCoverPicture_btn);
        DeleteAccount = findViewById(R.id.deleteAccount);

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("users").child(userId);

        // Load existing profile data
        loadProfileData();

        // Set a TextWatcher on fullName EditText to update userFullname TextView live
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userFullname.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        editProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser("profile");
            }
        });

        editCoverPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser("cover");
            }
        });

        // Set a click listener for the delete account button
        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
    }

    private void openFileChooser(String imageType) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        currentImageType = imageType;
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (currentImageType.equals("profile")) {
                                databaseReference.child("profilePicture").setValue(uri.toString());
                                saveImageLocally(uri, "profile");
                                Glide.with(EditProfile.this).load(uri).into(profilePicture);
                            } else if (currentImageType.equals("cover")) {
                                databaseReference.child("coverPictureUrl").setValue(uri.toString());
                                saveImageLocally(uri, "cover");
                                Glide.with(EditProfile.this).load(uri).into(coverPicture);
                            }
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UploadError", e.getMessage(), e);
            });
        }
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    private void saveImageLocally(Uri uri, String imageType) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            File directory = getDir("user_images", Context.MODE_PRIVATE);
            File imageFile = new File(directory, imageType + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d("SaveImageLocally", "Image saved locally: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("SaveImageLocally", "Error saving image locally", e);
        }
    }

    private void saveProfile() {
        String fullNameText = fullName.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String phoneNumberText = phoneNumber.getText().toString().trim();

        if (fullNameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || phoneNumberText.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!usernameText.matches("^[a-zA-Z0-9_]{3,15}$")) {
            Toast.makeText(this, "Username must be 3-15 characters and can only contain letters, numbers, and underscores", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("username")
                .equalTo(usernameText)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && !dataSnapshot.child(userId).exists()) {
                            Toast.makeText(EditProfile.this, "Username already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            // Proceed to save profile
                            UserProfile userProfile = new UserProfile(fullNameText, usernameText, emailText, phoneNumberText);
                            databaseReference.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("SaveProfile", "Profile saved successfully");
                                        Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MyAccount.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("SaveProfileError", "Error saving profile", task.getException());
                                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("DatabaseError", databaseError.getMessage());
                        Toast.makeText(EditProfile.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void loadProfileData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        fullName.setText(userProfile.fullName);
                        username.setText(userProfile.username);
                        email.setText(userProfile.email);
                        phoneNumber.setText(userProfile.phoneNumber);
                        userFullname.setText(userProfile.fullName); // Set the TextView with the full name

                        String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);
                        String coverPictureUrl = dataSnapshot.child("coverPictureUrl").getValue(String.class);

                        if (profilePictureUrl != null) {
                            Glide.with(EditProfile.this).load(profilePictureUrl).into(profilePicture);
                        }

                        if (coverPictureUrl != null) {
                            Glide.with(EditProfile.this).load(coverPictureUrl).into(coverPicture);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                Log.e("LoadProfileError", databaseError.getMessage(), databaseError.toException());
            }
        });
    }


    private void deleteAccount() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting Account...");
        progressDialog.show();

        // Step 1: Delete user data from Realtime Database
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Step 2: Delete user profile picture from Firebase Storage
                    StorageReference profilePicRef = storageReference.child("profile.jpg");
                    profilePicRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() || task.getException() instanceof StorageException) {
                                // Step 3: Delete user cover picture from Firebase Storage
                                StorageReference coverPicRef = storageReference.child("cover.jpg");
                                coverPicRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful() || task.getException() instanceof StorageException) {
                                            // Step 4: Delete user authentication
                                            FirebaseUser user = auth.getCurrentUser();
                                            if (user != null) {
                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EditProfile.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(EditProfile.this, Login.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(EditProfile.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                                            Log.e("DeleteAccountError", task.getException().getMessage(), task.getException());
                                                        }
                                                    }
                                                });
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditProfile.this, "Failed to delete cover picture", Toast.LENGTH_SHORT).show();
                                            Log.e("DeleteCoverPicError", task.getException().getMessage(), task.getException());
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(EditProfile.this, "Failed to delete profile picture", Toast.LENGTH_SHORT).show();
                                Log.e("DeleteProfilePicError", task.getException().getMessage(), task.getException());
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                    Log.e("DeleteAccountError", task.getException().getMessage(), task.getException());
                }
            }
        });
    }
}

class UserProfile {
    public String fullName, username, email, phoneNumber, profilePicture, coverPictureUrl;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String fullName, String username, String email, String phoneNumber) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
