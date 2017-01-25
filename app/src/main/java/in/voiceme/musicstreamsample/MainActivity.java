package in.voiceme.musicstreamsample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mbanje.kurt.fabbutton.FabButton;

public class MainActivity extends AppCompatActivity {
    FabButton flipButton;
    private ProgressHelper helper;

    PlayerService mBoundService;
    boolean mServiceBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
            flipPlayPauseButton(isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        flipButton = (FabButton) findViewById(R.id.playbutton);

        helper = new ProgressHelper(flipButton,this);

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mServiceBound){
                    startStreamService("https://s3-us-west-2.amazonaws.com/voiceme-audio-bucket/1484987887currentRecording.mp3");
                    helper.startIndeterminate();

                } else {
                    mBoundService.togglePlayer();
                    helper.stopDeterminate();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("changePlayButton"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    private void startStreamService(String url){
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("url", url);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void flipPlayPauseButton(boolean isPlaying){
        if (isPlaying){
            flipButton.setIcon(R.drawable.stop_button, R.drawable.ic_fab_play);
            helper.stopDeterminate();
        } else {
            flipButton.setIcon(R.drawable.play_button, R.drawable.stop_button);
        }
    }
}
