package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.UserWriteRecord;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class ClickPostActivity extends AppCompatActivity {

    ImageView postImage;
    TextView postDescription;
    Button postEditButton;
    Button postDeleteButton;
    Button banUserButton;
    DatabaseReference clickPostRef;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    File localFile;
    VideoView postVideo;

    String PostKey, currentUserID, databaseUserID, description, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        postImage = (ImageView)findViewById(R.id.click_post_image);
        postVideo = (VideoView)findViewById(R.id.click_post_video);
        postDescription = (TextView)findViewById(R.id.click_post_description);
        postEditButton = (Button)findViewById(R.id.edit_post_button);
        postDeleteButton = (Button)findViewById(R.id.delete_post_button);
        banUserButton = (Button)findViewById(R.id.ban_user_button);

        postDeleteButton.setVisibility(View.INVISIBLE);
        postEditButton.setVisibility(View.INVISIBLE);
        banUserButton.setVisibility(View.INVISIBLE);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();


                    postDescription.setText(description);
                    if(image.equals("https://firebasestorage.googleapis.com/v0/b/seniors-at-work.appspot.com/o/Post%20Media%2Fplaybutton.png?alt=media&token=e1ee3158-5b93-405c-b8e1-635e28663af1")) {
                        postImage.setVisibility(View.GONE);
                        postVideo.setVisibility(View.VISIBLE);
                        download(postVideo);
                    }else{
                        postImage.setVisibility(View.VISIBLE);
                        postVideo.setVisibility(View.GONE);
                        Picasso.get().load(image).into(postImage);
                    }

                    if(currentUserID.equals(databaseUserID) || currentUserID.equals("knZTjI9U4qdF8GGEXxPH7VItSdm2")){
                        postDeleteButton.setVisibility(View.VISIBLE);
                        postEditButton.setVisibility(View.VISIBLE);
                        if(currentUserID.equals("knZTjI9U4qdF8GGEXxPH7VItSdm2")){
                            banUserButton.setVisibility(View.VISIBLE);
                        }
                    }

                    postEditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(description);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });

        banUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BanPostUser();
            }
        });




    }

    public void download(View view){
        try{
            localFile = File.createTempFile("uservideo", "mp4");

            StorageReference videoRef = FirebaseStorage.getInstance().getReference("Post Video/").child(PostKey);
            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ClickPostActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();

                    final VideoView videoView = (VideoView)findViewById(R.id.click_post_video);
                    videoView.setVideoURI(Uri.fromFile(localFile));
                    videoView.start();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ClickPostActivity.this, "Download failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Failed to create temp file: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void BanPostUser() {
        String user = PostKey;
        String substring = user.substring(0, 28);
        HashMap bannedMap = new HashMap();
        bannedMap.put("banned", "true");
        userRef.child(substring).updateChildren(bannedMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    finish();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(ClickPostActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void EditCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post: ");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post updated..", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
    }

    private void DeleteCurrentPost() {
        clickPostRef.removeValue();
        SendUserToHomePage();
        Toast.makeText(this, "Post has been deleted..", Toast.LENGTH_SHORT).show();
    }

    private void SendUserToHomePage() {
        Intent mainIntent = new Intent(ClickPostActivity.this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
