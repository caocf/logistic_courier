package com.dekaisheng.courier.util.code;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
	private static final String ENCRYPT_KEY = "2xIV4bSDaVy5uvaahKUeTsoWKq1%PA96mzPc$X";

	/**
	 * 用MD5算法进行加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 * @return MD5加密后的结果
	 */
	public static String encodeMD5String(String str) {
		return encode(str, "MD5");
	}

	/**
	 * 用SHA算法进行加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 * @return SHA加密后的结果
	 */
	public static String encodeSHAString(String str) {
		return encode(str, "SHA");
	}

	/**
	 * 用base64算法进行加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 * @return base64加密后的结果
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeBase64String(String str)
			throws UnsupportedEncodingException {
		return Base64.encode(str.getBytes("UTF-8"), "UTF-8");
	}

	/**
	 * 用base64算法进行解密
	 * 
	 * @param str
	 *            需要解密的字符串
	 * @return base64解密后的结果
	 * @throws IOException
	 */
	public static String decodeBase64String(String str) throws IOException {
		return new String(Base64.decode(str.getBytes("UTF-8")), "UTF-8");
	}

	/**
	 * 指定加密类型 加密
	 * 
	 * @author lishenfei
	 * @param str
	 * @param method
	 * @return
	 */
	private static String encode(String str, String method) {
		MessageDigest md = null;
		String dstr = null;
		try {
			md = MessageDigest.getInstance(method);
			md.update(str.getBytes());
			dstr = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {

		}
		return dstr;
	}

	/**
	 * 加密算法
	 * @throws IOException
	 */
	public static String encode(String reqStrTmp) throws Exception {
		// 对加密字符串先base64,解决java与php中文字符加密不一致的问题
		String reqString =encodeBase64String(reqStrTmp).replace("\n","");
		String key = encodeMD5String(ENCRYPT_KEY);// MD5加密
		int keyLen = key.length();// 获取两次"key"MD5加密后的长度
		int strLen = reqString.length();
		int[] rndkey = new int[128];
		// 生成128个加密因子 （按密匙中单个字符的ASCII 值）
		for (int i = 0; i < rndkey.length; i++) {
			rndkey[i] = key.charAt(i % keyLen);
		}
		StringBuffer result = new StringBuffer();
		// 用字条串的每个字符ASCII值和加密因子里的（当前循环次数*字符串长度 求于 128） 按位异或 最后 ASCII 值返回字符
		for (int i = 0; i < strLen; i++) {
			result.append((char) ((reqString.charAt(i)) ^ rndkey[i * strLen
					% 128]));
		}
		// 截取新密匙的前16位并大写 + base64加密
		String kk=key.substring(0, 16).toUpperCase()
				+ encodeBase64String(result.toString()).replace("\n", "");
		return kk;
	}

	/**
	 * 解密算法
	 * @return
	 * @throws IOException
	 */
	public static String decode(String encodeResultTmp) throws Exception {
		String key = encodeMD5String(ENCRYPT_KEY);// MD5加密
		int keyLen = key.length();// 获取两次"key"MD5加密后的长度
		// 加密结果的截取后十六位
		String encodeResult = decodeBase64String(encodeResultTmp.substring(16));
		int strLen = encodeResult.length();
		int[] rndkey = new int[128];
		// 生成128个加密因子 （按密匙中单个字符的ASCII 值）
		for (int i = 0; i < rndkey.length; i++) {
			rndkey[i] = key.charAt(i % keyLen);
		}
		StringBuffer result = new StringBuffer();
		// 用字条串的每个字符ASCII值和加密因子里的（当前循环次数*字符串长度 求于 128） 按位异或 最后 ASCII 值返回字符
		for (int i = 0; i < strLen; i++) {
			result.append((char) ((encodeResult.charAt(i)) ^ rndkey[i * strLen
					% 128]));
		}
		return decodeBase64String(result.toString());
	}

	public static void main(String[] args)
	    {
	        try
	        {
	           String reqString ="mobile=13418463415&unit_id=22&detl_id=8837627&clinic_no=&pay_method=2&type=2";
	         String encodeResult = encode(reqString);
	         System.out.println("str："+reqString+"base64:"+encodeResult+"urlcode:"+URLEncoder.encode(encodeResult,"UTF-8"));
	         //String decodeResult = decode("2D23D7FB49C2C071UGMMWVNjTVxiYHBKfHBwBHxwbEp8cHABeFpjRVNsZ1ZTY2QJf158XWhzYwBQcgxAaHAFBH1weAN8XnwDeFl7Q1NjAEBrBQxFUE4FXVFzcwVqBgRcVnNdRmhwBUl4WmcFUXNgCX9TCA0=");
     
	          //System.out.println("字符串：" + reqString + "\n加密后：" + encodeResult + "\n解密后：" + decodeResult);
	          //  System.out.println("是否解密成功：" + decodeResult);
	         System.out.println(URLEncoder.encode("=","UTF-8"));
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
}
