package uncme.seniors_at_work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView commentLists;
    ImageButton postCommentBtn;
    EditText postCommentInput;

    String Post_Key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        commentLists = (RecyclerView)findViewById(R.id.comment_Lists);
        commentLists.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentLists.setLayoutManager(linearLayoutManager);


        postCommentBtn = (ImageButton)findViewById(R.id.post_Comment_Button);
        postCommentInput = (EditText)findViewById(R.id.comment_Input);
    }
}
