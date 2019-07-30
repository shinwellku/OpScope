package com.shinwell.mda.domain;

import java.util.HashMap;
import java.util.Map;

import org.snmp4j.PDU;

public class SnmpTrap {
	String classType;
	String date;
	String type;
	String source;
	String msg;
	String version;
	String timestamp;
	String generalTrap;
	String specialTrap;
	String enterprise;
	Map<Object, Object> vbs;
	PDU pud;
	public static final String V1 = "Version1";
	public static final String V2 = "Version2";
	public static final String V3 = "Version3";

	public SnmpTrap() {
		this.vbs = new HashMap();
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGeneralTrap() {
		return this.generalTrap;
	}

	public void setGeneralTrap(String generalTrap) {
		this.generalTrap = generalTrap;
	}

	public String getSpecialTrap() {
		return this.specialTrap;
	}

	public void setSpecialTrap(String specialTrap) {
		this.specialTrap = specialTrap;
	}

	public String getEnterprise() {
		return this.enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public Map<Object, Object> getVbs() {
		return this.vbs;
	}

	public void setVbs(Map<Object, Object> vbs) {
		this.vbs = vbs;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public PDU getPud() {
		return pud;
	}

	public void setPud(PDU pud) {
		this.pud = pud;
	}
	
}