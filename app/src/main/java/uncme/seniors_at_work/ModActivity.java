package uncme.seniors_at_work;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ModActivity extends AppCompatActivity {

    RecyclerView userPostList;

    ImageButton sUpvoteButton;
    ImageButton sDownvoteButton;
    TextView sVoteCondition;
    TextView postUserName;
    User user;
    Intent intent;
    FirebaseAuth auth;
    String currentUserID;
    CircleImageView NavProfileImage;
    TextView NavProfileName;
    DatabaseReference myRef, postsRef;
    DatabaseReference mConditionRef;
    DatabaseReference flaggedRef;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod);

        sVoteCondition = (TextView) findViewById(R.id.voteCondition);
        sUpvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        sDownvoteButton = (ImageButton) findViewById(R.id.downvoteButton);
        NavProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);
        NavProfileName = (TextView) findViewById(R.id.my_profile_username);

        //get auth instance
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mConditionRef = FirebaseDatabase.getInstance().getReference().child("VoteScore");
        postsRef = FirebaseDatabase.getInstance().getReference("Posts");
        flaggedRef = FirebaseDatabase.getInstance().getReference().child("Flagged Videos");
        userKey = postsRef.getKey();


        //get the user name text view and put the username of the user in it
        postUserName = findViewById(R.id.post_user_name);


        userPostList = (RecyclerView) findViewById(R.id.reported_videos_list);
        userPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userPostList.setLayoutManager(linearLayoutManager);


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
                        Toast.makeText(ModActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DisplayAllFlaggedPosts();
    }

    private void DisplayAllFlaggedPosts() {

        Query flagged = postsRef.orderByChild("flagged").equalTo("true");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(flagged, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, Home.PostsViewHolder> FirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, Home.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Home.PostsViewHolder holder, int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();

                holder.username.setText(model.getUsername());
                holder.time.setText(" " + model.getTime());
                holder.date.setText(" " + model.getDate());
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileImage()).into(holder.user_post_image);
                Picasso.get().load(model.getPostImage()).into(holder.postImage);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(ModActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });
            }
            @NonNull
            @Override
            public Home.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent ,false);
                Home.PostsViewHolder viewHolder=new Home.PostsViewHolder(view);
                return viewHolder;
            }
        };
        userPostList.setAdapter(FirebaseRecyclerAdapter);
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

}
