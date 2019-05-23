package com.xintu.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.xitnu.bean.CardImg;

public class FileUtil {

	public static List<String> fileReadLine(String filePath) throws Exception {
		List<String> list = new ArrayList<String>();
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		String str = null;
		while ((str = br.readLine()) != null) {
			if (!"".equals(str.trim())) {
				list.add(str.trim());
			}
		}
		return list;
	}

	public static void writeToFile(List<String> list, String strFilePath) {
		try {
			FileOutputStream fos = new FileOutputStream(strFilePath);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

			BufferedWriter bw = new BufferedWriter(osw);
			for (int i = 0; i < list.size(); i++) {
				bw.write(list.get(i) + "\r\n");
			}
			bw.flush();
			bw.close();
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件内容，作为字符串返回
	 */
	public static String readFileAsString(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(filePath);
		}

		if (file.length() > 1024 * 1024 * 1024) {
			throw new IOException("File is too large");
		}

		StringBuilder sb = new StringBuilder((int) (file.length()));
		// 创建字节输入流
		FileInputStream fis = new FileInputStream(filePath);
		// 创建一个长度为10240的Buffer
		byte[] bbuf = new byte[10240];
		// 用于保存实际读取的字节数
		int hasRead = 0;
		while ((hasRead = fis.read(bbuf)) > 0) {
			sb.append(new String(bbuf, 0, hasRead));
		}
		fis.close();
		return sb.toString();
	}

	/**
	 * 根据文件路径读取byte[] 数组
	 */
	public static byte[] readFileByBytes(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(filePath);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
			BufferedInputStream in = null;

			try {
				in = new BufferedInputStream(new FileInputStream(file));
				short bufSize = 1024;
				byte[] buffer = new byte[bufSize];
				int len1;
				while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
					bos.write(buffer, 0, len1);
				}

				byte[] var7 = bos.toByteArray();
				return var7;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException var14) {
					var14.printStackTrace();
				}

				bos.close();
			}
		}
	}

	/**
	 * 读取文件夹下中所有的文件
	 * 
	 * @param filepath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
//	static ArrayList<CardImg> cardList = new ArrayList<CardImg>();

	public static void readfile(String filepath, ArrayList<CardImg> cardList)
			throws FileNotFoundException, IOException {
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {

			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				String strFrontImg = "";
				String strBackImg = "";
				String strUserImg = "";
				String strDir = "";

				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						if (i == 0) {
							strFrontImg = readfile.getPath();
							strDir = readfile.getParent();
						} else if (i == 1) {
							strBackImg = readfile.getPath();
						} else if (i == 2) {
							strUserImg = readfile.getPath();
						}
					} else if (readfile.isDirectory()) {
						readfile(filepath + "\\" + filelist[i], cardList);
					}
				}

				if (!"".equals(strFrontImg) && !"".equals(strBackImg) && !"".equals(strUserImg)) {
					CardImg carImg = new CardImg(strFrontImg, strBackImg, strUserImg, strDir);
					cardList.add(carImg);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
	}
}
