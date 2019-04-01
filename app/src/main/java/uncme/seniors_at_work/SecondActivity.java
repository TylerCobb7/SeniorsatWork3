package uncme.seniors_at_work;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;
import android.widget.Button;
import android.widget.MediaController;

import java.net.URI;


public class SecondActivity extends AppCompatActivity {

    // create a button to go to the edit account page
    Button editAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //initialize the edit account settings button, and have it open
        // the edit account screen when clicked
        editAccountButton = findViewById(R.id.button_editAccount);
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, EditAccountSettingsActivity.class);
                startActivity(intent);
            }
        });

    } //end on Create


    public void videoPlay(View v) {

        Intent intent = new Intent(SecondActivity.this, FullscreenVideo.class);
        startActivity(intent);

    }
    public void userLogout(View v){
        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }

}

