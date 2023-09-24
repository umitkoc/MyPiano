package com.example.mypiano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;


public class PianoView extends View {

    public static final int NB=14;

    private Paint black,yellow,white;

    public HashMap<Integer,Key> whites=new HashMap<>();
    public HashMap<Integer,Key> blacks=new HashMap<>();

    private int keyWidth,keyHeight;
    private AudioSoundPlayer soundPlayer;




    public PianoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.black=new Paint();
        black.setColor(Color.BLACK);
        this.white=new Paint();
        this.white.setColor(Color.WHITE);
        this.yellow=new Paint();
        this.yellow.setColor(Color.YELLOW);
        this.yellow.setStyle(Paint.Style.FILL);
        soundPlayer=new AudioSoundPlayer(context);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.keyWidth=w/NB;
        this.keyHeight=h;
        int count=15;
        for (int i=0;i<NB;i++){
            int left=i*keyWidth;
            int right=left+keyWidth;
            if(i==NB-1){
                right=w;
            }
            RectF rect=new RectF(left,0,right,h);
            whites.put(i+1,new Key(rect,i+1));
            if(i!=0 && i!=3 &&i!=7 && i!=10){
                rect=new RectF((float)(i-1)*keyWidth+ 0.5f *keyWidth+ 0.25f *keyWidth,0,(float) i*keyWidth+0.25f*keyWidth,0.67f*keyHeight);
                blacks.put(count,new Key(rect,count));
                count++;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        for (Key key:whites.values()){
            canvas.drawRect(key.rect,key.down?yellow:white);
        }
        for (int i=0;i<NB;i++){
            canvas.drawLine(i*keyWidth,0,i*keyWidth,keyHeight,black);
        }

        for (Key key:blacks.values()){
            canvas.drawRect(key.rect,key.down?yellow:black);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        boolean isDownAction=action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_MOVE;
        for (int i=0;i<event.getPointerCount();i++){
            float x=event.getX(i);
            float y=event.getY(i);
            Key k= keyForCoords(x,y);
            if(k!=null){
                k.down=isDownAction;

            }
        }
        ArrayList<Key> tmp=new ArrayList<>(whites.values());
        tmp.addAll(blacks.values());

        for(Key key:tmp){
            if(key.down){
                if(soundPlayer.isNotePlaying(key.sound)){
                    soundPlayer.playNote(key.sound);
                    invalidate();
                }else{
                    releaseKey(key);
                }
            }else{
                soundPlayer.stopNote(key.sound);
                releaseKey(key);
            }
        }
        return true;

    }

    private Key keyForCoords(float x, float y){
        for(Key key:whites.values()){
            if(key.rect.contains(x,y)){
                return key;
            }
        }

        return null;
    }

    private void releaseKey(final Key key){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                key.down=false;
                handler.sendEmptyMessage(0);
            }
        },100);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            invalidate();
        }
    };


}
