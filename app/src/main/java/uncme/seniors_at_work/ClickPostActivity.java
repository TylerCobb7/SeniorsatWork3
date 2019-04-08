package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    ImageView postImage;
    TextView postDescription;
    Button postEditButton;
    Button postDeleteButton;
    DatabaseReference clickPostRef;

    String PostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        postImage = (ImageView)findViewById(R.id.click_post_image);
        postDescription = (TextView)findViewById(R.id.click_post_description);
        postEditButton = (Button)findViewById(R.id.edit_post_button);
        postDeleteButton = (Button)findViewById(R.id.delete_post_button);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String description = dataSnapshot.child("description").getValue().toString();
                String image = dataSnapshot.child("postImage").getValue().toString();

                postDescription.setText(description);
                Picasso.get().load(image).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
