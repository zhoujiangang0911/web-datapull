package com.mobile.future.util.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class TrustAnyHostnameVerifier implements HostnameVerifier  {
	public boolean verify(String hostname, SSLSession session) {
        System.out.println(hostname + " vs. " + session.getPeerHost());
        return true;
    }
}
