/**
 * 
 */
package com.shinwell.mda.domain;

import java.io.Serializable;


/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年7月28日下午2:13:44
 * @Version: 1.0
 * @Desc:
 */
public class SmisProvider implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8972558061797511711L;
	
	String displayName;
	String namespace;
	int port;
	String userName;
	String password;
	String protocol;
	String host;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	@Override
	public String toString() {
		return "SmisProvider [displayName=" + displayName + ", namespace=" + namespace + ", port=" + port + ", userName=" + userName + ", password=" + password + ", protocol=" + protocol + ", host="
				+ host + "]";
	}
	

}
