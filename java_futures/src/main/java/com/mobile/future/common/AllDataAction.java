package com.mobile.future.common;

/**
 * 这个类是专门为各个业务区间提供数据捕获后的标准化数组的一个类
 */
public class AllDataAction {
	
	/*
	 * 该方法是将获得的交易数据变成一个可用的字符数组
	 */
	public static String[] getString() {
		    //获取全部数据
		    //老接口地址https://218.249.27.78:16025/tradeweb/hq/hqV.jsp（已废弃）
		   //新接口地址https://218.249.27.78:16026/tradeweb_en/hq/hqV.jsp
			String str=TransactionDataAction.getHttps("https://218.249.27.78:16026/tradeweb_en/hq/hqV.jsp");
			//将所有空格回车等全部换为“,”，以便处理字符串
			String documentTxt = str.replaceAll("[\\t\\n\\r]", ",");
			String[] strs = documentTxt.split(",");
			String sts = "";
			// 该for循环的作用是将多余的“，”去掉，使字符串变成一个只含数据的串
			for (String st : strs) {
				if (!st.equals("")) {
					sts += st + ",";
				}
			}
			strs = sts.split(",");
			//返回一个数组
			return strs;
		}
}
