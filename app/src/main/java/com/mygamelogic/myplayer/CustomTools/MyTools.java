package com.mygamelogic.myplayer.CustomTools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.mygamelogic.myplayer.R;

/**
 * Created by admin on 16/07/18.
 */

public class MyTools {
    public static void scaleAndAlphaAnimation(View view){
        view.setAlpha(0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleX).with(scaleY).with(alpha);
        scaleUp.setStartDelay(300);
        scaleUp.setInterpolator(new DecelerateInterpolator());
        scaleUp.setDuration(500);
        scaleUp.start();
    }
    public static void alphaAnim(View view){
        view.setAlpha(0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        alpha.setStartDelay(1800);
        alpha.setDuration(500);
        alpha.start();
    }

    public static int getColorBack(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
    public static void showToast(Context context,String message){
        Toast toast1 = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast1.show();
    }

    public static void hideAndScaleAnimation(View view) {
        view.setAlpha(1);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0);
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleX).with(scaleY).with(alpha);
        scaleUp.setInterpolator(new DecelerateInterpolator());
        scaleUp.setDuration(250);
        scaleUp.start();
    }

}
