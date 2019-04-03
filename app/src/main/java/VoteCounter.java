
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import uncme.seniors_at_work.R;
import uncme.seniors_at_work.SecondActivity;
/*
public class VoteCounter extends SecondActivity {
    ImageButton sUpvoteButton;
    ImageButton sDownvoteButton;
    TextView sVoteCondition;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = myRef.child("VoteScore");
    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sVoteCondition = (TextView)findViewById(R.id.voteCondition);
        sUpvoteButton = (ImageButton)findViewById(R.id.upvoteButton);
        sDownvoteButton = (ImageButton)findViewById(R.id.downvoteButton);
    }
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
                mConditionRef.setValue("Hey");
            }
        });
        sDownvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConditionRef.setValue("Goodbye");
            }
        });
    }
}
*/