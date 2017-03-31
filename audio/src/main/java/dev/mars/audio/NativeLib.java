package dev.mars.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ma.xuanwei on 2017/3/7.
 */

public class NativeLib {

    private AtomicBoolean isRecording = new AtomicBoolean(false);
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private AtomicBoolean isRecordAndPlay = new AtomicBoolean(false);

    static {
        System.loadLibrary("native");
    }

    public void setIsRecording(boolean v) {
        isRecording.set(v);
        LogUtils.DEBUG("setIsRecording " + v);
    }

    public void setIsRecordingAndPlaying(boolean v) {
        isRecordAndPlay.set(v);
        LogUtils.DEBUG("setIsRecordingAndPlaying " + v);
    }

    public boolean isRecording() {
        return isRecording.get();
    }

    public void setIsPlaying(boolean b) {
        isPlaying.set(b);
        LogUtils.DEBUG("setIsPlaying " + b);
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public boolean isRecordingAndPlaying() {
        return isRecordAndPlay.get();
    }

    public native void startRecording(int sampleRate, int period, int channels, String path);

    public native void stopRecording();

    public native void playRecording(int sampleRate, int period, int channels, String path);

    public native void stopPlaying();

    public native int encode(String pcm, String speex);

    public native int decode(String speex, String pcm);

    public native int recordAndPlayPCM(boolean enableProcess, boolean enableEchoCancel);

    public native int stopRecordingAndPlaying();

    BlockingQueue<AudioFrame> audioFrames;

    public void startPlay2(BlockingQueue<AudioFrame> audioFrames) {
        this.audioFrames = audioFrames;
        playRecording2(8000, 20, 1);
    }

    public native void startRecording2(int sampleRate, int period, int channels);

    public native void playRecording2(int sampleRate, int period, int channels);

    public byte[] getOneFrame() {

        //AudioFrame audioFrame = audioFrames.take();
        AudioFrame audioFrame = audioFrames.poll(); //非阻塞
        if (audioFrame == null) {
            return null;
        }
        LogUtils.DEBUG("JNI 从 JAVA层取走byte[]数据");
        return audioFrame.data;
    }

    public void onRecord(byte[] bytes) {
        LogUtils.DEBUG("收到native传来的bytes，长度 = " + (bytes == null ? 0 : bytes.length));
        if (onRecordListener != null) {
            onRecordListener.onRecord(bytes);
        }
    }

    public void onRecordStart() {
        if (onRecordListener != null)
            onRecordListener.onStart();
    }

    OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener l) {
        onRecordListener = l;
    }

    public interface OnRecordListener {
        void onRecord(byte[] datas);

        void onStart();
    }
}
