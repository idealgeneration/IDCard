package com.xintu.del;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

import javax.imageio.ImageIO;

public class RuoKuai {

	private static String httpRequestData(String url, String param) throws IOException {
		// TODO Auto-generated method stub
		URL u;
		HttpURLConnection con = null;
		OutputStreamWriter osw;
		StringBuffer buffer = new StringBuffer();

		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
		osw.write(param);
		osw.flush();
		osw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	/**
	 * 字符串MD5加密
	 * 
	 * @param s 原始字符串
	 * @return 加密后字符串
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 查询用户信息
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static String getInFo(String username, String password) {
		String ret = "";
		String param = String.format("username=%s&password=%s", username, password);
		try {
			ret = httpRequestData("http://api.ruokuai.com/info.xml", param);
		} catch (Exception e) {
			ret = "未知问题";
		}

		return ret;

	}

	/**
	 * 图像识别
	 * 
	 * @param url   请求URL，不带参数 如：http://api.ruokuai.com/create.xml
	 * @param param 请求参数，如：username=test&password=1
	 * @param data  图片二进制流
	 * @return 平台返回结果XML样式
	 * @throws IOException
	 */
	public static String httpPostImage(String url, String param, byte[] data) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;

		u = new URL(url);

		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		// con.setReadTimeout(95000);
		con.setConnectTimeout(95000); // 此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		out = con.getOutputStream();

		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			String paramString = "Content-Disposition: form-data; name=\"" + paramValue.split("[=]")[0] + "\"\r\n\r\n"
					+ paramValue.split("[=]")[1];
			out.write(paramString.getBytes("UTF-8"));
		}
		out.write(boundarybytesString.getBytes("UTF-8"));

		String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\"" + "sample.gif"
				+ "\"\r\nContent-Type: image/gif\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));

		out.write(data);

		String tailer = "\r\n--" + boundary + "--\r\n";
		out.write(tailer.getBytes("UTF-8"));

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	/**
	 * 上传图像并返回结果
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @param typeid   图像类型 ，建议询问在线客服，做最优设置
	 * @param timeout  任务超时时间
	 * @param softid   软件ID
	 * @param softkey  软件KEY
	 * @param filePath 图像截图或原始图二进制数据路径
	 * @return
	 * @throws IOException
	 */
	public static String createByPost(String username, String password, String typeid, String timeout, String softid,
			String softkey, String filePath) {
		String result = "";
		String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s", username,
				password, typeid, timeout, softid, softkey);
		try {
			File f = new File(filePath);
			if (null != f) {
				int size = (int) f.length();
				byte[] data = new byte[size];
				FileInputStream fis = new FileInputStream(f);
				fis.read(data, 0, size);
				if (null != fis)
					fis.close();

				if (data.length > 0)
					result = RuoKuai.httpPostImage("http://api.ruokuai.com/create.json", param, data);
			}
		} catch (Exception e) {
			result = "未知问题" + e.getMessage();
		}

		return result;
	}

	/**
	 * 识别图像URL
	 * 
	 * @param softId
	 * @param softKey
	 * @param typeid
	 * @param username
	 * @param password
	 * @param url
	 * @return
	 */
	public static String createByUrl(String softId, String softKey, String typeid, String username, String password,
			String url, String cookie) {
		// TODO Auto-generated method stub
		String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s", username,
				password, typeid, "90", softId, softKey);
		ByteArrayOutputStream baos = null;
		String ret;
		HttpURLConnection con = null;
		InputStream stream = null;

		try {
			URL u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Cookie", cookie);

			stream = con.getInputStream();

			BufferedImage image = ImageIO.read(stream);

			baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			byte[] data = baos.toByteArray();
			baos.close();
			ret = httpPostImage("http://api.ruokuai.com/create.json", param, data);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}

		return ret;
	}

	/**
	 * 报错
	 * 
	 * @param username
	 * @param password
	 * @param softId
	 * @param softKey
	 * @param ID
	 * @return
	 */

	public static String error(String username, String password, String softId, String softKey, String ID) {
		// TODO Auto-generated method stub
		String ret = "";
		String param = String.format("username=%s&password=%s&softid=%s&softkey=%s&id=%s", username, password, softId,
				softKey, ID);
		try {
			ret = httpRequestData("http://api.ruokuai.com/reporterror.xml", param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误" + e.getMessage();
		}

		return ret;
	}

	/**
	 * 注册
	 * 
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */

	public static String zhuce(String username, String password, String email) {
		// TODO Auto-generated method stub
		String ret = "";
		String param = String.format("username=%s&password=%s&email=%s", username, password, email);
		try {
			ret = httpRequestData("http://api.ruokuai.com/register.xml", param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}
		return ret;
	}

	/**
	 * 充值
	 * 
	 * @param username
	 * @param kid
	 * @param key
	 * @return
	 */

	public static String chongzhi(String username, String kid, String key) {
		// TODO Auto-generated method stub

		String ret = "";
		String param = String.format("username=%s&password=%s&id=%s", username, key, kid);
		try {
			ret = httpRequestData("http://api.ruokuai.com/recharge.xml", param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}

		return ret;
	}
}
