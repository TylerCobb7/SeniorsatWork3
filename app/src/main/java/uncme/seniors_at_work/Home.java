package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import uncme.seniors_at_work.AccountActivity.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uncme.seniors_at_work.AccountActivity.LoginActivity;

public class Home extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    String SERIALIZE_DATA = "serializedata";
    ImageButton sUpvoteButton;
    ImageButton sDownvoteButton;
    TextView sVoteCondition;
    TextView postUserName;
    ImageButton settingsButton;
    int reddit;
    User user;
    Intent intent;
    FirebaseAuth auth;
    ImageButton addNewPostButton;

    String currentUserID;

    CircleImageView NavProfileImage;
    TextView NavProfileName;

    DatabaseReference myRef, postsRef;
    DatabaseReference mConditionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        //get the user name text view and put the username of the user in it
        postUserName = findViewById(R.id.post_user_name);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarToggle = new ActionBarDrawerToggle(Home.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileName = (TextView) navView.findViewById(R.id.nav_userName);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });


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
                        Toast.makeText(Home.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
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
        });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });

        DisplayAllUsersPosts();

    }

    private void DisplayAllUsersPosts() {
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postsRef,Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> FirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
                final String PostKey = getRef(position).getKey();

                holder.username.setText(model.getUsername());
                holder.time.setText(" " + model.getTime());
                holder.date.setText(" "+ model.getDate());
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileImage()).into(holder.user_post_image);
                Picasso.get().load(model.getPostImage()).into(holder.postImage);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(Home.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout,parent,false);
                PostsViewHolder viewHolder=new PostsViewHolder(view);
                return viewHolder;
            }
        };
        postList.setAdapter(FirebaseRecyclerAdapter);
        FirebaseRecyclerAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        TextView username,date,time,description;
        CircleImageView user_post_image;
        ImageView postImage;
        View mView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username=itemView.findViewById(R.id.post_user_name);
            date=itemView.findViewById(R.id.post_date);
            time=itemView.findViewById(R.id.post_time);
            description=itemView.findViewById(R.id.post_description);
            postImage=itemView.findViewById(R.id.post_image);
            user_post_image=itemView.findViewById(R.id.post_profile_image);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToPostActivity() {
        Intent intent = new Intent(Home.this, PostActivity.class);
        startActivity(intent);

    }

    public void signOut(){
        auth.signOut();
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
                intent = new Intent(Home.this, Home.class);
                startActivity(intent);
                break;

            case R.id.nav_settings:
                intent = new Intent(Home.this, EditAccountSettingsActivity.class);
                drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
                drawerLayout.closeDrawers();
                startActivity(intent);
                break;

            case R.id.nav_Logout:
                intent = new Intent(Home.this, LoginActivity.class);
                signOut();
                startActivity(intent);
                break;
        }
    }
}