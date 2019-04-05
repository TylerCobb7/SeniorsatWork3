package uncme.seniors_at_work;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;

import uncme.seniors_at_work.AccountActivity.LoginActivity;

public class SelectedVideo extends AppCompatActivity {
    private Button postCommentButton;
    private EditText CommentInput;
    private RecyclerView CommentList;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    RecyclerView postList;
    Intent intent;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        //get auth instance
        auth = FirebaseAuth.getInstance();
        CommentList = (RecyclerView)findViewById(R.id.commentList);
        CommentList.setHasFixedSize(true);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager.setReverseLayout(true);
        LinearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(LinearLayoutManager);

        CommentInput = (EditText)findViewById(R.id.comment_input);
        postCommentButton = (Button)findViewById(R.id.commentButton);


        String videopath = "android.resource://uncme.seniors_at_work/" + R.raw.show;
        String videopath2 = "android.resource://uncme.seniors_at_work/" + R.raw.movie;
        VideoView videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse(videopath);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        //Navigation
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        }); //End of Navigation
    }

    private void UserMenuSelector(MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                intent = new Intent(SelectedVideo.this, HomePage.class);
                startActivity(intent);
                break;

            case R.id.nav_settings:
                intent = new Intent(SelectedVideo.this, EditAccountSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_Logout:
                intent = new Intent(SelectedVideo.this, LoginActivity.class);
                signOut();
                startActivity(intent);
                break;
        }
    }

    public void signOut(){
        auth.signOut();
    }

    public void comments(View v) {
        Intent intent = new Intent(SelectedVideo.this, SelectedVideo.class);
    }

    public void videoPlay(View v) {

        Intent intent = new Intent(SelectedVideo.this, FullscreenVideo.class);
        startActivity(intent);
    }
}
