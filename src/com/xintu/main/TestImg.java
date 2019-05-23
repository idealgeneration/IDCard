package com.xintu.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.xintu.utils.Base64Util;
import com.xintu.utils.FileUtil;
import com.xintu.utils.HttpDownImg;
import com.xintu.utils.HttpUploadFile;
import com.xintu.utils.HttpUtil;
import com.xitnu.bean.CardImg;

public class TestImg {
	public static void main(String[] args) {
//		String softId = "112360";
//		String softKey = "ab36a2dd9e924b92ae4921f2443bb7f3";
//		String typeid = "1040";
//		String username = "lx2584765322";
//		String password = "lixiang258476";
//		String url = "https://rnr.10646.cn/portal/image?Seed=";
////		String url="C:/Users/xintu/Desktop/image.png";
//		
//		String res=RuoKuai.createByUrl(softId, softKey, typeid, username, password, url,"");
//		System.out.println(res);

//		// baidu文字识别
//		String APPID = "16293023";
//		String APIKEY = "8iEiXcsWX0tvYYcr4BSuNyT3";
//		String SECRETKEY = "G31UN9hnCKWzRkLayP2svGTONxxEGxph";
//		String baiduUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
//
//		// 24.670d0f5738c6b2a24812d1630a1ffac1.2592000.1560923691.282335-16293023
//		String strAccessToken = "24.670d0f5738c6b2a24812d1630a1ffac1.2592000.1560923691.282335-16293023";
////		String strAccessToken = AuthService.getAuth(APIKEY, SECRETKEY);
//		System.out.println("strAccessToken=" + strAccessToken);
//
//		String params;
//		String filePath = "C:/Users/xintu/Desktop/image.png";
//
//		try {
//			byte[] imgData = FileUtil.readFileByBytes(filePath);
//			String imgStr = Base64Util.encode(imgData);
//			params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
//			String resICardNum = HttpUtil.post(baiduUrl, strAccessToken, params);
//			System.out.println("resICardNum=" + resICardNum);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		String imageUrl = "https://rnr.10646.cn/portal/image?Seed=";
//		String filePath = "C:/Users/xintu/Desktop/b.png";
//
//		HttpDownImg.downImg(imageUrl, filePath, "");
//
//		AuthService.getCode(filePath);

		// 斐斐打码
//		try {
//			Api api = new Api();
//			String app_id = "312281";
//			String app_key = "XQ6OLC7Kjr24H2HxzCTgYv2rUK8bQOXK";
//			String pd_id = "112281";
//			String pd_key = "/IMCvjOUA0qx+wme8kdYLE1EfpnqjVOL";
//			// 对象生成之后，在任何操作之前，需要先调用初始化接口
//			api.Init(app_id, app_key, pd_id, pd_key);
//
//			String pred_type = "10400";
//			// 通过文件进行验证码识别,请使用自己的图片文件替换
//			String img_file = "C:\\Users\\xintu\\Desktop\\image.png";
//			Util.HttpResp resp;
//
//			resp = api.PredictFromFile(pred_type, img_file);
//			System.out.println("resp=" + resp);
////			System.out.printf("predict from file!ret: %d cust: %f err: %s reqid: %s pred: %s\n", resp.ret_code,
////					resp.cust_val, resp.err_msg, resp.req_id, resp.pred_resl);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // 返回识别结果的详细信息
//
//		String strPath = "C:\\Users\\xintu\\Desktop\\card\\";
//		try {
//			ArrayList<CardImg> cardList = new ArrayList<CardImg>();
//			FileUtil.readfile(strPath, cardList);
//			System.out.println("cardList.size=" + cardList.size());
//
//			Iterator<CardImg> it = cardList.iterator();
//			while (it.hasNext()) {
//				CardImg cardImg = it.next();
//				System.out.println(
//						cardImg.getStrFrontImg() + "," + cardImg.getStrBackImg() + "," + cardImg.getStrUserImg());
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		{"code":"0000","msg":"成功","data":{"iccid":"","vdate2":"2036.02.15","cacheKey":"fastAuthKa+9tQ0RUjpIv1mq78J0C9Kp9JjF7CNrqBRPrTS/2fw=","vdate1":"2016.02.15","name":"李化成","id":"610526198205021333"}}
//		{"code":"0000","msg":"成功","data":{"iccid":"","vdate2":"2024.11.26","cacheKey":"fastAuth0skjA8XFIUE2Ac4oRTShcyyTLEr82nUxLvxx8EutxDA=","vdate1":"2014.11.26","name":"韩宁宁","id":"642226199606200418"}}
//		Map<String, String> mapAuth = new HashMap<>();
//		mapAuth.put("cacheKey", "fastAuthKa+9tQ0RUjpIv1mq78J0C9Kp9JjF7CNrqBRPrTS/2fw=");
//		mapAuth.put("identityCode", "610526198205021333");
//		mapAuth.put("custName", "李化成");
//		mapAuth.put("identityCodeFrom", "2016.02.15");
//		mapAuth.put("identityCodeTo", "2036.02.15");
//		mapAuth.put("iccid", "8986091871001034474");
//		mapAuth.put("iccidOther", "8986091871001034474");
//		mapAuth.put("phone", "18810261445");
//		mapAuth.put("verificationCode", "3053");
//
//		String resAuth = HttpUploadFile.formUp("https://rnr.10646.cn/portal/fast/auth/authInformation", mapAuth,
//				"JSESSIONID=node0eqa9hnz24abrd4marc5ewur428320.node0", false);
//		JSONObject jsonresAuth = JSONObject.parseObject(resAuth);
//		System.out.println("4<<<resAuth=" + resAuth);
	}
}
