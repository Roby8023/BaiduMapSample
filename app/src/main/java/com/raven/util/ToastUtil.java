package com.raven.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {

  private static Toast sToast;

  private ToastUtil() {
    /* cannot be instantiated */
    throw new UnsupportedOperationException("cannot be instantiated");
  }

  /**
   * 短时间显示Toast
   */
  public static void showShort(Context context, CharSequence message) {
    if (TextUtils.isEmpty(message)) {
      message = "service code:-1";
    }
    if (sToast == null) {
      sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    } else {
      sToast.setText(message);
      sToast.setDuration(Toast.LENGTH_SHORT);
    }
    sToast.show();
  }

  /**
   * 短时间显示Toast
   */
  public static void showShort(Context context, String message) {
    if (TextUtils.isEmpty(message)) {
      message = "service code:-1";
    }
    if (sToast == null) {
      sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    } else {
      sToast.setText(message);
      sToast.setDuration(Toast.LENGTH_SHORT);
    }
    sToast.show();
  }

  /**
   * 直接用string文件的id
   */
  public static void showShort(Context context, @StringRes int stringID) {
    if (sToast == null) {
      sToast =
          Toast.makeText(context, context.getResources().getString(stringID), Toast.LENGTH_SHORT);
    } else {
      sToast.setText(context.getResources().getString(stringID));
      sToast.setDuration(Toast.LENGTH_SHORT);
    }
    sToast.show();
  }
}