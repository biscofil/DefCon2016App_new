package com.biscofil.defcon2016.lib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.biscofil.defcon2016.R;
import com.biscofil.defcon2016.fragments.Menu_fragment;

public class Utils {

    public static Fragment setFragmentContent(AppCompatActivity act, Class fragmentClass, MenuItem mi, boolean first) {
        return set_fragment_content(act.getSupportFragmentManager(), fragmentClass, mi, first);
    }

    public static Fragment setFragmentContent(Fragment f, Class fragmentClass, MenuItem mi, boolean first) {
        return set_fragment_content(f.getActivity().getSupportFragmentManager(), fragmentClass, mi, first);
    }

    private static Fragment set_fragment_content(FragmentManager fm, Class fragmentClass, MenuItem item, boolean first) {
        try {
            item.setChecked(true);
            FragmentTransaction ft = fm.beginTransaction();
            String _tag = fragmentClass.getSimpleName();
            Fragment fragment = fm.findFragmentByTag(_tag);

            if (fragment == null) {
                fragment = (Fragment) fragmentClass.newInstance();
                //Log.d("BISCO",_tag + " non trovato, devo instanziare...");
            } else {
                //Log.d("BISCO",_tag + " trovato, non instanzio...");
            }

            if (first) {
               /* for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }*/
            } else {
                Fragment old = fm.findFragmentById(R.id.flContent);
                if (null == old || old.getClass().equals(Menu_fragment.class)) {
                    ft.addToBackStack(null);
                } else {

                }
            }
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            //ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.replace(R.id.flContent, fragment, _tag);
            ft.commit();

            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
