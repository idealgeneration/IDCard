package com.xitnu.bean;

public class CardImg {
	private String strDir = "";
	private String strFrontImg = "";
	private String strBackImg = "";
	private String strUserImg = "";

	public CardImg(String strFrontImg, String strBackImg, String strUserImg, String strDir) {
		super();
		this.strFrontImg = strFrontImg;
		this.strBackImg = strBackImg;
		this.strUserImg = strUserImg;
		this.strDir = strDir;
	}

	public String getStrDir() {
		return strDir;
	}

	public void setStrDir(String strDir) {
		this.strDir = strDir;
	}

	public String getStrFrontImg() {
		return strFrontImg;
	}

	public void setStrFrontImg(String strFrontImg) {
		this.strFrontImg = strFrontImg;
	}

	public String getStrBackImg() {
		return strBackImg;
	}

	public void setStrBackImg(String strBackImg) {
		this.strBackImg = strBackImg;
	}

	public String getStrUserImg() {
		return strUserImg;
	}

	public void setStrUserImg(String strUserImg) {
		this.strUserImg = strUserImg;
	}
}
