package in.voiceme.musicstreamsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://s3-us-west-2.amazonaws.com/voiceme-audio-bucket/1484987887currentRecording.mp3";
        if (Player.player == null){
            new Player();
        }
        Player.player.playStream(url);
    }
}
