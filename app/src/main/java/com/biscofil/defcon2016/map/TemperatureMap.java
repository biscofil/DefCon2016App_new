package com.biscofil.defcon2016.map;

import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.maps.android.geometry.Bounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.attr.value;

/**
 * Created by bisco on 07/12/2016.
 */

public class TemperatureMap {

    //this.ctx = ctx;
    private List<MyPoint> points;
    private Point[] poligoni;
    private Bounds limits = new Bounds(0, 0, 0, 0);

    private Point size;
    private int width, height;

    public TemperatureMap(Point k) {
        size = new Point(k.x, k.y);
    }

    public float crossProduct(Point o, Point a, Point b) {
        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }

    public boolean pointInPolygon(Point point, Point[] vs) {
        int x = point.x;
        int y = point.y;
        boolean inside = false;
        int i = 0;
        int j = 0;
        int xi = 0;
        int xj = 0;
        int yi = 0;
        int yj = 0;
        boolean intersect = false;

        j = vs.length - 1;
        for (i = 0; i < vs.length; i = i + 1) {
            xi = vs[i].x;
            yi = vs[i].y;
            xj = vs[j].x;
            yj = vs[j].y;

            intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) {
                inside = !inside;
            }
            j = i;
        }

        return inside;
    }

    public float squareDistance(Point p0, Point p1) {
        float x = p0.x - p1.x;
        float y = p0.y - p1.y;
        return x * x + y * y;
    }

    public float hue2rgb(float p, float q, float t) {
        if (t < 0) {
            t += 1;
        } else if (t > 1) {
            t -= 1;
        }
        if (t >= 0.66) {
            return p;
        } else if (t >= 0.5) {
            return p + (q - p) * (0.66f - t) * 6f;
        } else if (t >= 0.33) {
            return q;
        } else {
            return p + (q - p) * 6 * t;
        }
    }

    public int hslToRgb(float h, float s, float l) {
        float r, g, b, q, p;
        if (s == 0) {
            r = g = b = l;
        } else {

            q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            p = 2 * l - q;
            r = hue2rgb(p, q, h + 0.33f);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 0.33f);
        }

        int _r = (int) (r * 255);
        int _g = (int) (g * 255);
        int _b = (int) (b * 255);

        return 1; //new Color.rgb(_r,_g,_b);
    }

    public int getColor(boolean levels, float value) {
        float val = value;
        float tmp = 0;
        float lim = 0.55f;
        float min = -30;
        float max = 50;
        float dif = max - min;
        float lvs = 25;

        if (val < min) {
            val = min;
        }
        if (val > max) {
            val = max;
        }

        tmp = 1 - (1 - lim) - (((val - min) * lim) / dif);

        if (levels) {
            tmp = Math.round(tmp * lvs) / lvs;
        }

        return hslToRgb(tmp, 1f, 0.5f);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double getPointValue(int limit, Point point) {
        int counter = 0;
        //Point arr[];

        //Map<Float,Integer> arr = new HashMap<>();
        List<Float> arr = new ArrayList<>();

        float tmp = 0.0f;
        float dis = 0.0f;
        double inv = 0.0f;
        double t = 0.0f;
        double b = 0.0f;
        int pwr = 2;
        MyPoint ptr;

        // From : https://en.wikipedia.org/wiki/Inverse_distance_weighting

        if (pointInPolygon(point, this.poligoni)) {

            for (counter = 0; counter < this.points.size(); counter = counter + 1) {
                dis = squareDistance(point, this.points.get(counter));
                if (dis == 0) {
                    return this.points.get(counter).getValue();
                }
                //arr[counter] =[dis, counter];
                arr.add(counter, dis);
            }

            Collections.sort(arr, new Comparator<Float>() {
                public int compare(Float p1, Float p2) {
                    return p1.compareTo(p2);
                }
            });

            for (counter = 0; counter < limit; counter = counter + 1) {
                //ptr = arr[counter];
                //inv = 1 / Math.pow(ptr[0], pwr); //dis
                inv = 1 / Math.pow(arr.get(counter), pwr); //dis
                //t = t + inv * this.points[ptr[1]].value; //counter
                t = t + inv * this.points.get(counter).getValue(); //counter
                b = b + inv;
            }

            return t / b;

        } else {
            return -255;
        }
    }

    public void setConvexhullPolygon(List<MyPoint> points) {

        /*var lower = [],
        upper = [],
        i = 0;

        // Sort by 'y' to get yMin/yMax
        points.sort(function (a, b) {
            return a.y === b.y ? a.x - b.x : a.y - b.y;
        });

        this.limits.yMin = points[0].y;
        this.limits.yMax = points[points.length - 1].y;

        // Sort by 'x' to get convex hull polygon and xMin/xMax
        points.sort(function (a, b) {
            return a.x === b.x ? a.y - b.y : a.x - b.x;
        });

        this.limits.xMin = points[0].x;
        this.limits.xMax = points[points.length - 1].x;

        // Get convex hull polygon from points sorted by 'x'
        for (i = 0; i < points.length; i = i + 1) {
            while (lower.length >= 2 && TemperatureMap.crossProduct(lower[lower.length - 2], lower[lower.length - 1], points[i]) <= 0) {
                lower.pop();
            }
            lower.push(points[i]);
        }

        for (i = points.length - 1; i >= 0; i = i - 1) {
            while (upper.length >= 2 && TemperatureMap.crossProduct(upper[upper.length - 2], upper[upper.length - 1], points[i]) <= 0) {
                upper.pop();
            }
            upper.push(points[i]);
        }

        upper.pop();
        lower.pop();
        this.polygon = lower.concat(upper);*/
    }

    public void setPoints(List<MyPoint> arr, int width, int height) {
        this.points = arr;
        this.width = width;
        this.height = height;
        this.setConvexhullPolygon(this.points);
    }

    public void setRandomPoints() {
        int counter = 0;
        int x = 0;
        int y = 0;
        float v = 0.0f;
        List<MyPoint> rst = new ArrayList<>();

        for (counter = 0; counter < points.size(); counter = counter + 1) {

            x = (int) ((Math.random() * 100000) % width);
            y = (int) ((Math.random() * 100000) % height);
            v = (float) ((Math.random() * 100) / 2);

            if (Math.random() > 0.5) {
                v = -v;
            }
            if (Math.random() > 0.5) {
                v = v + 30;
            }

            rst.add(new MyPoint(x,y,v));
        }

        this.setPoints(rst, width, height);
    }


}
