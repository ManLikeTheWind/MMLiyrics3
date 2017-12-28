package com.dxiang.mring3.utils;

public class UserVariable {

	// 用户号码，部分用户相关的请求需要，在登录成功后赋值
	public static String CALLNUMBER = "";

	// 用户密码，登录成功后赋值
	public static String USERPWD = "";

	// 0-Not exist
	// 1-activated called RBT user
	// 2-deactivated called RBT user
	// 3-Force deactivated called RBT user
	public static String STATUSCALLED = "0";
	public static String STATUSCALLING = "0";

	/**
	 * 标示登录状态
	 */
	public static boolean LOGINED = false;
}
