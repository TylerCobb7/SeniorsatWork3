package uncme.seniors_at_work;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uncme.seniors_at_work.FullscreenVideo;
import uncme.seniors_at_work.MainActivity;
import uncme.seniors_at_work.R;
import uncme.seniors_at_work.SelectedVideo;


public class SecondActivity extends AppCompatActivity {
    ImageButton sUpvoteButton;
    ImageButton sDownvoteButton;
    TextView sVoteCondition;
    int reddit;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = myRef.child("VoteScore");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        sVoteCondition = (TextView)findViewById(R.id.voteCondition);
        sUpvoteButton = (ImageButton)findViewById(R.id.upvoteButton);
        sDownvoteButton = (ImageButton)findViewById(R.id.downvoteButton);
    }

    public void videoPlay(View v) {

        Intent intent = new Intent(SecondActivity.this, FullscreenVideo.class);
        startActivity(intent);

    }
    public void videoSelected(View v) {

        Intent intent = new Intent(SecondActivity.this, SelectedVideo.class);
        startActivity(intent);

    }

    public void userLogout(View v) {
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void commentPathway(View v) {
        Intent intent = new Intent(SecondActivity.this, SelectedVideo.class);
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

    /* ********old code*********
    @Override
    protected void onStart() {
        super.onStart();

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String upvoteCounter = dataSnapshot.getValue(String.class);
                sVoteCondition.setText(upvoteCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        }); */

    }

}

