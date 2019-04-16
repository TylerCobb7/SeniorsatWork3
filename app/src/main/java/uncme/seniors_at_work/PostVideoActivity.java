package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostVideoActivity extends AppCompatActivity {

    VideoView view;
    Button updateVideoButton, recordButton, uploadButton, downloadButton;
    EditText videoDescription;
    Uri videoUri;
    String description;
    String saveCurrentDate;
    String saveCurrentTime;
    String postRandomName;
    String current_USER_ID;
    String downloadURL;
    String postImage;
    String databaseToStoragePull;
    ProgressBar progressBar;
    RadioButton anonymousBtn;
    String uid;
    StorageReference postReferences, videoRef;
    DatabaseReference usersRef, postsRef;
    FirebaseAuth mAuth;
    File localFile;

    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        view = (VideoView)findViewById(R.id.userVideo);
        updateVideoButton = (Button)findViewById(R.id.updateVideoButton);
        videoDescription = (EditText)findViewById(R.id.postVideoDescription);
        progressBar = (ProgressBar)findViewById(R.id.progressBar3);
        anonymousBtn = (RadioButton)findViewById(R.id.anonymous_button);
        recordButton = (Button)findViewById(R.id.recordButton);

        postImage = "https://firebasestorage.googleapis.com/v0/b/seniors-at-work.appspot.com/o/Post%20Media%2Fplaybutton.png?alt=media&token=e1ee3158-5b93-405c-b8e1-635e28663af1";

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());
        postRandomName = saveCurrentDate + saveCurrentTime;

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        uid = mAuth.getUid();
        databaseToStoragePull = uid + postRandomName;
        videoRef = FirebaseStorage.getInstance().getReference().child("Post Video/").child(databaseToStoragePull);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(view);
            }
        });


        updateVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });

    }

    public void upload(View view){
        if(videoUri != null){
            final UploadTask uploadTask = videoRef.putFile(videoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostVideoActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL = uri.toString();
                            if(anonymousBtn.isChecked()) {
                                SavingPostInformationToDatabaseAnon(downloadURL);
                            }else{
                                SavingPostInformationToDatabase(downloadURL);
                            }
                        }
                    });
                    Toast.makeText(PostVideoActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    updateProgress(taskSnapshot);
                }
            });
        }else{
            Toast.makeText(this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void ValidatePostInfo() {
        description = videoDescription.getText().toString();

        if(videoUri == null){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Post description is empty...", Toast.LENGTH_SHORT).show();
        }
        else{
            upload(view);
        }
    }

    private void SavingPostInformationToDatabaseAnon(final String downloadURL) {
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userName = "Anonymous";
                    String userProfileImage = "https://firebasestorage.googleapis.com/v0/b/seniors-at-work.appspot.com/o/profile%20Images%2Fprofile.png?alt=media&token=f367aa11-c7b5-40eb-a663-fae9074faecf";

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", uid);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", postImage);
                    postsMap.put("videoImage", downloadURL);
                    postsMap.put("storageKey", databaseToStoragePull);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("username", userName);

                    postsRef.child(uid + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(PostVideoActivity.this, "New post has been posted..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PostVideoActivity.this, "Post update failed..", Toast.LENGTH_SHORT).show();
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

    private void SavingPostInformationToDatabase(final String downloadURL) {
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   String userName = dataSnapshot.child("username").getValue().toString();
                   String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                   HashMap postsMap = new HashMap();
                   postsMap.put("uid", uid);
                   postsMap.put("date", saveCurrentDate);
                   postsMap.put("time", saveCurrentTime);
                   postsMap.put("description", description);
                   postsMap.put("postImage", postImage);
                   postsMap.put("storageKey", databaseToStoragePull);
                   postsMap.put("postVideo", downloadURL);
                   postsMap.put("profileImage", userProfileImage);
                   postsMap.put("username", userName);

                   postsRef.child(uid + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           if(task.isSuccessful()){
                               SendUserToMainActivity();
                               Toast.makeText(PostVideoActivity.this, "New post has been posted..", Toast.LENGTH_SHORT).show();
                           }
                           else
                           {
                               Toast.makeText(PostVideoActivity.this, "Post update failed..", Toast.LENGTH_SHORT).show();
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

    private void SendUserToMainActivity() {
        Intent intent = new Intent(PostVideoActivity.this, Home.class);
        startActivity(intent);
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot){
        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar3);
        progressBar.setProgress((int) progress);
    }

    public void record(View view){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void download(View view){
        try{
            localFile = File.createTempFile("userVideo", "mp4");

            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostVideoActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();

                    final VideoView videoView = (VideoView)findViewById(R.id.userVideo);
                    videoView.setVideoURI(Uri.fromFile(localFile));
                    videoView.start();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostVideoActivity.this, "Download failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Failed to create temp file: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        videoUri = data.getData();
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Video saved to:\n" + videoUri, Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
