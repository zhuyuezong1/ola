package com.kasa.ola.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;


import com.kasa.ola.App;
import com.kasa.ola.manager.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * SharePreference 工具类
 * 
 * @ClassName: SPUtils
 * 
 * 
 */

public class SPUtils {
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putInt(key, value).apply();
	}

	public static void putFloat(Context context, String key, Float value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putFloat(key, value).apply();
	}

	public static void putLong(Context context, String key, Long value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putLong(key, value).apply();
	}

	public static void putBoolean(Context context, String key, Boolean value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putBoolean(key, value).apply();
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putString(key, value).apply();
	}

	public static void putStringSet(Context context, String key,
                                    Set<String> value) {
		SharedPreferences sp = getSp(context);
		sp.edit().putStringSet(key, value).apply();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getString(key, "");
	}

	public static String getString(Context context, String key, String defValue) {
		SharedPreferences sp = getSp(context);
		return sp.getString(key, defValue);
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getInt(key, 0);
	}

	public static int getInt(Context context, String key, Integer defValue) {
		SharedPreferences sp = getSp(context);
		return sp.getInt(key, defValue);
	}

	public static Float getFloat(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getFloat(key, 0);
	}

	public static Float getFloat(Context context, String key, Float defValue) {
		SharedPreferences sp = getSp(context);
		return sp.getFloat(key, defValue);
	}

	public static Boolean getBoolean(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getBoolean(key, false);
	}

	public static Boolean getBoolean(Context context, String key,
                                     Boolean defValue) {
		SharedPreferences sp = getSp(context);
		return sp.getBoolean(key, defValue);
	}

	public static Long getLong(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getLong(key, 0);
	}

	public static Long getLong(Context context, String key, Long defValue) {
		SharedPreferences sp = getSp(context);
		return sp.getLong(key, defValue);
	}

	public static Set<String> getStringSet(Context context, String key) {
		SharedPreferences sp = getSp(context);
		return sp.getStringSet(key, null);
	}
	public static void clearSP(Context context){
		SharedPreferences sp = getSp(context);
		Editor edit = sp.edit();
		edit.clear();
		edit.apply();
	}
	public static void removeToken(Context context, String key){
		SharedPreferences sp = getSp(context);
		Editor edit = sp.edit();
		edit.remove(Const.TOKEN);
		edit.remove(Const.USER_ID);
		edit.apply();
	}


	/**
	 * 获取SharePreferences对象
	 * @Title: getSp
	 * @param context 上下文
	 * @return SharedPreferences 返回类型
	 */
	private static SharedPreferences getSp(Context context) {
		return context.getSharedPreferences(Const.SP_NAME, Context.MODE_PRIVATE);
	}
	// 保存搜索记录
	public static void saveSearchHistory(String inputText,String spTag,String historySpTag) {
		SharedPreferences sp = App.getApp().getSharedPreferences(spTag, Context.MODE_PRIVATE);
		if (TextUtils.isEmpty(inputText)) {
			return;
		}
		String longHistory = sp.getString(historySpTag, "");  //获取之前保存的历史记录
		String[] tmpHistory = longHistory.split(","); //逗号截取 保存在数组中
		List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory)); //将改数组转换成ArrayList
		SharedPreferences.Editor editor = sp.edit();
		if (historyList.size() > 0) {
			//1.移除之前重复添加的元素
			for (int i = 0; i < historyList.size(); i++) {
				if (inputText.equals(historyList.get(i))) {
					historyList.remove(i);
					break;
				}
			}
			historyList.add(0, inputText); //将新输入的文字添加集合的第0位也就是最前面(2.倒序)
			if (historyList.size() > 8) {
				historyList.remove(historyList.size() - 1); //3.最多保存8条搜索记录 删除最早搜索的那一项
			}
			//逗号拼接
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < historyList.size(); i++) {
				sb.append(historyList.get(i) + ",");
			}
			//保存到sp
			editor.putString(historySpTag, sb.toString());
			editor.commit();
		} else {
			//之前未添加过
			editor.putString(historySpTag, inputText + ",");
			editor.commit();
		}
	}
	/**
	 * 清除搜索记录
	 */
	public static void cleanHistory(String spTag) {
		SharedPreferences sp = App.getApp().getSharedPreferences(spTag, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	//获取搜索记录
	public static List<String> getSearchHistory(String spTag,String historySpTag){
		SharedPreferences sp = App.getApp().getSharedPreferences(spTag, Context.MODE_PRIVATE);
		String longHistory =sp.getString(historySpTag, "");
		String[] tmpHistory = longHistory.split(","); //split后长度为1有一个空串对象
		List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
		if (historyList.size() == 1 && historyList.get(0).equals("")) { //如果没有搜索记录，split之后第0位是个空串的情况下
			historyList.clear();  //清空集合，这个很关键
		}
		return historyList;
	}
}
