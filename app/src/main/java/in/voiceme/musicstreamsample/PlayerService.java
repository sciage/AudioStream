package in.voiceme.musicstreamsample;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class PlayerService extends Service {
    MediaPlayer mediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder{
        PlayerService getService(){
            return PlayerService.this;
        }
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playStream(intent.getStringExtra("url"));
    return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
                    flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void flipPlayPauseButton(Boolean isPlaying){
        Intent intent = new Intent("changePlayButton");
        intent.putExtra("isPlaying", isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void pausePlayer(){
        try {
            mediaPlayer.pause();
            flipPlayPauseButton(false);

            unregisterReceiver(noisyAudioStreamReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playPlayer(){
        try {
            mediaPlayer.start();
            flipPlayPauseButton(true);
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

    // Audio Focus Section
    private AudioManager am;
    private boolean playingBeforeInterruption = false;

    public void getAudioFocusAndPlay(){
        am = (AudioManager) this.getBaseContext().getSystemService((Context.AUDIO_SERVICE));

        int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
            registerReceiver(noisyAudioStreamReceiver, filter);
        }
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // A Phone Call coming in, audio will reduce slowly
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                if (mediaPlayer.isPlaying()){
                    playingBeforeInterruption = true;
                } else {
                    playingBeforeInterruption = false;
                }
                pausePlayer();
                // Gained back audio focus
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){
                if (playingBeforeInterruption){
                    pausePlayer();
                }
                // Lost audio focus completely
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                pausePlayer();
                // TO prevent app to auto start audio after getting stopped
                am.abandonAudioFocus(afChangeListener);
            }
        }
    };

    // Audio Rerouted
    private class NoisyAudioStreamReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                pausePlayer();
            }
        }
    }

    private IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();

}
