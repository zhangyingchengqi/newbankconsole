package com.yc.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ���ܹ�����
 * md5���ܳ����ĳ�����32λ
 * sha���ܳ����ĳ�����40λ
 */
public class Encrypt {

	/**
	 * ����
	 */
	public static void main(String[] args) {
		// md5���ܲ���
		String md5_1 = md5("ab");   // 32λ
		String md5_2 = md5("ba");
		
		System.out.println(md5_1 + "\n" + md5_2);
		// sha���ܲ���
		String sha_1 = sha("a");   //40λ
		String sha_2 = sha("1");
		System.out.println(sha_1 + "\n" + sha_2);
		
		//�ƽ�:  �ʺ��
		//  0cc175b9c0f1b6a831c399e269772661     a
		//  c4ca4238a0b923820dcc509a6f75849b     1
		//����: ����. �ò�ͬ���㷨����n��
		System.out.println(  md5(md5(md5("a")))  );
		System.out.println(  md5(sha(md5("a")))  );
		
		//Ӧ�ü�ֵ :  ��Ϊʹ��md5��sha���ܵõ��Ľ���ǲ�һ��(ɢ��Ч��)�����Կ�����������ָ��. ����ʶ���ļ�. 
		//  baidu����: idea.exe    �����ظ�. 
		//       �ļ�-> ����ָ��, ���ļ���ָ��
		
		
		
		

	}

	/**
	 * ����
	 * 
	 * @param inputText
	 * @return
	 */
	public static String e(String inputText) {
		return md5(inputText);
	}

	/**
	 * ����:  ���μ��ܣ�Ӧ���ƽⲻ���˰ɣ�
	 * @param inputText
	 * @return
	 */
	public static String md5AndSha(String inputText) {
		return sha(md5(inputText));
	}

	/**
	 * md5����
	 * @param inputText
	 * @return
	 */
	public static String md5(String inputText) {
		return encrypt(inputText, "md5");
	}

	/**
	 * sha����
	 * @param inputText
	 * @return
	 */
	public static String sha(String inputText) {
		return encrypt(inputText, "sha-1");
	}

	/**
	 * md5����sha-1����
	 * 
	 * @param inputText
	 *            Ҫ���ܵ�����
	 * @param algorithmName
	 *            �����㷨���ƣ�md5����sha-1�������ִ�Сд
	 * @return
	 */
	private static String encrypt(String inputText, String algorithmName) {
		if (inputText == null || "".equals(inputText.trim())) {
			throw new IllegalArgumentException("������Ҫ���ܵ�����");
		}
		if (algorithmName == null || "".equals(algorithmName.trim())) {
			algorithmName = "md5";
		}
		String encryptText = null;
		try {
			MessageDigest m = MessageDigest.getInstance(algorithmName);
			m.update(inputText.getBytes("UTF8"));
			byte s[] = m.digest();
			// m.digest(inputText.getBytes("UTF8"));
			return hex(s);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encryptText;
	}

	/**
	 * ����ʮ�������ַ���
	 * @param arr
	 * @return
	 */
	private static String hex(byte[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

}
