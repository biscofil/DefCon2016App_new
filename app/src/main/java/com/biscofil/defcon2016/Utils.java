package com.biscofil.defcon2016;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class Utils {

    public static Fragment setFragmentContent(AppCompatActivity act, Class fragmentClass, boolean first) {
        return setFragmentContent(act.getSupportFragmentManager(), fragmentClass, first);
    }

    public static Fragment setFragmentContent(Fragment f, Class fragmentClass, boolean first) {
        return setFragmentContent(f.getActivity().getSupportFragmentManager(), fragmentClass, first);
    }


    private static Fragment setFragmentContent(FragmentManager fm, Class fragmentClass, boolean first) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

            //if (first) {
            ft.replace(R.id.flContent, fragment).commit();
            /*} else {
                ft.replace(R.id.flContent, fragment).addToBackStack(null).commit();
            }*/
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    ///

    public static int val2col(double punteggio) {
        if (!(0 <= punteggio && punteggio <= 5)) {
            throw new RuntimeException("0 < hue < 5");
        }

        float hue = ((float) (punteggio / 5.0)) * (255 / 3);

        //Log.d("BISCO", "il punteggio " + punteggio + " ha hue " + hue);

        return hsvToRgb(hue / 360, 0.75f, 0.6f);
    }

    public static int hsvToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return rgbToColor(value, t, p);
            case 1:
                return rgbToColor(q, value, p);
            case 2:
                return rgbToColor(p, value, t);
            case 3:
                return rgbToColor(p, q, value);
            case 4:
                return rgbToColor(t, p, value);
            case 5:
                return rgbToColor(value, p, q);
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }

    public static int rgbToColor(float r, float g, float b) {
        return Color.rgb((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
