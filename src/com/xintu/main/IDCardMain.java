package com.xintu.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.SoftBevelBorder;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xintu.utils.FileUtil;
import com.xintu.utils.HttpDownImg;
import com.xintu.utils.HttpUploadFile;
import com.xintu.utils.Util;
import com.xitnu.bean.CardImg;

public class IDCardMain extends JFrame implements ActionListener {
	public static Logger logger = Logger.getLogger(IDCardMain.class);

	static {
		String path = new File("").getAbsolutePath();
		FileAppender appender = (FileAppender) org.apache.log4j.Logger.getRootLogger().getAppender("appenderName2");
		appender.setFile(path + File.separator + "idcard.log");
	}
	// 实名认证中用到的路径
	public static final String strUrlAuthIndex = "https://rnr.10646.cn/portal/fast/auth";
	public static final String strUrlUpImg = "https://rnr.10646.cn/portal/fast/auth/upImage";
	public static final String strUrlICardNum = "https://rnr.10646.cn/portal/fast/auth/getICardNum";
	public static final String strUrlAuth = "https://rnr.10646.cn/portal/fast/auth/authInformation";
	public static final String strUrlHoldImg = "https://rnr.10646.cn/portal/fast/auth/doHoldImge";
	public static final String strUrlCodeImg = "https://rnr.10646.cn/portal/image?Seed=";
//  斐斐打码识别
	public static final String app_id = "312281";
	public static final String app_key = "XQ6OLC7Kjr24H2HxzCTgYv2rUK8bQOXK";
//	public static final String pd_id = "112281";
//	public static final String pd_key = "/IMCvjOUA0qx+wme8kdYLE1EfpnqjVOL";
	public static final String pd_id = "112474";
	public static final String pd_key = "4wpS3Pum+sEbPWygpT/PeWT+Lf2RblPE";
	public static final String pred_type = "10400";
	static Api api;

	public boolean isDebug = false;

//	ICCID:89860919700007523448
	JPanel jpContainer;
	JPanel jpContent;
	JTextField jfTxt;
	JTextField jfImg;
	JButton startBtn;
	JButton stopBtn;
	JPanel jpTips;
	JLabel jlTips;

	String strSIMTxt = "";
	static String strImgPath = "";

	boolean isStart = true;

	public static final String actioncommand_start = "startBtn";
	public static final String actioncommand_stop = "stopBtn";

	public Runnable run;
	public String tipValue = "";
	UpIDCardsThread upIDCardsThread = null;

