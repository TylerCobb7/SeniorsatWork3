package uncme.seniors_at_work;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class SelectedVideo extends AppCompatActivity {
    private Button postCommentButton;
    private EditText CommentInput;
    private RecyclerView CommentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        CommentList = (RecyclerView)findViewById(R.id.commentList);
        CommentList.setHasFixedSize(true);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager.setReverseLayout(true);
        LinearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(LinearLayoutManager);

        CommentInput = (EditText)findViewById(R.id.comment_input);
        postCommentButton = (Button)findViewById(R.id.commentButton);


        String videopath = "android.resource://uncme.seniors_at_work/" + R.raw.show;
        VideoView videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse(videopath);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }
    public void comments(View v) {
        Intent intent = new Intent(SelectedVideo.this, SelectedVideo.class);

    }

    public void videoPlay(View v) {

        Intent intent = new Intent(SelectedVideo.this, FullscreenVideo.class);
        startActivity(intent);
    }
}
