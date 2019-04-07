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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ImageButton selectPostImage;
    Button updatePostButton;
    EditText postDescription;
    Uri imageUri;
    String description;
    String saveCurrentDate;
    String saveCurrentTime;
    String postRandomName;

    public static final int Gallery_Pick = 1;
    StorageReference postReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        selectPostImage = (ImageButton) findViewById(R.id.postImageButton);
        updatePostButton = (Button) findViewById(R.id.postButton);
        postDescription = (EditText) findViewById(R.id.postImageDescription);
        postReferences = FirebaseStorage.getInstance().getReference();



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

    private void ValidatePostInfo() {
        description = postDescription.getText().toString();

        if(imageUri == null){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Post description is empty...", Toast.LENGTH_SHORT).show();
        }
        else{
            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());
        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = postReferences.child("Post Media").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(PostActivity.this, "Image Uploaded Successfully to Storage....", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }
    }
}