	public static String rootPath = "";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		rootPath = System.getProperty("user.dir");
		System.out.println("根目录=" + rootPath);
		// 斐斐打码
		api = new Api();
		api.Init(app_id, app_key, pd_id, pd_key);
		// 加载界面
		IDCardMain instance = new IDCardMain();
	}

	public static void sleep() {
		try {
			Thread.sleep((long) (200 + Math.random() * 400));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String uploadIDCard(String strICCID, String phone, String strFrontImg, String strBackImg, String strUserImg)
			throws Exception {
//		String strImgPath1 = "C:/Users/xintu/Desktop/IDCard/aaa.jpg";// 身份证正面
//		String strImgPath2 = "C:/Users/xintu/Desktop/IDCard/bbb.jpg";// 身份证背面
//		String strImgPath3 = "C:/Users/xintu/Desktop/IDCard/ccc.jpg";// 客户手持身份证照片

		String cookies = "";
		System.out.println("0>>>" + strICCID + "==>开始");

		// 1.上传身份证照片
		// {"code":"0000","msg":"成功","data":"group1/M00/A1/63/rB7OsVzAHX2AMHD_AACkKsvQBXw018.png"}
		// {"code":"0000","msg":"成功","data":"group1/M00/A1/63/rB7OsVzAHYWACEoEAAC19UtBuq4466.png"}
		String resImg1 = HttpUploadFile.UploadImage(strUrlUpImg, strFrontImg);
		if (isDebug) {
			System.out.println("resImg1>>>" + resImg1);
		}
		if ("".equals(resImg1)) {
			return "resnull";
		}
		JSONObject josnImg1 = JSON.parseObject(resImg1);
		sleep();// 休息
		String resImg2 = HttpUploadFile.UploadImage(strUrlUpImg, strBackImg);
		if ("".equals(resImg2)) {
			return "resnull";
		}
		JSONObject josnImg2 = JSON.parseObject(resImg2);
		if (isDebug) {
			System.out.println("resImg2>>>" + resImg2);
		}

		if (!"0000".equals(josnImg1.getString("code")) || !"0000".equals(josnImg2.getString("code"))) {
			System.out.println("1<<<" + strICCID + "-->身份证上传失败");
			return "failed";
		}
		System.out.println("1>>>" + strICCID + ".upImg.success");

		tipValue = strICCID + ">>身份证正反面照片上传成功";
		SwingUtilities.invokeLater(run);
		sleep();// 休息

		// 2.获取身份证的信息
		// {"code":"0000","msg":"成功","data":{"iccid":"","vdate2":"2030.12.31",
		// "cacheKey":"fastAuthMMBUlw12nRNUfQAugKce9LQTiwlkbmZBauGjOP2Umuc=",
		// "vdate1":"2010.12.31","name":"张进","id":"310225197901022411"}}
		Map<String, String> mapICardNum = new HashMap<>();
		mapICardNum.put("idcardpUrl", josnImg1.getString("data"));
		mapICardNum.put("idcardbUrl", josnImg2.getString("data"));
		String resICardNum = HttpUploadFile.formUp(strUrlICardNum, mapICardNum, "", true);
		if (isDebug) {
			System.out.println("2<<<resICardNum=" + resICardNum);
		}
		if ("".equals(resICardNum)) {
			return "resnull";
		}
		JSONObject josnICardNum = JSON.parseObject(resICardNum);

		if ("3000".equals(josnICardNum.getString("code"))) {
			// {\"code\":\"3000\",\"msg\":\"身份证正面照读取内容缺失：姓名读取为空 \"}
			return "3000";
		}

		if (!"0000".equals(josnICardNum.getString("code"))) {
			System.out.println("2<<<" + strICCID + "-->获取身份证信息失败");
			return "failed";
		}

		JSONObject jsonData = josnICardNum.getJSONObject("data");
		String strCacheKey = jsonData.getString("cacheKey");
		String strID = jsonData.getString("id");
		String strVdate1 = jsonData.getString("vdate1");
		String strvdate2 = jsonData.getString("vdate2");
		String strName = jsonData.getString("name");
		System.out
				.println("2>>>" + strICCID + ".getICardNum.strID=" + strID + ",cookies=" + HttpUploadFile.getCookies());

		tipValue = strICCID + ">>获取身份证信息成功";
		SwingUtilities.invokeLater(run);

		// System.out.println(strCacheKey + "," + strID + "," + strVdate1 + "," +
		// strvdate2 + "," + strName);
		sleep();// 休息
		// 3.获取验证码code
		String filePath = rootPath + File.separator + "tmpcode" + File.separator + strICCID + ".png";
		String fileParentPath = rootPath + File.separator + "tmpcode";
		HttpDownImg.downImg(strUrlCodeImg, filePath, fileParentPath, HttpUploadFile.getCookies());
		String strCode = "";
		try {
			Util.HttpResp resp = api.PredictFromFile(pred_type, filePath);
			if (resp.ret_code == 0) {
				strCode = resp.pred_resl;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ("".equals(strCode)) {
			System.out.println("3<<<" + strICCID + "-->获取code的失败");
			return "failed";
		}
		System.out.println("3>>>" + strICCID + ".code=" + strCode);
		tipValue = strICCID + ">>获取验证码成功";
		SwingUtilities.invokeLater(run);

		sleep();// 休息

		// 4.身份认证
		// cacheKey: fastAuthMMBUlw12nRNUfQAugKce9LQTiwlkbmZBauGjOP2Umuc=
		// identityCode: 310225197901022411
		// custName: 张进
		// identityCodeFrom: 2010.12.31
		// identityCodeTo: 2030.12.31
		// iccid: 8986091970000752344
		// iccidOther: 8986091970000752344
		// phone: 18810261445
		// verificationCode: 8009

		// {"code":"0000","msg":"成功","data":{"cacheKey":"fastAuthMMBUlw12nRNUfQAugKce9LQTiwlkbmZBauGjOP2Umuc=","identityCode":"310225197901022411","custName":"张进","identityCodeFrom":"2010.12.31","identityCodeTo":"2030.12.31","iccid":"8986091970000752344","iccidOther":"8986091970000752344","phone":"18810261445","verificationCode":"8009","type":0}}

		Map<String, String> mapAuth = new HashMap<>();
		mapAuth.put("cacheKey", strCacheKey);
		mapAuth.put("identityCode", strID);
		mapAuth.put("custName", strName);
		mapAuth.put("identityCodeFrom", strVdate1);
		mapAuth.put("identityCodeTo", strvdate2);
		mapAuth.put("iccid", strICCID);
		mapAuth.put("iccidOther", strICCID);
		mapAuth.put("phone", phone);
		mapAuth.put("verificationCode", strCode);

		if (isDebug) {
			System.out.println("cacheKey=" + strCacheKey + ",identityCode=" + strID + ",custName=" + strName
					+ ",identityCodeFrom=" + strVdate1 + ",identityCodeTo=" + strvdate2 + ",iccid=" + strICCID
					+ ",phone=" + phone + ",verificationCode=" + strCode);
		}

		String resAuth = HttpUploadFile.formUp(strUrlAuth, mapAuth, HttpUploadFile.getCookies(), false);
		if ("".equals(resAuth)) {
			return "resnull";
		}
		JSONObject jsonresAuth = JSONObject.parseObject(resAuth);
		System.out.println("4<<<resAuth=" + resAuth);

		if ("7102".equals(jsonresAuth.getString("code"))) {
			// {"code":"7102","msg":"ICCID不能重复认证"}
			return "iccidfull";
		}
		if ("0004".equals(jsonresAuth.getString("code"))) {
			// {"code":"0004","msg":"身份证在国政通平台认证失败，证件号码未实名制"}
			return "0004";
		}
		if ("7104".equals(jsonresAuth.getString("code"))) {
//			{"code":"7104","msg":"系统错误请重新提交"}
			return "7104";
		}

		if (!"0000".equals(jsonresAuth.getString("code"))) {
			System.out.println("4<<<" + strICCID + "-->身份认证失败");
			return "failed";
		}

		System.out.println("4>>>" + strICCID + ".auth.success");
		tipValue = strICCID + ">>身份认证成功";
		SwingUtilities.invokeLater(run);
		sleep();// 休息
		// 5.上传客户手持身份证照片
		// {"code":"0000","msg":"成功","data":"group2/M00/DC/47/rB7OslzAHumAeTE7AADcc-XluG0928.png"}
		String resImg3 = HttpUploadFile.UploadImage(strUrlUpImg, strUserImg);

		if (isDebug) {
			System.out.println("5<<<resImg3=" + resImg3);
		}
		if ("".equals(resImg3)) {
			return "resnull";
		}
		JSONObject jsonImg3 = JSON.parseObject(resImg3);

		if (!"0000".equals(jsonImg3.getString("code"))) {
			System.out.println("5<<<" + strICCID + "-->手持身份证照片上传失败");
			return "failed";
		}
		System.out.println("5>>>" + strICCID + ".upImg2.success");

		sleep();// 休息
		tipValue = strICCID + ">>用户正面照片上传成功";
		SwingUtilities.invokeLater(run);
		// 6.最后客户认证
		// peImgUrl: group2/M00/DC/47/rB7OslzAHumAeTE7AADcc-XluG0928.png
		// cacheKey: fastAuthMMBUlw12nRNUfQAugKce9LQTiwlkbmZBauGjOP2Umuc=
		// {"code":"0000","msg":"成功","data":{"pageUrl":""}}
//			System.out.println(jsonImg3.getString("data") + "----" + strCacheKey);
		Map<String, String> mapHoldImg = new HashMap<>();
		mapHoldImg.put("peImgUrl", jsonImg3.getString("data"));
		mapHoldImg.put("cacheKey", strCacheKey);

		String resHoldImg = HttpUploadFile.formUp(strUrlHoldImg, mapHoldImg, HttpUploadFile.getCookies(), false);
		System.out.println("6<<<resHoldImg=" + resHoldImg);
		if ("".equals(resHoldImg)) {
			return "resnull";
		}
		JSONObject jsonHoldImg = JSON.parseObject(resHoldImg);
		if ("7101".equals(jsonHoldImg.getString("code"))) {
			// {"code":"7101","msg":"此证件已经绑定5张卡"}
			return "cardfull";
		}

		if (!"0000".equals(jsonHoldImg.getString("code"))) {
			System.out.println("6<<<" + strICCID + "-->认证失败");
			return "failed";
		} else {
			System.out.println("6>>>" + strICCID + "==>认证成功");
			return "success";
		}
	}

	public IDCardMain() {
		setLayout(new BorderLayout());
		SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);

		jpContainer = new JPanel();
		jpContainer.setLayout(new BoxLayout(jpContainer, BoxLayout.X_AXIS));

		Font myFont1 = new Font("宋体", Font.BOLD, 25);// 字体样式1
		Font myFont2 = new Font("宋体", Font.BOLD, 20);// 字体样式2
		Font myFont3 = new Font("宋体", Font.BOLD, 16);// 字体样式3

		jpContent = new JPanel();
		jpContent.setLayout(new GridLayout(5, 1));
//		jpContent.setBorder(sbb);
//		jpContent.setLayout(new BoxLayout(jpContent, BoxLayout.Y_AXIS));

		JLabel jlTitle = new JLabel("SIM卡实名系统", JLabel.CENTER);
		jlTitle.setFont(myFont1);

		JLabel jlTxt = new JLabel("SIM卡文档路径：");
		JLabel jlImg = new JLabel("身份证图片路径：");
		jlTxt.setFont(myFont2);
		jlImg.setFont(myFont2);

		jfTxt = new JTextField(15);
		jfImg = new JTextField(15);

		JPanel jpTxt = new JPanel();
		JPanel jpImg = new JPanel();
		jpTxt.add(jlTxt);
		jpTxt.add(jfTxt);
		jpImg.add(jlImg);
		jpImg.add(jfImg);

		jpTips = new JPanel();
		jlTips = new JLabel("请将上面所需的配置按要求填写");
		jlTips.setFont(myFont3);
		jlTips.setForeground(Color.blue);
		jpTips.add(jlTips);

		startBtn = new JButton("开始");
		startBtn.setFont(myFont2);
		startBtn.setContentAreaFilled(false);
		startBtn.addActionListener(this);
		startBtn.setActionCommand(actioncommand_start);

		stopBtn = new JButton("结束");
		stopBtn.setFont(myFont2);
		stopBtn.setContentAreaFilled(false);
		stopBtn.addActionListener(this);
		stopBtn.setActionCommand(actioncommand_stop);
		stopBtn.setEnabled(false);

		JPanel jpBtn = new JPanel();
//		jpBtn.add(stopBtn);
		jpBtn.add(startBtn);

		jpContent.add(jlTitle);
		jpContent.add(jpTxt);
		jpContent.add(jpImg);
		jpContent.add(jpTips);
		jpContent.add(jpBtn);

		jpContainer.add(jpContent);
		this.add(jpContainer);

		run = new Runnable() {// 实例化更新组件的线程
			public void run() {
				jlTips.setText("");
				jlTips.setText(tipValue);
			}
		};

		// 监听关闭窗口事件
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				int exi = JOptionPane.showConfirmDialog(null, "要退出该程序吗？", "提示", JOptionPane.YES_NO_OPTION);
				if (exi == JOptionPane.YES_OPTION) {

					System.exit(0);
				} else {
					return;
				}
			}
		});
		this.setResizable(false);
		this.setSize(500, 250);
		this.setTitle("SIM卡实名系统");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (actioncommand_start.equals(e.getActionCommand())) {
			strSIMTxt = jfTxt.getText().trim();
			strImgPath = jfImg.getText().trim();

			if ("".equals(strSIMTxt)) {
				int exi = JOptionPane.showConfirmDialog(null, "请填写SIM卡信息文档的路径！", "错误", JOptionPane.YES_NO_OPTION);
				return;
			}
			if (!strSIMTxt.endsWith("txt")) {
				int exi = JOptionPane.showConfirmDialog(null, "SIM卡信息文档必须是txt格式！", "错误", JOptionPane.YES_NO_OPTION);
				return;
			}
			if ("".equals(strImgPath)) {
				int exi = JOptionPane.showConfirmDialog(null, "请填写身份证 图片的路径！", "错误", JOptionPane.YES_NO_OPTION);
				return;
			}
			if (!strImgPath.endsWith("\\")) {
				strImgPath = strImgPath + "\\";
			}
			System.out.println("strSIMTxt= " + strSIMTxt + ",strImgPath=" + strImgPath);

			startBtn.setEnabled(false);
			stopBtn.setEnabled(true);
			isStart = true;

			if (upIDCardsThread == null) {
				upIDCardsThread = new UpIDCardsThread(strSIMTxt, strImgPath);
				upIDCardsThread.start();

			}

		} else if (actioncommand_stop.equals(e.getActionCommand())) {
			int exi = JOptionPane.showConfirmDialog(null, "是否要中断实名认证？", "提示", JOptionPane.YES_NO_OPTION);
			if (exi == JOptionPane.YES_OPTION) {
				isStart = false;
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
			}
		}
	}

	class UpIDCardsThread extends Thread {
		String strSIMTxt, strImgPath;

		public UpIDCardsThread(String strSIMTxt, String strImgPath) {
			this.strSIMTxt = strSIMTxt;
			this.strImgPath = strImgPath;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String res = uploadIDCards(strSIMTxt, strImgPath);
			if ("txtfail".equals(res)) {
				tipValue = "SIM卡信息文档格式错误！";
				SwingUtilities.invokeLater(run);
			} else if ("cardfail".equals(res)) {
				tipValue = "身份证照片读取失败！";
				SwingUtilities.invokeLater(run);
			} else if ("cardnull".equals(res)) {
				tipValue = "身份证照片照片用完了！";
				SwingUtilities.invokeLater(run);
			} else if ("suc".equals(res)) {
				tipValue = "SIM卡实名完成！";
				SwingUtilities.invokeLater(run);
			}
		}
	}

	private String uploadIDCards(String strSIMTxt, String strImgPath) {
		// 读取身份证照片
		ArrayList<CardImg> cardList = new ArrayList<CardImg>();
		try {
			FileUtil.readfile(strImgPath, cardList);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("cardList.size=" + cardList.size());

		if (cardList.size() == 0) {
			System.out.println("身份证照片读取失败！");
			return "cardfail";
		}
		Iterator<CardImg> it = cardList.iterator();
		while (it.hasNext()) {
			CardImg cardImg = it.next();
			System.out.println(cardImg.getStrDir() + "," + cardImg.getStrFrontImg() + "," + cardImg.getStrBackImg()
					+ "," + cardImg.getStrUserImg());
		}

		List<String> simList;
		try {
			simList = FileUtil.fileReadLine(strSIMTxt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "txtfail";
		}

		logger.info("ICCID实名认证开始");

		int j = 0;// 身份证图片下标
		int num = 0;
		int totalnum = 0;
		for (int i = 0; i < simList.size(); i++) {
			if (isStart) {
				System.out.println("sim=" + simList.get(i));
				String strICCID = simList.get(i);
				if (j >= cardList.size()) {
					System.out.println("<><><><><><><>身份证图片用完了<><><><><><><>");
					return "cardnull";
				}
				if ("".equals(strICCID)) {
					continue;
				}
				if (strICCID.length() < 19) {
					System.out.println(strICCID + "--->异常");
					continue;
				}

				strICCID = strICCID.substring(0, 19);

				tipValue = strICCID + "-->开始实名";
				SwingUtilities.invokeLater(run);
				num++;
				totalnum++;
				// 实名认证操作
				String strRes = "";
				try {
					strRes = uploadIDCard(strICCID, "18732910238", cardList.get(j).getStrFrontImg(),
							cardList.get(j).getStrBackImg(), cardList.get(j).getStrUserImg());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("e1=" + e1);
				}

				if ("failed".equals(strRes)) {// 失败
					logger.error("fail," + strICCID + "," + cardList.get(j).getStrDir());
					tipValue = strICCID + "-->实名失败";
					SwingUtilities.invokeLater(run);
				} else if ("3000".equals(strRes)) {// {\"code\":\"3000\",\"msg\":\"身份证正面照读取内容缺失：姓名读取为空 \"}
					logger.error("fail," + strICCID + "," + cardList.get(j).getStrDir()
							+ ",{\"code\":\"3000\",\"msg\":\"身份证正面照读取内容缺失：姓名读取为空 \"}");
					tipValue = strICCID + "->实名失败,身份证内容缺失";
					SwingUtilities.invokeLater(run);
				} else if ("0004".equals(strRes)) {// {\"code\":\"0004\",\"msg\":\"身份证在国政通平台认证失败，证件号码未实名制\"}
					logger.error("fail," + strICCID + "," + cardList.get(j).getStrDir()
							+ ",{\"code\":\"0004\",\"msg\":\"身份证在国政通平台认证失败，证件号码未实名制\"}");
					tipValue = strICCID + "->实名失败,证件未实名制";
					SwingUtilities.invokeLater(run);
				} else if ("7104".equals(strRes)) {// {\"code\":\"7104\",\"msg\":\"系统错误请重新提交\"}
					logger.error("fail," + strICCID + "," + cardList.get(j).getStrDir()
							+ ",{\"code\":\"7104\",\"msg\":\"系统错误请重新提交\"}");
					tipValue = strICCID + "->实名失败,系统错误";
					SwingUtilities.invokeLater(run);
				} else if ("cardfull".equals(strRes)) {// 身份证满5次
					logger.info("info," + cardList.get(j).getStrDir() + "满5次认证");
					tipValue = cardList.get(j).getStrDir() + "满5次认证";
					SwingUtilities.invokeLater(run);
					j++;// 下一个身份证
					num = 0;
					i--;// 再把当前iccid重新认证
				} else if ("iccidfull".equals(strRes)) {// ICCID已经认证
					num--;
					tipValue = strICCID + "->已实名";
					SwingUtilities.invokeLater(run);
					logger.error("suc," + strICCID + "已实名认证");
				} else if ("resnull".equals(strRes)) {
					logger.error("fail," + strICCID + "," + cardList.get(j).getStrDir() + ",接口数据返回为空");
					tipValue = strICCID + "->实名失败,系统错误";
					SwingUtilities.invokeLater(run);
				} else {// 成功
					tipValue = strICCID + "->实名成功";
					SwingUtilities.invokeLater(run);
					logger.error("suc," + strICCID + "," + cardList.get(j).getStrDir());
				}
				if (num == 5) {// 满5次换身份证
					j++;
					num = 0;
				}
				if (totalnum == 10) {
					totalnum = 0;
					System.out.println("操作完成10个，休息20秒");
					tipValue = "操作完成10个，休息20秒";
					SwingUtilities.invokeLater(run);
					try {
						Thread.sleep((long) (20000 + Math.random() * 1000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					System.out.println("操作完成1个，休息1秒");
					try {
						Thread.sleep((long) (300 + Math.random() * 500));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {
				return "stop";
			}
		}
		return "suc";
	}
}
