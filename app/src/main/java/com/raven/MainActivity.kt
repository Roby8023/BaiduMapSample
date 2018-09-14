package com.raven

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.raven.ui.AddMarkerActivity
import com.raven.ui.FenceOperationActivity
import com.raven.ui.MarkerListActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * MainPage
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //显示Marker列表，并点击操作
    btMarkList.setOnClickListener {
      startActivity(Intent(this, MarkerListActivity::class.java))
    }

    //长按地图添加Marker并操作
    btAddMarker.setOnClickListener {
      startActivity(Intent(this, AddMarkerActivity::class.java))
    }

    //栅栏的显示和操作
    btFenceList.setOnClickListener {
      startActivity(Intent(this, FenceOperationActivity::class.java))
    }
  }
}
