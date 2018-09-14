package com.raven.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.raven.R;
import com.raven.data.DataSource;
import com.raven.data.bean.MapPointBean;
import com.raven.util.NetUtils;
import com.raven.util.ToastUtil;
import com.raven.util.UIUtil;
import com.raven.view.progress.MyLoadingDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raven on 2018年9月13日10:49:56
 *
 * Tips 页面包含功能:
 * 1.多个带标签的Marker展示
 * 2.点击后直接弹出菜单操作
 */
public class MarkerListActivity extends AppCompatActivity implements SensorEventListener {
  private MapView mMapView;
  private BaiduMap mBaiduMap;
  private boolean isMove2Center = true; // 是否移动到中心位置
  private boolean isFirst = true; // 判断点击了定位自身位置后是否请求map点列表
  private int screenWidth, screenHeight;
  private MyLoadingDialog myLoadingDialog;

  // 定位相关
  private LocationClient mLocClient;
  public MyLocationListener myListener = new MyLocationListener();
  private SensorManager mSensorManager;
  private Double lastX = 0.0;
  private int mCurrentDirection = 0;
  private double mCurrentLat = 0.0;
  private double mCurrentLon = 0.0;
  private float mCurrentAccracy;

  private MyLocationData locData;
  private ArrayList<MapPointBean> locationList = new ArrayList<>();
  private List<Marker> markerList = new ArrayList<>();
  //地图点的图标
  private BitmapDescriptor bdMarker =
      BitmapDescriptorFactory.fromResource(R.drawable.location_icon);
  //移动地图所用类
  private MapStatusUpdate mMapStatusUpdate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_marker_list);
    mMapView = findViewById(R.id.bmapView);
    findViewById(R.id.iv_map_my_position).setOnClickListener(v -> {
      isMove2Center = true;
      //定位按钮
      getMyPosition();
    });
    myLoadingDialog = new MyLoadingDialog(this);
    screenWidth = getWindowManager().getDefaultDisplay().getWidth();
    screenHeight = getWindowManager().getDefaultDisplay().getHeight();
    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务

    // 地图初始化
    mBaiduMap = mMapView.getMap();
    // 开启定位图层
    mBaiduMap.setMyLocationEnabled(true);
    /** 点击Marker */
    mBaiduMap.setOnMarkerClickListener((Marker marker) -> {
      Marker bean;
      for (int i = 0; i < markerList.size(); i++) {
        bean = markerList.get(i);
        /** 判断点击的Marker是Marker中的哪一个 */
        if (bean == marker) {
          // TODO: 2018/9/13  以下可根据自己的逻辑去作相对应的逻辑
          ToastUtil.showShort(MarkerListActivity.this, locationList.get(i).getLocationName());
          new AlertDialog.Builder(MarkerListActivity.this).setTitle(
              locationList.get(i).getLocationName())
              //.setMessage("")
              .setCancelable(true).setNegativeButton(R.string.cancel, (dialog, which) -> {
          }).setPositiveButton(R.string.ok, (dialog, which) -> {
          }).create().show();
          break;
        }
      }
      return true;
    });

    //判断是否为android6.0系统版本，如果是，需要动态添加权限
    if (Build.VERSION.SDK_INT >= 23) {
      showPermissionsInfo(false);
    } else {
      //新版本的逻辑变为在没有权限的时候就去获取全国的点, 但是低版本不涉及权限问题, 所以直接定位
      // 定位初始化
      getMyPosition();
    }
  }

  private void getMyPosition() {
    if (!NetUtils.isNetworkConnected(MarkerListActivity.this)) {
      noNetWork();
      return;
    }
    myLoadingDialog.show();

    mLocClient = new LocationClient(this);
    mLocClient.registerLocationListener(myListener);
    LocationClientOption option = new LocationClientOption();
    option.setOpenGps(true); // 打开gps
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
    option.setCoorType("bd09ll"); // 设置坐标类型
    //option.setScanSpan(5000);
    option.setIsNeedAddress(true); // 获取地址
    mLocClient.setLocOption(option);
    mLocClient.start();
  }

  /**
   * 获取权限的提示
   */
  private void showPermissionsInfo(boolean showToastFlag) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {

      //当无权限时: 弹权限框
      showPermissionSettingDialog();
      if (showToastFlag) {
        ToastUtil.showShort(this, "权限拒绝 T_T");
      }
    } else {
      getMyPosition();
    }
  }

  /**
   * 权限设置的提示框
   */
  private void showPermissionSettingDialog() {
    new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.loc_title))
        .setMessage(getResources().getString(R.string.loc_msg))
        .setCancelable(false)
        .setNegativeButton(R.string.cancel, (dialog, which) -> {
          ToastUtil.showShort(this, getResources().getString(R.string.loc_negative));
        })
        .setPositiveButton(R.string.ok, (dialog, which) -> {
          Uri packageURI = Uri.parse("package:" + getPackageName());
          Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
          //this.startActivityForResult(intent, 777);
          this.startActivityForResult(intent, Activity.RESULT_FIRST_USER);
        })
        .create()
        .show();
  }

  /** 移动地图到指定点为中心 */
  private void movePoint2Center(LatLng latLng) {
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(latLng);
    //移动地图视角到指定点
    mMapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
        /**在这里获取到的地图宽高都为0, 所以只能用屏幕宽高来处理*/
        screenWidth - UIUtil.dip2px(this, 120), screenHeight - UIUtil.dip2px(this, 150));
    mBaiduMap.animateMapStatus(mMapStatusUpdate);
  }

  public void noNetWork() {
    ToastUtil.showShort(this, "网络不可用");
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    double x = sensorEvent.values[SensorManager.DATA_X];
    if (Math.abs(x - lastX) > 1.0) {
      mCurrentDirection = (int) x;
      locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
          // 此处设置开发者获取到的方向信息，顺时针0-360
          .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
      mBaiduMap.setMyLocationData(locData);
    }
    lastX = x;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  /**
   * 定位SDK监听函数
   */
  public class MyLocationListener implements BDLocationListener {
    @Override
    public void onReceiveLocation(BDLocation location) {
      // map view 销毁后不在处理新接收的位置
      if (location == null || mMapView == null) {
        return;
      }
      mCurrentLat = location.getLatitude();
      mCurrentLon = location.getLongitude();
      mCurrentAccracy = location.getRadius();
      locData = new MyLocationData.Builder().accuracy(location.getRadius())
          // 此处设置开发者获取到的方向信息，顺时针0-360
          .direction(mCurrentDirection)
          .latitude(location.getLatitude())
          .longitude(location.getLongitude())
          .build();
      mBaiduMap.setMyLocationData(locData);
      if (isMove2Center) {
        isMove2Center = false;
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(17.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
      }
      myLoadingDialog.dismiss();
      if (isFirst) {
        //如果是第一次进页面则为true, 去请求坐标点列表, 之后为false不再请求
        isFirst = false;
        getMapPointList();
      }
    }
  }

  //获取假数据列表, 在地图上显示Marker点
  private void getMapPointList() {
    locationList.clear();
    myLoadingDialog.dismiss();
    List<MapPointBean> dataList = DataSource.getMapPointList(); //获取数据源
    if (dataList != null && !dataList.isEmpty()) {
      locationList.addAll(dataList);
      //显示地图点坐标
      showLocationsOnMap();
    }
  }

  //显示地图点坐标
  private void showLocationsOnMap() {
    if (markerList != null) {
      markerList.clear();
    }

    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    View viewPin;
    LatLng latLng;
    TextView nameView;
    BitmapDescriptor bitmap;
    OverlayOptions option;
    Marker mMarkerB;
    for (int i = 0; i < locationList.size(); i++) {
      latLng = new LatLng(Double.parseDouble(locationList.get(i).getLatitude()),
          Double.parseDouble(locationList.get(i).getLongitude()));

      /** 可区分点类别做不同显示 */
      // 充气布局
      viewPin = View.inflate(MarkerListActivity.this, R.layout.map_scenic_maker, null);
      // 填充数据
      nameView = viewPin.findViewById(R.id.maker_name);
      nameView.setText(locationList.get(i).getLocationName());
      // 构建BitmapDescriptor
      bitmap = BitmapDescriptorFactory.fromView(viewPin);
      // 构建MarkerOption，用于在地图上添加Marker
      option = new MarkerOptions()
          //.icon(bdMarker)
          .position(latLng).animateType(MarkerOptions.MarkerAnimateType.grow)
          //.zIndex(10)
          .period(10).icon(bitmap);
      //在地图上添加Marker，并显示
      mMarkerB = (Marker) mBaiduMap.addOverlay(option);

      //Marker mMarkerB = (Marker) (mBaiduMap.addOverlay(markerOptionsB));
      markerList.add(mMarkerB);
      builder.include(latLng);

      bitmap.recycle();
      //显示位置栅栏 - shapeList
      //showShape(locationList.get(i).getShapeList());
    }

    //移动地图视角, 显示所有的点
    mMapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
        mMapView.getWidth() - UIUtil.dip2px(this, 120),
        mMapView.getHeight() - UIUtil.dip2px(this, 150));
    mBaiduMap.animateMapStatus(mMapStatusUpdate);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //从权限页面设置回来后的操作
    if (requestCode == Activity.RESULT_FIRST_USER) {
      showPermissionsInfo(true);
    }
  }

  @Override
  protected void onPause() {
    mMapView.onPause();
    super.onPause();
  }

  @Override
  protected void onResume() {
    mMapView.onResume();
    super.onResume();
    //为系统的方向传感器注册监听器
    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        SensorManager.SENSOR_DELAY_UI);
  }

  @Override
  protected void onStop() {
    //取消注册传感器监听
    mSensorManager.unregisterListener(this);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    // 退出时销毁定位
    if (mLocClient != null) {
      mLocClient.stop();
    }
    // 关闭定位图层
    if (mBaiduMap != null) {
      mBaiduMap.setMyLocationEnabled(false);
    }
    mMapView.onDestroy();
    mMapView = null;
    bdMarker.recycle();
    super.onDestroy();
  }
}
