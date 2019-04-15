package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

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
    ProgressBar progressBar;
    RadioButton anonymousBtn;
    String uid;

    StorageReference postReferences, videoRef;
    DatabaseReference usersRef, postsRef;
    FirebaseAuth mAuth;

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
        uploadButton = (Button)findViewById(R.id.uploadButton);
        downloadButton = (Button)findViewById(R.id.downloadButton);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        uid = mAuth.getUid();
        videoRef = FirebaseStorage.getInstance().getReference().child("Post Video/").child(uid);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(view);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(view);
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(view);
            }
        });
    }

    public void upload(View view){
        if(videoUri != null){
            UploadTask uploadTask = videoRef.putFile(videoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostVideoActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
            final File localFile = File.createTempFile("userVideo", "mp4");

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
