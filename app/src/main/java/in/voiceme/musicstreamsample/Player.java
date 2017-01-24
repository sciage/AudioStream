package in.voiceme.musicstreamsample;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by harish on 1/24/2017.
 */

public class Player {

    MediaPlayer mediaPlayer = new MediaPlayer();
    public static Player player;
    String url = "";

    public Player(){
        this.player = this;
    }

    public void playStream(String url){
        if (mediaPlayer != null){
            try {
                mediaPlayer.stop();
            } catch (Exception e){

            }
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playPlayer();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MainActivity.flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
            } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void pausePlayer(){
        try {
            mediaPlayer.pause();
            MainActivity.flipPlayPauseButton(false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playPlayer(){
        try {
            mediaPlayer.start();
            MainActivity.flipPlayPauseButton(true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void togglePlayer(){
        try {
            if (mediaPlayer.isPlaying()){
                pausePlayer();
            } else {
                playPlayer();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
