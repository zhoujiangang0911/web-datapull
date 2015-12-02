package com.mobile.future.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.mobile.future.action.data.StatisticsAction;
import com.mobile.future.util.https.CustomX509TrustManager;
import com.mobile.future.util.https.TrustAnyHostnameVerifier;

public class TransactionDataAction {
	
	
	
	public static String getHttps(String url){
		HttpsURLConnection urlCon = null;
		SSLContext sslContext = null;
		StringBuffer sb = new StringBuffer("");
        try {
            sslContext = SSLContext.getInstance("SSL"); //或SSL/TLS
            X509TrustManager[] xtmArray = new X509TrustManager[] {new CustomX509TrustManager()};
            sslContext.init(null, xtmArray, new java.security.SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
        	urlCon.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        urlCon.setDefaultHostnameVerifier(new TrustAnyHostnameVerifier());
        try {
            urlCon = (HttpsURLConnection)(new URL(url)).openConnection();
            //连接主机的超时时间
            urlCon.setConnectTimeout(5000); 
            //从主机读取数据的超时时间
            urlCon.setReadTimeout(50000);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
//            urlCon.setRequestProperty("Charset", "UTF-8"); 
            urlCon.setRequestProperty("Content-Length", "1024");
            urlCon.setUseCaches(false);
            urlCon.setDoInput(true);
//            urlCon.getOutputStream().write("request content".getBytes("utf-8"));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            InputStreamReader is =new InputStreamReader(urlCon.getInputStream(),"gbk");
            BufferedReader in = new BufferedReader(is);
            String str = "";
    		while ((str = in.readLine()) != null) {
    			sb.append(str).append("\n");
    		}
    		in.close();
    		is.close();
    		//这行代码给StatisticsAction类中的linkOrNot赋值，是为了行情接口出现问题的时候，用户依然可以拿到之前的行情数据
    		StatisticsAction.linkOrNot="True";
        } catch (Exception e) {
        	//这行代码给StatisticsAction类中的linkOrNot赋值，是为了行情接口出现问题的时候，用户依然可以拿到之前的行情数据
        	StatisticsAction.linkOrNot="Failse";
        }
        return sb.toString();
	}

}