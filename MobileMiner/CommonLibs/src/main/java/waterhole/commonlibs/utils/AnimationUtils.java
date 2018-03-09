/*
 * Copyright (C) 2014 Zlianjie Inc. All rights reserved.
 */
package waterhole.commonlibs.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

/**
 * 动画播放辅助工具类，用于播放一些较简单的过渡动画。
 *
 * @author kzw on 2015/11/04.
 */
public final class AnimationUtils {

    // 短动画时长
    public static final long DURATION_SHORT = 300L;
    // 中动画时长
    public static final long DURATION_MEDIUM = 500L;
    // 长动画时长
    public static final long DURATION_LONG = 900L;

    private AnimationUtils() {
    }

    public static void fadeIn(final View view, long duration) {
        fadeIn(view, duration, null);
    }

    public static void fadeIn(final View view, long duration, Animator.AnimatorListener adapter) {
        fadeIn(view, duration, adapter, 0f, 1f);
    }

    public static void fadeIn(final View view, long duration, Animator.AnimatorListener adapter,
                              float startAlpha, float endAlpha) {
        if (view != null) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha);
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view.getVisibility() != View.VISIBLE) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            });
            if (adapter != null) {
                anim.addListener(adapter);
            }
            anim.start();
        }
    }

    public static void fadeOut(final View view, long duration) {
        fadeOut(view, duration, null);
    }

    public static void fadeOut(final View view, long duration, Animator.AnimatorListener adapter) {
        if (view != null) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
            if (adapter != null) {
                anim.addListener(adapter);
            }
            anim.start();
        }
    }

    public static void animateBack(View backView, AnimatorListenerAdapter listener) {
        if (backView == null) {
            return;
        }
        ObjectAnimator backAnimator = ObjectAnimator.ofFloat(backView, "translationY",
                backView.getTranslationY(), 0f);
        backAnimator.setDuration(150);
        if (listener != null) {
            backAnimator.addListener(listener);
        }
        backAnimator.start();
    }

    public static void animateHide(View hideView, AnimatorListenerAdapter listener) {
        if (hideView == null) {
            return;
        }
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(hideView, "translationY",
                hideView.getTranslationY(), -hideView.getHeight());
        hideAnimator.setDuration(150);
        if (listener != null) {
            hideAnimator.addListener(listener);
        }
        hideAnimator.start();
    }

    public static AlphaAnimation getFadeInAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(250);
        return animation;
    }

    public static void animate(View view, int durationMillis) {
        if (view != null) {
            AlphaAnimation fade = new AlphaAnimation(0.0F, 1.0F);
            fade.setDuration((long) durationMillis);
            fade.setInterpolator(new DecelerateInterpolator());
            view.startAnimation(fade);
        }
    }

    public static void scaleIn(final View view, long durationMillis) {
        if (view != null && durationMillis > 0) {
            view.clearAnimation();
            view.setVisibility(View.GONE);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", .3f, 1.f);
            scaleX.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view.getVisibility() != View.VISIBLE) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            });
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", .3f, 1.f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(durationMillis);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }
}
