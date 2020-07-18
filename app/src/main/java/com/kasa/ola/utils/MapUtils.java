package com.kasa.ola.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class MapUtils {
    public List<String> mapsList (){
        List<String> maps = new ArrayList<>();
        maps.add("com.baidu.BaiduMap");
        maps.add("com.autonavi.minimap");
        maps.add("com.tencent.map");
        return maps;
    }
    // 检索筛选后返回
    public  List<String> hasMap (Context context){
        List<String> mapsList = mapsList();
        List<String> backList = new ArrayList<>();
        for (int i = 0; i < mapsList.size(); i++) {
            boolean avilible = isAvilible(context, mapsList.get(i));
            if (avilible){
                backList.add(mapsList.get(i));
            }
        }
        return backList;
    }
    // 检索地图软件
    public static boolean isAvilible(Context context, String packageName){
//获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
//获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
//用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
//从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
//判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
    // 百度地图
    public static void toBaidu(Context context, String longitude,String latitude){

        Intent intent= new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + latitude + "," + longitude));
        context.startActivity(intent);
    }
    // 高德地图
    public static void toGaodeNavi(Context context, String longitude,String latitude){
        Intent intent= new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat="+ latitude +"&dlon="+ longitude+"&dname=目的地&dev=0&t=2"));
        context.startActivity(intent);
    }
    // 腾讯地图
    public static void toTencent(Context context, String longitude,String latitude){
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=目的地&tocoord=" + latitude + "," + longitude + "&policy=0&referer=appName"));
        context.startActivity(intent);
    }
}
