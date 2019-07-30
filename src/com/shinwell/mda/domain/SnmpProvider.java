package com.shinwell.mda.domain;

import java.io.Serializable;


/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月26日上午10:20:40
 * @Version: 1.0
 * @Desc:
 */
public class SnmpProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090637746994439568L;
	String displayName;
	String host;
	int port;
	String readCommunity;
	String writeCommunity;
	String enterpriseOID;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getReadCommunity() {
		return readCommunity;
	}
	public void setReadCommunity(String readCommunity) {
		this.readCommunity = readCommunity;
	}
	public String getWriteCommunity() {
		return writeCommunity;
	}
	public void setWriteCommunity(String writeCommunity) {
		this.writeCommunity = writeCommunity;
	}
	public String getEnterpriseOID() {
		return enterpriseOID;
	}
	public void setEnterpriseOID(String enterpriseOID) {
		this.enterpriseOID = enterpriseOID;
	}
	@Override
	public String toString() {
		return "SnmpProvider [displayName=" + displayName + ", host=" + host + ", port=" + port + ", readCommunity=" + readCommunity + ", writeCommunity=" + writeCommunity + ", enterpriseOID="
				+ enterpriseOID + "]";
	}

}
