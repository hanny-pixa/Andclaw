package com.andforce.mdm.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerHelperDouble {
    private MediaPlayer mPlayer;
    private boolean isPause = false;

    public MediaPlayerHelperDouble playSound(Context context, int filePath) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(mediaPlayer -> release());

        mPlayer.setOnPreparedListener(mp -> {
            mPlayer.start();
            isPause = false;
        });
        mPlayer.setOnErrorListener((mp, what, extra) -> {
            mPlayer.reset();
            return false;
        });
        try {
            AssetFileDescriptor assest = context.getResources().openRawResourceFd(filePath);
            mPlayer.setDataSource(assest.getFileDescriptor(), assest.getStartOffset(), assest.getLength());
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
//            throw new RuntimeException("读取文件异常：" + e.getMessage());
        }
        return this;
    }


    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    // 继续
    public void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

}
