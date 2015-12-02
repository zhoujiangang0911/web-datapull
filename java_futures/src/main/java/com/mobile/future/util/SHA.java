package com.mobile.future.util;

import java.security.MessageDigest;

public class SHA {
	public static final String KEY_SHA = "SHA";  
	 /** 
     * SHA加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptSHA(byte[] data) throws Exception {  
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);  
        sha.update(data);  
        return sha.digest();  
    }

}  
