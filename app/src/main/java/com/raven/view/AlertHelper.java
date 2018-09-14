package com.raven.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import com.raven.R;

/**
 * AlertDialog 帮助类
 * Created by Raven on 2018/3/6 19:53.
 */
public class AlertHelper {

  /**
   * 简单的alert
   */
  public static void showSimpleAlertWithCallback(Context mContext, String title,
      AlertDialog.OnClickListener okBack) {
    new AlertDialog.Builder(mContext).setTitle(title)
        .setCancelable(true)
        .setNegativeButton(R.string.cancel, (dialog, which) -> {
        })
        .setPositiveButton(R.string.ok, okBack)
        .create()
        .show();
  }

  /**
   * 简单的alert
   * 只包含一个按钮
   */
  public static void showSimpleAlertWithCallback2(Context mContext, String title,
      AlertDialog.OnClickListener okBack) {
    new AlertDialog.Builder(mContext).setTitle(title).setCancelable(false)
        //隐藏"取消"按钮
        //.setNegativeButton(R.string.cancel, (dialog, which) -> {
        //})
        .setPositiveButton(R.string.ok, okBack).create().show();
  }
}
