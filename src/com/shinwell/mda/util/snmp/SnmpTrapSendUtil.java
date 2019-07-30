package com.shinwell.mda.util.snmp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.shinwell.mda.domain.SnmpTrap;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月28日下午7:56:47
 * @Version: 1.0
 * @Desc:
 */
public class SnmpTrapSendUtil {

	private static Logger logger = Logger.getLogger(SnmpTrapSendUtil.class);
	private static Snmp snmp = null;
	private Address targetAddress = null;
	private String host = "127.0.0.1";
	private int port = 162;
	private String addr;
	private int retries = 3;
	private int timeout = 3 * 1000;
	private TransportMapping  transport;

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public SnmpTrapSendUtil(String host, int port) {
		this.host = host;
		this.port = port;
		this.addr = "udp:" + this.host + "/" + this.port;
	}

	private static PDU createPDU(int pduType) {
		PDU request;
		if (pduType == PDU.V1TRAP) {
			request = new PDUv1();
		} else {
			request = new PDU();
		}
		request.setType(pduType);
		return request;
	}

	private CommunityTarget createCommunityTarget(int version, int retries, int timeout, String community) {
		// 设置 target
		CommunityTarget target = new CommunityTarget();
		target.setAddress(targetAddress);
		// 通信不成功时的重试次数
		target.setRetries(retries <= 0 ? getRetries() : retries);
		// 超时时间
		target.setTimeout(timeout <= 0 ? getTimeout() : timeout*1000);
		// snmp版本
		target.setVersion(version);
		if (null!=community&&community.trim().length()>0) {
			target.setCommunity(new OctetString(community));
		}
		return target;
	}

	public void initComm() throws IOException {
		// 设置管理进程的IP和端口
		targetAddress = GenericAddress.parse(this.addr);
		transport = new DefaultUdpTransportMapping();
		//transport = new DefaultTcpTransportMapping();
		snmp = new Snmp(transport);
		transport.listen();
	}

	public void sendTrap(SnmpTrap trap, int trapType, int version, int retries, int timeout, String community) throws IOException {
		if (trapType == PDU.V1TRAP) {
			if (version == SnmpConstants.version1) {
				sendV1Trap(trap.getEnterprise(),trap.getSpecialTrap(),trap.getVbs(), SnmpConstants.version1, retries, timeout, community);
			}
//			else if (version == SnmpConstants.version2c) {
//				sendV1Trap(trap.getEnterprise(),trap.getSpecialTrap(),vars, SnmpConstants.version2c, retries, timeout, community);
//			} else if (version == SnmpConstants.version3) {
//				sendV1Trap(trap.getEnterprise(),trap.getSpecialTrap(),vars, SnmpConstants.version3, retries, timeout, community);
//			}
		} else if (trapType == PDU.TRAP) {
			if (version == SnmpConstants.version1) {
				sendV2Trap(trap.getVbs(), SnmpConstants.version1, retries, timeout, community);
			} else if (version == SnmpConstants.version2c) {
				sendV2Trap(trap.getVbs(), SnmpConstants.version2c, retries, timeout, community);
			} else if (version == SnmpConstants.version3) {
				sendV2Trap(trap.getVbs(), SnmpConstants.version3, retries, timeout, community);
			}
		} else {
			logger.warn("not correct trapType!");
		}

	}

	private void sendV1Trap(String enterprise,String specialTrap,Map<Object, Object> vars, int version, int retries, int timeout, String community) throws IOException {
		CommunityTarget target = createCommunityTarget(version, retries, timeout, community);
		// 建立PDU对象
		PDUv1 pdu = (PDUv1) createPDU(PDU.V1TRAP);
		pdu.setEnterprise(new OID(enterprise));
		pdu.setGenericTrap(6);
		pdu.setSpecificTrap(Integer.parseInt(specialTrap));
		// 发起snmp request
		for (Entry<Object, Object> entry : vars.entrySet()) {
			pdu.add(new VariableBinding(new OID(entry.getKey().toString()), new OctetString(entry.getValue().toString())));
		}
		 // 向Agent发送PDU，并接收Response 
		ResponseEvent respEvnt = snmp.send(pdu, target);
		 // 解析Response    
        if (respEvnt != null && respEvnt.getResponse() != null) {    
            Vector<VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();    
            for (int i = 0; i < recVBs.size(); i++) {    
                VariableBinding recVB = recVBs.elementAt(i);    
              logger.info("发送Trap返回内容："+recVB.getOid() + " : " + recVB.getVariable());    
            }    
        }    
		snmp.close();
		
	}

	private void sendV2Trap(Map<Object, Object> vars, int version, int retries, int timeout, String community) throws IOException {
		//
		CommunityTarget target = createCommunityTarget(version, retries, timeout, community);
		// 建立PDU对象
		PDU pdu = createPDU(PDU.TRAP);

		// 1.let uptime just be system time...
		TimeTicks sysUpTime = new TimeTicks((long) (System.currentTimeMillis() / 1000));
		pdu.add(new VariableBinding(SnmpConstants.sysUpTime, sysUpTime));

		// 2.define snmp trap OID
		OID trapOID = SnmpConstants.coldStart;
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, trapOID));

		// 发起snmp request
		for (Entry<Object, Object> entry : vars.entrySet()) {
			pdu.add(new VariableBinding(new OID(entry.getKey().toString()), new OctetString(entry.getValue().toString())));
		}

		// 发起snmp request
		snmp.send(pdu, target);
		snmp.close();
	}

	@SuppressWarnings("unused")
	private void sendV3Trap(Map<Object, Object> vars, int version, int retries, int timeout, String community) throws IOException {
		// TrasportMapping
		TransportMapping transport;
		transport = new DefaultUdpTransportMapping();
		transport.listen();

		// Creating USM
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);

		// Add user to the USM
		snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID, new OctetString("MD5DESUsrAuthPwd"), PrivDES.ID, new OctetString("MD5DESUsrPrivPwd")));

		// Create the target
		Address targetAddress = GenericAddress.parse(this.addr);
		UserTarget target = new UserTarget();
		target.setAddress(targetAddress);
		target.setRetries(retries <= 0 ? getRetries() : retries);
		target.setTimeout(timeout <= 0 ? getTimeout() : timeout);
		target.setVersion(version);
		target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
		target.setSecurityName(new OctetString("MD5DES"));
		// Create PDU
		ScopedPDU pdu = new ScopedPDU();

		TimeTicks sysUpTime = new TimeTicks((long) (System.currentTimeMillis() / 1000));
		pdu.add(new VariableBinding(SnmpConstants.sysUpTime, sysUpTime));
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(InetAddress.getLocalHost())));
		// 发起snmp request
		for (Entry<Object, Object> entry : vars.entrySet()) {
			pdu.add(new VariableBinding(new OID(entry.getKey().toString()), new OctetString(entry.getValue().toString())));
		}

		pdu.setType(ScopedPDU.TRAP);
		snmp.send(pdu, target);
		snmp.close();
	}

	public static void main(String[] args) throws IOException {
		SnmpTrapSendUtil trapSendUtil = new SnmpTrapSendUtil("127.0.0.1", 162);
		trapSendUtil.initComm();
		SnmpTrap trap = new SnmpTrap();
		Map<Object, Object> vbs = new HashMap<Object, Object>();
		vbs.put("1.3.6.4.2.1.365000", "hello word!");
		trap.setVbs(vbs);
		trapSendUtil.sendTrap(trap, PDU.TRAP, SnmpConstants.version2c, 3, 3000, "public");
	}
}