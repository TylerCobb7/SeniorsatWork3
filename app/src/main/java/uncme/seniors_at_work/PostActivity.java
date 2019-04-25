package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.grpc.Context;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    ImageButton selectPostImage;
    Button updatePostButton;
    EditText postDescription;
    Uri imageUri;
    String description;
    String saveCurrentDate;
    String saveCurrentTime;
    String postRandomName;
    String current_USER_ID;
    String downloadURL;
    ProgressBar progressBar;
    RadioButton anonymousBtn;

    public static final int Gallery_Pick = 1;
    StorageReference postReferences;
    DatabaseReference usersRef, postsRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        selectPostImage = (ImageButton) findViewById(R.id.postImageButton);
        updatePostButton = (Button) findViewById(R.id.postButton);
        postDescription = (EditText) findViewById(R.id.postImageDescription);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        current_USER_ID = mAuth.getCurrentUser().getUid();postReferences = FirebaseStorage.getInstance().getReference();

        anonymousBtn = (RadioButton)findViewById(R.id.anonymous_button);



        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        updatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });

    }

    //Validate post information.
    //Checks if image exist, description is empty, and if posting anonymously
    private void ValidatePostInfo() {
        description = postDescription.getText().toString();

        if(imageUri == null){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Post description is empty...", Toast.LENGTH_SHORT).show();
        }
        if(anonymousBtn.isChecked()){
            progressBar.setVisibility(View.VISIBLE);
            StoringImageToFirebaseStorageAnon();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            StoringImageToFirebaseStorage();
        }
    }

    //Sends post data to firebase
    private void StoringImageToFirebaseStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());
        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = postReferences.child("Post Media").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        progressBar.setVisibility(View.VISIBLE);
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL = uri.toString();

                            SavingPostInformationToDatabase(downloadURL);
                        }
                    });

                    Toast.makeText(PostActivity.this, "Post has been posted successfully...", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    //Sends post to firebase as anonymous
    private void StoringImageToFirebaseStorageAnon() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());
        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = postReferences.child("Post Media").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        progressBar.setVisibility(View.VISIBLE);
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL = uri.toString();

                            SavingPostInformationToDatabaseAnon(downloadURL);
                        }
                    });

                    Toast.makeText(PostActivity.this, "Post has been posted successfully...", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    //Saving post as the user
    private void SavingPostInformationToDatabase(final String url) {
        usersRef.child(current_USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_USER_ID);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", url);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("username", userName);

                    postsRef.child(current_USER_ID + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(PostActivity.this, "New post has been posted..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PostActivity.this, "Post update failed..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Saving post info as anonymous
    private void SavingPostInformationToDatabaseAnon(final String url) {
        usersRef.child(current_USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userName = "Anonymous";
                    String userProfileImage = "https://firebasestorage.googleapis.com/v0/b/seniors-at-work.appspot.com/o/profile%20Images%2Fprofile.png?alt=media&token=f367aa11-c7b5-40eb-a663-fae9074faecf";

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_USER_ID);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", url);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("username", userName);

                    postsRef.child(current_USER_ID + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(PostActivity.this, "New post has been posted..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PostActivity.this, "Post update failed..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Send user to HomeActivity
    private void SendUserToMainActivity() {
        Intent intent = new Intent(PostActivity.this, Home.class);
        startActivity(intent);
    }

    //Opens gallery from inside the phone
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    //Checks for all criteria to allow app to upload image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }
    }
}
