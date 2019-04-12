package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

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
                    Picasso.get().load(image).into(postImage);

                    if(currentUserID.equals(databaseUserID) || currentUserID.equals("1GJsL0PZ3HMU1HK7Wz3U0SkjOrB3")){
                        postDeleteButton.setVisibility(View.VISIBLE);
                        postEditButton.setVisibility(View.VISIBLE);
                        if(currentUserID.equals("1GJsL0PZ3HMU1HK7Wz3U0SkjOrB3")){
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
