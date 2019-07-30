package com.shinwell.mda.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.ibm.icu.text.SimpleDateFormat;
import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.domain.SnmpTrap;

/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月26日下午12:19:02
 * @Version: 1.0
 * @Desc:
 */
public final class CommonUtil {
	static Logger logger = Logger.getLogger(CommonUtil.class.getSimpleName());

	public static void writeObjectToFile(String fileName, Object obj) {
		File file = new File(fileName);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
			objOut.close();
			logger.info("write object success!");
		} catch (IOException e) {
			logger.error("write object failed", e);
			e.printStackTrace();
		}
	}

	public static Object readObjectFromFile(String fileName) {
		Object temp = null;
		File file = new File(fileName);
		if (file.exists()) {
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				ObjectInputStream objIn = new ObjectInputStream(in);
				temp = objIn.readObject();
				objIn.close();
				logger.info("read object success!");
			} catch (IOException e) {
				logger.error("read object failed", e);
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException", e);
			}
		}

		return temp;
	}

	public static void zipFile(String saveDir, String saveFileName, List<?> data) throws IOException {
		String savePath = saveDir + File.separator + saveFileName + ".log";
		File file = new File(savePath);
		logger.info("DataPath:" + file.getAbsolutePath());
		if (!file.exists()) {
			boolean flag = file.createNewFile();
			if (flag) {
				logger.info("File to create successful!");
			}
		}
		FileOutputStream fout = new FileOutputStream(file, false);
		for (Object object : data) {
			if (null != object) {
				fout.write(object.toString().getBytes());
				fout.write("\n".getBytes());

			}
		}
		fout.flush();
		fout.close();
		if (file.exists()) {
			FileOutputStream fzout = new FileOutputStream(saveDir + File.separator + saveFileName + ".zip");
			ZipOutputStream zout = new ZipOutputStream(fzout);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipEntry ze = new ZipEntry(file.getName());
			zout.putNextEntry(ze);
			int length;
			byte[] buffer = new byte[4096];
			while ((length = bis.read(buffer)) != -1) {
				zout.write(buffer, 0, length);
			}
			zout.closeEntry();
			zout.close();
			bis.close();
			fis.close();
			file.delete();
		}

	}

	// 判断ip、端口是否可连接
	public static boolean isHostConnectable(String host, int port) {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	// 判断ip是否可以连接 timeOut是超时时间
	public static boolean isHostReachable(String host, Integer timeOut) {
		try {
			return InetAddress.getByName(host).isReachable(timeOut);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static SnmpTrap parseTrapText(String trapContent) {
		SnmpTrap trap = new SnmpTrap();
		int inxFrom = trapContent.indexOf("[");
		int inxTo = trapContent.lastIndexOf("]");

		String msg = trapContent.substring(inxFrom + 1, inxTo);
		System.out.println(msg);
		String[] msgs = msg.split(",");
		for (String s : msgs) {
			s = s.trim();
			if (s.startsWith("enterprise"))
				trap.setEnterprise(s.split("=")[1].trim());
			else if (s.startsWith("timestamp"))
				trap.setTimestamp(s.split("=")[1].trim());
			else if (s.startsWith("genericTrap"))
				trap.setGeneralTrap(s.split("=")[1].trim());
			else if (s.startsWith("specificTrap")) {
				trap.setSpecialTrap(s.split("=")[1].trim());
			}

		}
		inxFrom = msg.indexOf("[");
		inxTo = msg.lastIndexOf("]");
		msg = msg.substring(inxFrom + 1, inxTo);
		logger.info(msg);
		String[] oids = msg.split(";");
		for (String oid : oids) {
			int inx = oid.indexOf("=");
			if (inx != -1) {
				trap.getVbs().put(oid.substring(0, inx).trim(), oid.substring(inx + 1).trim());
			}
		}
		logger.info("bulid snmp trap from TrapText: "+trap.getVbs());
		return trap;

	}
	
	public static String formatDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
		
	}
	
	public static long calculateTimeDiffer(String startTime,String endTime) {
		long seconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		try {
			//Date startDate = sdf.parse("20181227142805.860000+000");
			//Date endDate = sdf.parse("20181227142855.188000+000");
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			seconds = (endDate.getTime() - startDate.getTime()) / (1000);
			logger.info(startTime+"-"+endTime+" 时间相差:" + seconds+"秒");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return seconds;
	}
	
	public static double calculateDoubleForLongData(Long firstData,Long secondData){
		 BigDecimal first = new BigDecimal(firstData);
		 BigDecimal second = new BigDecimal(secondData);
		 BigDecimal quotient = first.divide(second,2,BigDecimal.ROUND_HALF_UP);
		 //double result = quotient.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		 double result = quotient.doubleValue();
		 logger.info(" 计算结果：result="+result);
         return result;
	}
	public static double getDoubleWith2Bit(Double data){
		BigDecimal _data = new BigDecimal(data);
		double result = _data.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		logger.info(" 计算结果：result="+result);
		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Set<SmisProvider> set = new HashSet<>();
		SmisProvider provider = new SmisProvider();
		provider.setDisplayName("ds4700");
		provider.setUserName("admin");
		provider.setPassword("password");
		provider.setNamespace("root/ibm");
		provider.setPort(5988);
		provider.setProtocol("HTTP");
		provider.setHost("10.20.66.15");
		set.add(provider);
		CommonUtil.writeObjectToFile("conf/smis.dat", set);

		HashSet<SmisProvider> temp = (HashSet<SmisProvider>) CommonUtil.readObjectFromFile("conf/smis.dat");
		for (SmisProvider smisProvider : temp) {
			System.out.println(smisProvider.getDisplayName());
			System.out.println(smisProvider.getUserName());
		}

	}

}
