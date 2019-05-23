package com.xintu.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * java通过模拟post方式提交表单实现图片上传功能实例 其他文件类型可以传入 contentType 实现
 * 
 * @author zdz8207 {@link http://www.cnblogs.com/zdz8207/}
 * @version 1.0
 */
public class HttpUploadFile {

//    public static void main(String[] args) {
//    	domain();
//    }

//	public static void domain() {
//		String fileName1 = "C:/Users/xintu/Desktop/a.png";
//		String fileName2 = "C:/Users/xintu/Desktop/b.png";
//		String url = "https://rnr.10646.cn/portal/fast/auth/getICardNum";
//		String resImg1 = UploadImage(fileName1);
//		String resImg2 = UploadImage(fileName2);
//
//		Map<String, String> map = new HashMap<>();
//
//		JSONObject josnImg1 = JSON.parseObject(resImg1);
//		JSONObject josnImg2 = JSON.parseObject(resImg2);
//		map.put("idcardpUrl", josnImg1.getString("data"));
//		map.put("idcardbUrl", josnImg2.getString("data"));
//
//		String res = formUp(url, map);
//
//		System.out.println("res=" + res);
//
//	}
	public static String firstCookie = "";

	public static String getCookies() {
		String[] firstCookies = firstCookie.split(";");
		return firstCookies[0];
	}

	/**
	 * 测试上传png图片
	 * 
	 */
	public static String UploadImage(String url, String fileName) {
//		Map<String, String> textMap = new HashMap<String, String>();
//		// 可以设置多个input的name，value
//		textMap.put("name", "file");
		// 设置file的name，路径
		Map<String, String> fileMap = new HashMap<String, String>();
		fileMap.put("file", fileName);
		String contentType = "";

		String ret = formUpImg(url, null, fileMap, contentType);
		return ret;
	}

	public static String formUp(String url, Map<String, String> params, String cookie, boolean isGetCookie)
			throws Exception {
		URL u = null;
		HttpURLConnection con = null;
		// 构建请求参数
		StringBuffer sb = new StringBuffer();
//		String strContent = "";
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
				sb.append("&");

			}
//			strContent = sb.substring(0, sb.length() - 1);
		}
//		System.out.println("send_data:" + strContent);
		// 尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			//// POST 只能为大写，严格限制，post会不识别
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (!"".equals(cookie)) {
				con.setRequestProperty("Cookie", cookie);
			}
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(sb.toString());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}

			if (isGetCookie) {
				// 获取cookie
				Map<String, List<String>> map = con.getHeaderFields();
				Set<String> set = map.keySet();
//				for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				for (String key : set) {
					// String key = (String) iterator.next();
					if (key == null) {
						continue;
					}
					if (key.equals("Set-Cookie")) {
						List<String> list = map.get(key);
						StringBuilder builder = new StringBuilder();
						for (String str : list) {
							builder.append(str).toString();
						}
						firstCookie = builder.toString();
//						System.out.println("cookie=" + firstCookie);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	/**
	 * 上传图片
	 * 
	 * @param urlStr
	 * @param textMap
	 * @param fileMap
	 * @param contentType 没有传入文件类型默认采用application/octet-stream
	 *                    contentType非空采用filename匹配默认的图片类型
	 * @return 返回response数据
	 */
	@SuppressWarnings("rawtypes")
	public static String formUpImg(String urlStr, Map<String, String> textMap, Map<String, String> fileMap,
			String contentType) {
		String res = "";
		HttpURLConnection conn = null;
		// boundary就是request头和上传文件内容的分隔符
//		String BOUNDARY = "---------------------------123821742118716";
		String BOUNDARY = "----WebKitFormBoundary3gAJIXGzWEuFsllr";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}
			// file
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					// 没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
					contentType = new MimetypesFileTypeMap().getContentType(file);
					// contentType非空采用filename匹配默认的图片类型
					if (!"".equals(contentType)) {
						if (filename.endsWith(".png")) {
							contentType = "image/png";
						} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
								|| filename.endsWith(".jpe")) {
							contentType = "image/jpeg";
						} else if (filename.endsWith(".gif")) {
							contentType = "image/gif";
						} else if (filename.endsWith(".ico")) {
							contentType = "image/image/x-icon";
						}
					}
					if (contentType == null || "".equals(contentType)) {
						contentType = "application/octet-stream";
					}
					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
					out.write(strBuf.toString().getBytes());
					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();
			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}
}