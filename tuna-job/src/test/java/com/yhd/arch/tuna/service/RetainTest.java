package com.yhd.arch.tuna.service;

/**
 * Created by root on 10/24/16.
 */
import com.yhd.arch.tuna.linktree.dto.NodeInfo;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RetainTest {

	static MessageDigest  md5=null;
	static MessageDigest  SHA=null;
	public static void main(String args[])
	{
		String hello="hello";

		ArrayList<String> list=null;
		ArrayList<ArrayList<String>> lists=new ArrayList<ArrayList<String>>();
		Set<ArrayList<String>> set=new HashSet<ArrayList<String>>();
		for(int i=0;i<3;i++){
			list=new ArrayList<String>();
			list.add("1");
			set.add(list);
			lists.add(list);
		}
		System.out.println(set+"  "+lists);
		System.out.println(md5(hello));
		System.out.println(md5(hello));
		System.out.println(sha(hello));
//		try{
//			 md5 = MessageDigest.getInstance("MD5");
//			SHA = MessageDigest.getInstance("SHA");
//		}catch(Exception e){
//		}
//		System.out.println(md("ahsdahsdhaosdhashdoashiahs"));
//		System.out.println(md("ahsdahsdhaosdhashdoashidfahs"));
//
//		System.out.println("sha: "+sha("yihaodian/front-shoppingshareservice/gos-createCreateSoService.createSocreate"+
//				"NoGroup1shareservice/gos-createyihaodian/gss-writeGssFrozenStockServiceServer.frozenStockWithResultngssFrozenStockServiceHessian[order_create]2"));

		Set<String> list1 = new HashSet<>();
		Set<String> list2 = new HashSet<String>();
		list1.add("g");
		list1.add("s");
		list1.add("a");
		list1.add("f");
//		list2.add("g");
//		list2.add("c");
//		list2.add("b");
//		list2.add("a");
		list1.retainAll(list2);
		System.out.print(list1);

	}
	public static String sha(String s){
		byte[] md5Bytes = DigestUtils.sha(s.getBytes());
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++){
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}
	public static String md5(String str){
		byte[] md5Bytes = DigestUtils.md5(str.getBytes());
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++){
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
