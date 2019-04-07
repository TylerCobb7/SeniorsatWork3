package uncme.seniors_at_work;

import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import uncme.seniors_at_work.AccountActivity.LoginActivity;


public class HomePage extends AppCompatActivity {

    String SERIALIZE_DATA = "serializedata";
    ImageButton sUpvoteButton;
    ImageButton sDownvoteButton;
    TextView sVoteCondition;
    TextView postUserName;
    ImageButton settingsButton;
    int reddit;
    User user;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RecyclerView postList;
    Intent intent;
    FirebaseAuth auth;
    ImageButton addNewPostButton;

    String currentUserID;

    CircleImageView NavProfileImage;
    TextView NavProfileName;

    DatabaseReference myRef;
    DatabaseReference mConditionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        sVoteCondition = (TextView) findViewById(R.id.voteCondition);
        sUpvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        sDownvoteButton = (ImageButton) findViewById(R.id.downvoteButton);
        addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);
        user = (User) getIntent().getSerializableExtra("serializedata");

        //get auth instance
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mConditionRef = FirebaseDatabase.getInstance().getReference().child("VoteScore");

        //get the user name text view and put the username of the user in it
        postUserName = findViewById(R.id.post_user_name);
        //postUserName.setText(user.getUsername());

        //functionality for account settings button

        //Navigation
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileName = (TextView) navView.findViewById(R.id.nav_userName);

        myRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("username")){
                        String username = dataSnapshot.child("username").getValue().toString();
                        NavProfileName.setText(username);
                    }
                    if (dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(image).into(NavProfileImage);
                    } else {
                        Toast.makeText(HomePage.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        }); //End of Navigation

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });
    }

    private void SendUserToPostActivity() {
        Intent intent = new Intent(HomePage.this, PostActivity.class);
        startActivity(intent);

    }

    private void UserMenuSelector(MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                intent = new Intent(HomePage.this, HomePage.class);
                intent.putExtra(SERIALIZE_DATA, user);
                startActivity(intent);
                break;

            case R.id.nav_settings:
                intent = new Intent(HomePage.this, EditAccountSettingsActivity.class);
                intent.putExtra(SERIALIZE_DATA, user);
                drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
                drawerLayout.closeDrawers();
                startActivity(intent);
                break;

            case R.id.nav_Logout:
                intent = new Intent(HomePage.this, LoginActivity.class);
                intent.putExtra(SERIALIZE_DATA, user);
                signOut();
                startActivity(intent);
                break;
        }
    }

    public void signOut(){
        auth.signOut();
    }
    public void videoPlay(View v) {

        Intent intent = new Intent(HomePage.this, FullscreenVideo.class);
        startActivity(intent);

    }
    public void videoSelected(View v) {

        Intent intent = new Intent(HomePage.this, SelectedVideo.class);
        startActivity(intent);

    }

    public void userLogout(View v) {
        Intent intent = new Intent(HomePage.this, MainActivity.class);
        startActivity(intent);
    }

    public void commentPathway(View v) {
        Intent intent = new Intent(HomePage.this, SelectedVideo.class);
        startActivity(intent);
    }
    // ********lais code**************
    @Override
    protected void onStart() {
        super.onStart();

        sUpvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConditionRef.setValue("1");
            }
        });
        sDownvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConditionRef.setValue("-1");
            }
        });

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int upvoteCounter = 0;
                //Check firebase for a "1" for upvote or "-1" for downvote
                if(dataSnapshot.getValue().equals("1")) {
                    //Increased number based on "1" or "-1"
                    sVoteCondition.setText(Integer.toString(upvoteCounter + 1));
                }
                else{
                    sVoteCondition.setText(Integer.toString(upvoteCounter - 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
