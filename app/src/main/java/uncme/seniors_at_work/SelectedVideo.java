package uncme.seniors_at_work;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class SelectedVideo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);
        String videopath = "android.resource://uncme.seniors_at_work/" + R.raw.show;
        VideoView videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse(videopath);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }


    public void videoPlay(View v) {

        Intent intent = new Intent(SelectedVideo.this, FullscreenVideo.class);
        startActivity(intent);
    }
}
