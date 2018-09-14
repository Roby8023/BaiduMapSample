package com.raven.util;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Method;

public class UIUtil {

  private static final float DARK_ALPHA = .4F;
  private static final float BRIGHT_ALPHA = 1.0F;
  private static String TAG = "UIUtil";

  private static void moveCursor2End(Spannable text) {
    try {
      Selection.setSelection(text, text.length());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void darken(Activity activity) {
    darken(activity, false);
  }

  public static void darken(Activity activity, boolean anim) {
    changeAlpha(activity, DARK_ALPHA, anim);
  }

  public static void brighten(Activity activity) {
    brighten(activity, false);
  }

  public static void brighten(Activity activity, boolean anim) {
    changeAlpha(activity, BRIGHT_ALPHA, anim);
  }

  private static void changeAlpha(final Activity activity, float alpha, boolean anim) {
    if (activity == null) {
      return;
    }
    final WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
    if (anim) {
      float startAlpha = layoutParams.alpha;
      final ValueAnimator animation = ValueAnimator.ofFloat(startAlpha, alpha);
      animation.setDuration(300);
      animation.start();
      animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
          layoutParams.alpha = (Float) valueAnimator.getAnimatedValue();
          activity.getWindow().setAttributes(layoutParams);
        }
      });
      return;
    }
    layoutParams.alpha = alpha;
    activity.getWindow().setAttributes(layoutParams);
  }

  public static void setText(EditText editText, String text) {
    if (editText == null) {
      return;
    }
    editText.setText(text);
    moveCursor2End(editText.getText());
  }

  public static String getText(Editable editable) {
    return getText(editable, "");
  }

  public static String getText(Editable editable, String defaultValue) {
    if (editable == null) {
      return defaultValue;
    }
    return editable.toString();
  }

  public static String getText(TextView textView) {
    return getText(textView, "");
  }

  public static String getText(TextView textView, String defaultValue) {
    if (textView == null) {
      return defaultValue;
    }
    CharSequence charSequence = textView.getText();
    if (charSequence == null) {
      return defaultValue;
    }
    return charSequence.toString().trim();
  }

  public static boolean isVisible(View view) {
    return view.getVisibility() == View.VISIBLE;
  }

  public static void switchVisibleOrGone(View view) {
    if (view == null) {
      return;
    }
    view.setVisibility(isVisible(view) ? View.GONE : View.VISIBLE);
  }

  public static void setVisibleOrGone(View view, boolean condition) {
    if (view != null) {
      view.setVisibility(condition ? View.VISIBLE : View.GONE);
    }
  }

  public static void setVisibleOrInvisible(View view, boolean condition) {
    if (view != null) {
      view.setVisibility(condition ? View.VISIBLE : View.INVISIBLE);
    }
  }

  public static void setInvisible(View view) {
    setVisibleOrInvisible(view, true);
  }

  public static void setGone(View view) {
    setVisibleOrGone(view, false);
  }

  //public static void setEllipsis(final TextView textView, final int line) {
  //  ViewTreeObserver observer = textView.getViewTreeObserver();
  //  observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
  //    @Override
  //    public void onGlobalLayout() {
  //      ViewTreeObserver obs = textView.getViewTreeObserver();
  //      obs.removeGlobalOnLayoutListener(this);
  //      if (textView.getLineCount() > line) {
  //        int lineEndIndex = textView.getLayout().getLineEnd(line - 1);
  //        String text = textView.getText().subSequence(0, lineEndIndex - 3) + "...";
  //        textView.setText(text);
  //      }
  //    }
  //  });
  //}

  public static int getWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getHeight(Context context) {
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  public static int dip2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static void showSoftInput(Context context, View view) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
  }

  public static void hideSoftInput(Context context, View view) {
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  /**
   * Convert a translucent themed Activity
   * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
   * Activity.
   * <p>
   * Call this whenever the background of a translucent Activity has changed
   * to become opaque. Doing so will allow the {@link android.view.Surface} of
   * the Activity behind to be released.
   * <p>
   * This call has no effect on non-translucent activities or on activities
   * with the {@link android.R.attr#windowIsFloating} attribute.
   */
  public static void convertActivityFromTranslucent(Activity activity) {
    try {
      Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
      method.setAccessible(true);
      method.invoke(activity);
    } catch (Throwable t) {
    }
  }

  /**
   * Convert a translucent themed Activity
   * {@link android.R.attr#windowIsTranslucent} back from opaque to
   * translucent following a call to
   * {@link #convertActivityFromTranslucent(Activity)} .
   * <p>
   * Calling this allows the Activity behind this one to be seen again. Once
   * all such Activities have been redrawn
   * <p>
   * This call has no effect on non-translucent activities or on activities
   * with the {@link android.R.attr#windowIsFloating} attribute.
   */
  public static void convertActivityToTranslucent(Activity activity) {
    try {
      Class<?>[] classes = Activity.class.getDeclaredClasses();
      Class<?> translucentConversionListenerClazz = null;
      for (Class clazz : classes) {
        if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
          translucentConversionListenerClazz = clazz;
        }
      }
      Method[] methods = Activity.class.getDeclaredMethods();
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        @SuppressLint("PrivateApi") Method method =
            Activity.class.getDeclaredMethod("convertToTranslucent",
                translucentConversionListenerClazz);
        method.setAccessible(true);
        method.invoke(activity, new Object[] { null });
      } else {
        @SuppressLint("PrivateApi") Method method =
            Activity.class.getDeclaredMethod("convertToTranslucent",
                translucentConversionListenerClazz, ActivityOptions.class);
        method.setAccessible(true);
        method.invoke(activity, null, null);
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
