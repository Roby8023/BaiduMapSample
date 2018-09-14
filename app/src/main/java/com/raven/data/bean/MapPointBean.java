package com.raven.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.LatLng;
import java.util.ArrayList;

/**
 * Created by Raven on 2018年9月13日14:13:54
 *
 * Tips:
 * 这里实现了Parcelable接口，是因为该类中包含了百度SDK中的LatLng类，因为LatLng实现了Parcelable，
 * 在栅栏设置模块中有数据这个类的数据传递, 所以都实现了Parcelable接口，用户若想实现Serializable接口的
 * 话可自定义一个LatLng的接口然后再都实现成Serializable即可。
 */

public class MapPointBean implements Parcelable {

  private int locationID;
  private String locationName;
  private String latitude;
  private String longitude;
  private ArrayList<LatLng> shapeList;

  public MapPointBean() {
  }

  protected MapPointBean(Parcel in) {
    locationID = in.readInt();
    locationName = in.readString();
    latitude = in.readString();
    longitude = in.readString();
    shapeList = in.createTypedArrayList(LatLng.CREATOR);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(locationID);
    dest.writeString(locationName);
    dest.writeString(latitude);
    dest.writeString(longitude);
    dest.writeTypedList(shapeList);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<MapPointBean> CREATOR = new Creator<MapPointBean>() {
    @Override
    public MapPointBean createFromParcel(Parcel in) {
      return new MapPointBean(in);
    }

    @Override
    public MapPointBean[] newArray(int size) {
      return new MapPointBean[size];
    }
  };

  public ArrayList<LatLng> getShapeList() {
    return shapeList;
  }

  public void setShapeList(ArrayList<LatLng> shapeList) {
    this.shapeList = shapeList;
  }

  public MapPointBean(int locationID, String locationName, String latitude, String longitude) {
    this.locationID = locationID;
    this.locationName = locationName;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public MapPointBean(int locationID, String locationName, String latitude, String longitude,
      ArrayList<LatLng> shapeList) {
    this.locationID = locationID;
    this.locationName = locationName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.shapeList = shapeList;
  }

  public int getLocationID() {
    return locationID;
  }

  public void setLocationID(int locationID) {
    this.locationID = locationID;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  //public static class MyLatLng implements Serializable {
  //  private String latitude;
  //  private String longitude;
  //  private int shapeId;
  //  private int sort;
  //
  //  public String getLatitude() {
  //    return latitude;
  //  }
  //
  //  public void setLatitude(String latitude) {
  //    this.latitude = latitude;
  //  }
  //
  //  public String getLongitude() {
  //    return longitude;
  //  }
  //
  //  public void setLongitude(String longitude) {
  //    this.longitude = longitude;
  //  }
  //
  //  public int getShapeId() {
  //    return shapeId;
  //  }
  //
  //  public void setShapeId(int shapeId) {
  //    this.shapeId = shapeId;
  //  }
  //
  //  public int getSort() {
  //    return sort;
  //  }
  //
  //  public void setSort(int sort) {
  //    this.sort = sort;
  //  }
  //}
}

