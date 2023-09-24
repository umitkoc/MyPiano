package com.example.mypiano;

import android.graphics.RectF;

public class Key {
    public Key( RectF rect,int sound) {
        this.sound = sound;
        this.rect = rect;
    }

    public int sound;
    public RectF rect;
    public boolean down;
}
