package com.raven.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager mgr =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo[] info = mgr.getAllNetworkInfo();
    if (info != null) {
      for (int i = 0; i < info.length; i++) {
        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isNetworkConnected(Context context) {
    if (context != null) {
      ConnectivityManager mConnectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
      if (mNetworkInfo != null) {
        return mNetworkInfo.isAvailable();
      }
    }
    return false;
  }

  /**
   * 打开网络设置界面
   */
  public static void openSetting(Activity activity) {
    Intent intent = new Intent("/");
    ComponentName cm =
        new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
    intent.setComponent(cm);
    intent.setAction("android.intent.action.VIEW");
    activity.startActivityForResult(intent, 0);
  }
}
