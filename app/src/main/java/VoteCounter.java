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