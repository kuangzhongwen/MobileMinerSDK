package waterhole.miner.monero.keepappalive.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import waterhole.miner.core.R;

/**
 * 循环播放一段无声音频，以提升进程优先级
 */
public class PlayerMusicService extends Service {

    private final static String TAG = "PlayerMusicService";
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + "---->onCreate,启动服务");
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), waterhole.miner.monero.R.raw.silent);
        mMediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startPlayMusic();
            }
        }).start();
        return START_STICKY;
    }

    private void startPlayMusic() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "启动后台播放音乐");
            mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "关闭后台播放音乐");
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        Log.d(TAG, TAG + "---->onDestroy,停止服务");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), PlayerMusicService.class);
        startService(intent);
    }
}
