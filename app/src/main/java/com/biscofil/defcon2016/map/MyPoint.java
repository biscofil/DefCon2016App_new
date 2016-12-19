package com.biscofil.defcon2016.map;

import android.graphics.Point;

/**
 * Created by bisco on 07/12/2016.
 */

public class MyPoint extends Point {
    private float mValue;

    public MyPoint(int x, int y, float v) {
        super(x,y);
        mValue = v;
    }

    public float getValue() {
        return this.mValue;
    }
}
