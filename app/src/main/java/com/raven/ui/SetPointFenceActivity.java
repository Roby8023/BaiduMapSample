package com.raven.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.raven.R;
import com.raven.data.bean.MapPointBean;
import com.raven.util.NetUtils;
import com.raven.util.ToastUtil;
import com.raven.util.UIUtil;
import com.raven.view.AlertHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置对应点围栏信息
 * Created by Raven on 2018年9月14日10:56:53
 */
public class SetPointFenceActivity extends AppCompatActivity {
  // 地图相关
  private MapView mMapView;
  private BaiduMap mBaiduMap;
  private int screenWidth, screenHeight;

  private List<Marker> markerList = new ArrayList<>();
  private BitmapDescriptor iconBitmap;
  //传递过来要修改的位置信息
  private MapPointBean locationBean;

  //已添加的点的集合
  private List<LatLng> selPointList = new ArrayList<>();
  private Overlay shapeOverlay;
  private MapStatusUpdate mMapStatusUpdate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_point_fence);

    initViews();
  }

  private void initViews() {
    //注意取的时候的写法
    //locationBean = (MapPointBean) getIntent().getSerializableExtra("locationBean");
    locationBean = (MapPointBean) getIntent().getParcelableExtra("locationBean");
    //如果为空则直接结束页面
    if (locationBean == null) {
      AlertHelper.showSimpleAlertWithCallback2(this, "错误数据", (dialog, which) -> finish());
    }
    selPointList = locationBean.getShapeList();
    // 初始化地图
    mMapView = findViewById(R.id.bmapView);
    mBaiduMap = mMapView.getMap();
    // UI初始化
    Button btSetResult = findViewById(R.id.btSetResult);
    Button btAddPoint = findViewById(R.id.btAddPoint);
    Button btSave = findViewById(R.id.btSave);
    btSave.setOnClickListener(v -> savePointList());
    //显示绘图结果
    btSetResult.setOnClickListener(view -> {
      if (selPointList.size() > 2) {
        showShape();
      } else {
        //百度Map API设置栅栏必须至少显示3个点, 否则会报错
        ToastUtil.showShort(SetPointFenceActivity.this, "请选择至少3个点");
      }
    });
    btAddPoint.setOnClickListener(view -> cleanAllPoints());

    // 界面加载时添加绘制图层
    //addCustomElementsDemo();
    if (selPointList != null && !selPointList.isEmpty()) {
      showLocationsOnMap();
      showShape();
    }

    /** 地图的click点击事件 */
    mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
      /**
       *  这里的setOnMapClickListener如果不注意的话有个比较坑的地方,就是在实现的两个方法onMapClick()和onMapPoiClick()中,
       *  默认如果点击地图的Poi点的话触发了和onMapPoiClick()方法, 我试验的是设置返回值true和false无区别, 都不会执行继续
       *  执行自己的逻辑, 也考虑的想屏蔽该方法, 但是搜索无结果, 无奈, 就取巧在该方法中调用了onMapClick()方法即可。
       */
      @Override
      public void onMapClick(LatLng point) {
        if (selPointList == null) {
          selPointList = new ArrayList<>();
        }
        selPointList.add(new LatLng(point.latitude, point.longitude));
        // 充气布局
        View view = View.inflate(SetPointFenceActivity.this, R.layout.map_fence_pin, null);
        // 构建BitmapDescriptor
        iconBitmap = BitmapDescriptorFactory.fromView(view);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
            //.icon(bdMarker)
            .position(point).animateType(MarkerOptions.MarkerAnimateType.grow)
            //.zIndex(10)
            .period(10).icon(iconBitmap);
        //在地图上添加Marker，并显示
        Marker mMarkerNew = (Marker) mBaiduMap.addOverlay(option);

        markerList.add(mMarkerNew);
        //locationList.add(new LatLng(point.latitude, point.longitude));
      }

      @Override
      public boolean onMapPoiClick(MapPoi poi) {
        //调用上面的方法即可
        onMapClick(poi.getPosition());
        return true; //返回值true或false没发现异常
      }
    });

    /** 地图的Marker点击事件, 点击后删除 */
    mBaiduMap.setOnMarkerClickListener(marker -> {
      //int i = 0;
      for (int i = 0; i < markerList.size(); i++) {
        if (marker == markerList.get(i)) {
          selPointList.remove(i);
        }
      }
      markerList.remove(marker);
      marker.remove();
      return true;
    });

    screenWidth = getWindowManager().getDefaultDisplay().getWidth();
    screenHeight = getWindowManager().getDefaultDisplay().getHeight();

    movePoint2Center(new LatLng(Double.parseDouble(locationBean.getLatitude()),
        Double.parseDouble(locationBean.getLongitude())));
  }

  public static void startTo(Activity context, int requestCode, MapPointBean locationBean) {
    Intent intent = new Intent();
    intent.setClass(context, SetPointFenceActivity.class);
    intent.putExtra("locationBean", locationBean);
    intent.putExtra("RequestCode", requestCode);
    context.startActivityForResult(intent, requestCode);
  }

  //提交保存
  private void savePointList() {
    if (selPointList.size() > 2) {
      if (!NetUtils.isNetworkConnected(this)) {
        noNetWork();
        return;
      }
      //TODO 提交保存逻辑
      ToastUtil.showShort(SetPointFenceActivity.this, "todo 提交保存栅栏");
    } else {
      ToastUtil.showShort(SetPointFenceActivity.this, "请选择至少3个点");
    }
  }

  private void cleanAllPoints() {
    mMapView.getMap().clear();
    markerList.clear();
    selPointList.clear();
    //ToastUtil.showShort(SetPointFenceActivity.this,
    //    "clear后size:" + markerList.size() + " - " + selPointList.size());
  }

  private void showShape() {
    if (shapeOverlay != null) {
      shapeOverlay.remove();
    }
    OverlayOptions ooPolygon = new PolygonOptions().points(selPointList)
        .stroke(new Stroke(5, 0xAAff00ff))
        .fillColor(0xAAf5ec32);
    shapeOverlay = mBaiduMap.addOverlay(ooPolygon);
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
  }

  @Override
  protected void onDestroy() {
    mMapView.onDestroy();
    if (iconBitmap != null) {
      iconBitmap.recycle();
    }
    super.onDestroy();
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

  private void showLocationsOnMap() {
    if (markerList != null) {
      markerList.clear();
    }

    View viewPin;
    BitmapDescriptor bitmap;
    OverlayOptions option;
    Marker mMarkerB;
    for (int i = 0; i < selPointList.size(); i++) {
      LatLng latLng = new LatLng(selPointList.get(i).latitude, selPointList.get(i).longitude);

      /** 可区分点类别做不同显示 */
      // 充气布局
      viewPin = View.inflate(SetPointFenceActivity.this, R.layout.map_fence_pin, null);
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

      markerList.add(mMarkerB);
    }
  }

  public void noNetWork() {
    ToastUtil.showShort(this, "网络不可用");
  }
}
