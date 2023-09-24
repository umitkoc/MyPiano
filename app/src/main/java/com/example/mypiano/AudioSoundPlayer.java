package com.example.mypiano;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.SparseArray;

import java.io.InputStream;

public class AudioSoundPlayer {
    private SparseArray<PlayThread> threadMap=null;
    private static final SparseArray<String> SOUND_MAP=new SparseArray<>();
    private static final int MAX_VOLUME=100,CURRENT_VOLUME=90;

    private Context context;
    public  AudioSoundPlayer(Context context){
        this.context=context;
        threadMap=new SparseArray<>();
    }

    public void playNote(int note){
        if(!isNotePlaying(note)){
            PlayThread thread=new PlayThread(note);
            thread.start();
            threadMap.put(note,thread);
        }
    }
    public void stopNote(int note){
        PlayThread thread=threadMap.get(note);

        if(thread!=null){
            threadMap.remove(note);
        }
    }

    public boolean isNotePlaying(int note){
        return threadMap.get(note)!=null;
    }

    static {
        SOUND_MAP.put(1,"note_do");
        SOUND_MAP.put(2,"note_re");
        SOUND_MAP.put(3,"note_mi");
        SOUND_MAP.put(4,"note_fa");
        SOUND_MAP.put(5,"note_sol");
        SOUND_MAP.put(6,"note_la");
        SOUND_MAP.put(7,"note_si");

        SOUND_MAP.put(8,"second_do");
        SOUND_MAP.put(9,"second_re");
        SOUND_MAP.put(10,"second_mi");
        SOUND_MAP.put(11,"second_fa");
        SOUND_MAP.put(12,"second_sol");
        SOUND_MAP.put(13,"second_la");
        SOUND_MAP.put(14,"second_si");

        SOUND_MAP.put(15,"do_dies");
        SOUND_MAP.put(16,"re_dies");
        SOUND_MAP.put(17,"fa_dies");
        SOUND_MAP.put(18,"sol_dies");
        SOUND_MAP.put(19,"la_dies");

        SOUND_MAP.put(15,"second_dies_do");
        SOUND_MAP.put(16,"second_dies_re");
        SOUND_MAP.put(17,"second_dies_fa");
        SOUND_MAP.put(18,"second_dies_sol");
        SOUND_MAP.put(19,"second_dies_la");

    }

private  class PlayThread extends Thread{
       int note;
       AudioTrack audioTrack;

       public  PlayThread(int note){
           this.note=note;
       }

    @Override
    public void run() {
        try {
            String path=SOUND_MAP.get(note)+"wav";
            AssetManager assetManager=context.getAssets();
            AssetFileDescriptor ad=assetManager.openFd(path);
            long fileSize=ad.getLength();
            int bufferSize=4096;
            byte[] buffer=new byte[bufferSize];

            audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, 44100,AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize,AudioTrack.MODE_STREAM);
            float logVolume=(float) (1-(Math.log(MAX_VOLUME-CURRENT_VOLUME)/Math.log(MAX_VOLUME)));
            audioTrack.setStereoVolume(logVolume,logVolume);
            audioTrack.play();
            InputStream audioStream=null;
            int headerOffset=0x2C;
            long bytesWritten=0;
            int bytesRead=0;

            audioStream=assetManager.open(path);
            audioStream.read(buffer,0,headerOffset);
            while (bytesWritten<fileSize-headerOffset){
                bytesRead=audioStream.read(buffer,0,headerOffset);
                bytesWritten+=audioTrack.write(buffer,0,bytesRead);
            }
            audioTrack.stop();
            audioTrack.release();

        }catch (Exception error){
                audioTrack.release();
        }
    }
}

}
