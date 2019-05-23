package com.xintu.utils;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class HttpDownImg {

	public static boolean downImg(String imageUrl, String filepath, String fileParentPath, String cookies) {
		InputStream stream = null;
		try {
			File path = new File(fileParentPath);
			if (!path.exists()) {
				path.mkdir();
			}
			URL url = new URL(imageUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 设置通用的请求属性
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if (!"".equals(cookies)) {
				connection.setRequestProperty("Cookie", cookies);
			}

			// 得到请求的输出流对象
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write("".getBytes());
			out.flush();
			out.close();

			stream = connection.getInputStream();
			BufferedImage bufferedImg = ImageIO.read(stream);
			if (bufferedImg != null) {
				File file = null;
				if ("".equals(filepath)) {

				} else {
					file = new File(filepath);
					if (!file.exists()) {
						file.createNewFile();
					}
				}

				ImageIO.write(bufferedImg, "png", file);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}
}
