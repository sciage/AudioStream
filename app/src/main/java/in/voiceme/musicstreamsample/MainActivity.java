package in.voiceme.musicstreamsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static TextView flipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flipButton = (TextView) findViewById(R.id.playbutton);

        String url = "https://s3-us-west-2.amazonaws.com/voiceme-audio-bucket/1484987887currentRecording.mp3";
        if (Player.player == null){
            new Player();
        }
        Player.player.playStream(url);

        flipButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    public static void flipPlayPauseButton(boolean isPlaying){
        if (isPlaying){
            flipButton.setText("playing");
            // flipbutton.setImageResource(pauseButton);
        } {
            flipButton.setText("play");
            // flipbutton.setImageResource(playButton);
        }
    }
}
