package com.raven.data;

import com.baidu.mapapi.model.LatLng;
import com.raven.data.bean.MapPointBean;
import java.util.ArrayList;

/**
 * 测试数据源
 * Created by Raven on 2018/9/13 13:45.
 */
public class DataSource {

  //测试数据列表
  public static ArrayList<MapPointBean> getMapPointList() {
    ArrayList<MapPointBean> targetList = new ArrayList();
    targetList.add(new MapPointBean(1, "德玛西亚", "34.371276", "110.192514"));
    targetList.add(new MapPointBean(2, "诺克萨斯", "34.285658", "110.076381"));
    targetList.add(new MapPointBean(3, "暗影岛", "34.229072", "109.953187"));
    targetList.add(new MapPointBean(4, "比尔吉沃特", "34.275755", "109.940539"));
    targetList.add(new MapPointBean(5, "黑色玫瑰", "34.38227", "109.944923"));
    targetList.add(new MapPointBean(6, "战争学院", "34.168416", "110.086963"));
    return targetList;
  }

  //测试包含栅栏数据的列表
  public static ArrayList<MapPointBean> getMapPointListWithFenceData() {

    //第1个点的栅栏shape数据
    ArrayList<LatLng> shapeList1 = new ArrayList();
    shapeList1.add(new LatLng(34.336586, 109.074356));
    shapeList1.add(new LatLng(34.319057, 109.059696));
    shapeList1.add(new LatLng(34.299735, 109.066451));
    shapeList1.add(new LatLng(34.303791, 109.102696));
    shapeList1.add(new LatLng(34.325258, 109.101546));

    //第2个点的栅栏shape数据
    ArrayList<LatLng> shapeList2 = new ArrayList();
    shapeList2.add(new LatLng(34.577803, 109.458125));
    shapeList2.add(new LatLng(34.510723, 109.402358));
    shapeList2.add(new LatLng(34.457402, 109.415006));
    shapeList2.add(new LatLng(34.425964, 109.52194));
    shapeList2.add(new LatLng(34.470259, 109.647272));
    shapeList2.add(new LatLng(34.470259, 109.647272));
    shapeList2.add(new LatLng(34.579705, 109.649571));

    //第3个点的栅栏shape数据
    ArrayList<LatLng> shapeList3 = new ArrayList();
    shapeList3.add(new LatLng(34.22658, 110.165271));
    shapeList3.add(new LatLng(34.164473, 109.980148));
    shapeList3.add(new LatLng(34.099451, 110.039939));
    shapeList3.add(new LatLng(33.949133, 110.10318));
    shapeList3.add(new LatLng(33.965422, 110.253808));
    shapeList3.add(new LatLng(34.048737, 110.321648));
    shapeList3.add(new LatLng(34.202698, 110.165341));
    shapeList3.add(new LatLng(34.209386, 110.248058));
    shapeList3.add(new LatLng(34.257139, 110.169941));

    //第4个点的栅栏shape数据
    ArrayList<LatLng> shapeList4 = new ArrayList();
    shapeList4.add(new LatLng(34.928481, 109.881262));
    shapeList4.add(new LatLng(34.804326, 109.814537));
    shapeList4.add(new LatLng(34.659081, 110.025444));
    shapeList4.add(new LatLng(34.793892, 110.131926));
    shapeList4.add(new LatLng(34.855528, 110.027291));
    shapeList4.add(new LatLng(35.034479, 110.019242));

    ArrayList<MapPointBean> targetList = new ArrayList();
    targetList.add(new MapPointBean(1, "德玛西亚", "34.318103", "109.080836", shapeList1));
    targetList.add(new MapPointBean(2, "诺克萨斯", "34.503584", "109.519641", shapeList2));
    targetList.add(new MapPointBean(3, "暗影岛", "34.098494", "110.156072", shapeList3));
    targetList.add(new MapPointBean(4, "比尔吉沃特", "34.805275", "109.945653", shapeList4));
    return targetList;
  }
}
