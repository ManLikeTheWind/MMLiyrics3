package com.dxiang.mring3.utils;

/**
 * 常量定义类
 * 
 * @author hezhejing
 * 
 */
public final class FusionCode {

	/** add by hezhejing start ****/
	/**
	 * 服务器处理成功
	 */
	public static final int NETWORK_SUCESSED = 0;

	/**
	 * 服务器处理失败
	 */
	public static final int NETWORK_SUCESSED2 = 1;

	public static final int NETWORK_OTHER_SUC = 1;

	/**
	 * 网络连接错误
	 */
	public static final int NETWORK_ERROR = 201;

	/**
	 * 网络连接超时
	 */
	public static final int NETWORK_TIMEOUT = 202;

	/**
	 * 网络忙
	 */
	public static final int NETWORK_BUSY = 203;

	/**
	 * 解析错误
	 */
	public static final int PARSER_ERROR = 204;

	/**
	 * 普通的消息连接请求
	 */
	public static final int CONNECT_TYPE_WEBSERVICE = 209;

	/**
	 * 文件连接请求
	 */
	public static final int CONNECT_TYPE_FILE = 210;

	/** 登录流程相关 **/
	public static final int REQUEST_GETPWDEVT = 301;
	public static final int REQUEST_VERCODEEVT = 302;
	public static final int REQUEST_SUBSCRIBEAPPEVT = 303;
	public static final int REQUEST_SENDPWDEVT = 304;

	/** 彩铃业务流程相关 **/
	public static final int REQUEST_UNSUBSCRIBEEVT = 401;
	public static final int REQUEST_USERSUBSEREVT = 402;

	/** 个人铃音库相关 **/
	public static final int REQUEST_QRYUSERTONEEVT = 501;
	public static final int REQUEST_SETTONEEVT = 502;
	public static final int REQUEST_QRYCALLEDTONESETEVT = 503;
	public static final int REQUEST_QRYCALLERTONESETEVT = 504;
	public static final int REQUEST_DELTONESETEVT = 505;
	public static final int REQUEST_DELTONEEVT = 506;
	public static final int REQUEST_GETTONELISTENADDREVT = 507;
	public static final int REQUEST_QRYGRP = 508;
	public static final int REQUEST_QRYGRPMEMBER = 509;
	public static final int REQUEST_ADDNEWGROUP = 510;
	public static final int REQUEST_ADDRINGTOGROUP = 511;
	public static final int REQUEST_DELETERINGTOGROUP = 512;
	public static final int REQUEST_QRYCALLINGGROUP = 513;// 查询主叫号码组;
	public static final int REQUEST_QRYCALLINGGROUPMEMBER = 514;// 查询主叫号码组的成员信息;
	public static final int REQUEST_MANAGERCALLINGGROUP = 515;// 管理主叫号码组
	public static final int REQUEST_EDITMANAGERCALLINGGROUP = 516;// 编辑主叫号码成员
	public static final int REQUEST_EDITRECORD = 517;// 操作记录
	public static final int REQUEST_VALIDA = 518;// 是否购买过
	public static final int REQUEST_QRYPLAYMODE=519;//查询播放模式（随机或者）
	public static final int REQUEST_SETPLAYMODE=520;
	public static final int REQUEST_SETPLAYMODE_DEFAULT=521;//设置默认
	public static final int REQUEST_SETPLAYMODE_RANDOM=522;//设置随机
	/**
	 * 注销请求
	 */
	public static final int REQUEST_UNSUBSCRIBE = 601;
	/**
	 * 激活或去激活
	 */
	public static final int REQUEST_ACTDEACTUSER = 602;

	/**
	 * 修改密码
	 */
	public static final int REQUEST_EDITPWD = 603;

	/**
	 * 意见反馈
	 */
	public static final int REQUEST_SUGGESTIONFEEDBACK = 604;

	/**
	 * 铃音列表相关
	 */
	public static final int REQUEST_CONTENTBUYTONE = 701;
	public static final int REQUEST_CONTENTBUYTONEFORAPP = 702;
	public static final int REQUEST_QRYRANKTONE_WEEK = 706;
	public static final int REQUEST_QRYRANKTONE_MONTH = 707;
	public static final int REQUEST_QRYRANKTONE_TOTAL = 708;
	public static final int REQUEST_QRYRECOMMENDTONE = 704;
	public static final int REQUEST_QRYTONEBYID = 705;
	public static final int REQUEST_GIFTTONE = 710;
	public static final int REQUEST_NEWRING = 711;// 最新分类


	/**
	 * 铃音分类
	 */

	public static final int REQUEST_QryToneTypeAPP = 1000;

	/**
	 * 铃音搜索
	 */
	public static final int REQUEST_QryToneBySinger = 1001;

	public static final int REQUEST_QryToneByName = 1002;

	public static final int REQUEST_QryToneByType = 1003;

	public static final int REQUEST_QRYCALLERTONEBYID = 1004;

	public static final int REQUEST_QRYCALLEDTONEBYID = 1005;

	/**
	 * Get banner image info
	 */

	public static final int REQUEST_GetImageInfo = 801;

	// Get advertisement image info

	public static final int REQUEST_GetadvertisementImageInfo = 802;

	// Get ranking list image info
	public static final int REQUEST_GetrankinglistImageInfo = 803;
	/**
	 * 首页排行榜
	 */

	public static final int REQUEST_Recommend = 810;

}
