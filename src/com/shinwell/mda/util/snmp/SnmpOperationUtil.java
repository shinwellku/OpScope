package com.shinwell.mda.util.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月28日下午7:56:54
 * @Version: 1.0
 * @Desc:
 */
public class SnmpOperationUtil {
	static Logger logger = Logger.getLogger(SnmpOperationUtil.class.getSimpleName());
	public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	public static final String DEFAULT_PROTOCOL = "udp";
	public static final int DEFAULT_PORT = 161;
	public static final long DEFAULT_TIMEOUT = 3 * 1000L;
	public static final int DEFAULT_RETRY = 3;

	/**
	 * 创建对象communityTarget，用于返回target
	 * 
	 * @param targetAddress
	 * @param community
	 * @param version
	 * @param timeOut
	 * @param retry
	 * @return CommunityTarget
	 */
	public static CommunityTarget createDefault(String ip, String community, int version, long timeout, int retires) {
		Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(address);
		target.setVersion(version<=0 ? DEFAULT_VERSION : version);
		target.setTimeout(timeout<=0 ? DEFAULT_TIMEOUT : timeout); // milliseconds
		target.setRetries(retires<=0 ? DEFAULT_RETRY : retires);
		return target;
	}

	/* 根据OID，获取单条消息 */
	public static ArrayList<String> snmpGet(String ip, String community, String oid, int version, long timeout, int retires) {
		ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		try {
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));

			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			logger.info("-------> 发送PDU <-------");
			pdu.setType(PDU.GET);
			ResponseEvent respEvent = snmp.send(pdu, target);
			logger.info("PeerAddress:" + respEvent.getPeerAddress());
			PDU response = respEvent.getResponse();

			if (response == null) {
				logger.warn("response is null, request time out");
				result.add("response is null, request time out");
			} else {
				logger.info("response pdu size is " + response.size());
				for (int i = 0; i < response.size(); i++) {
					VariableBinding vb = response.get(i);
					logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
					result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
				}

			}
			logger.info("SNMP GET one OID value finished !");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("SNMP Get Exception:" + e);
			result.add("SNMP Get Exception:" + e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}

		}
		return result;
	}
	
	public static ArrayList<String> snmpGetBulk(String ip, String community, String oid, int version, long timeout, int retires) {
		ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		try {
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));

			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			logger.info("-------> 发送PDU <-------");
			pdu.setType(PDU.GETBULK);
			ResponseEvent respEvent = snmp.send(pdu, target);
			logger.info("PeerAddress:" + respEvent.getPeerAddress());
			PDU response = respEvent.getResponse();

			if (response == null) {
				logger.warn("response is null, request time out");
				result.add("response is null, request time out");
			} else {
				logger.info("response pdu size is " + response.size());
				for (int i = 0; i < response.size(); i++) {
					VariableBinding vb = response.get(i);
					logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
					result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
				}

			}
			logger.info("SNMP GET one OID value finished !");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("SNMP Get Exception:" + e);
			result.add("SNMP Get Exception:" + e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}

		}
		return result;
	}

	/* 根据OID列表，一次获取多条OID数据，并且以List形式返回 */
	public static void snmpGetList(String ip, String community, List<String> oidList, int version, long timeout, int retires) {
		ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		try {
			PDU pdu = new PDU();

			for (String oid : oidList) {
				pdu.add(new VariableBinding(new OID(oid)));
			}

			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			logger.info("-------> 发送PDU <-------");
			pdu.setType(PDU.GET);
			ResponseEvent respEvent = snmp.send(pdu, target);
			logger.info("PeerAddress:" + respEvent.getPeerAddress());
			PDU response = respEvent.getResponse();

			if (response == null) {
				logger.info("response is null, request time out");
				result.add("response is null, request time out");
			} else {
				logger.info("response pdu size is " + response.size());
				for (int i = 0; i < response.size(); i++) {
					VariableBinding vb = response.get(i);
					logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
					result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
				}

			}
			logger.info("SNMP GET one OID value finished !");
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("SNMP Get Exception:" + e);
			result.add("SNMP Get Exception:" + e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}
		}
	}

	/* 根据OID列表，采用异步方式一次获取多条OID数据，并且以List形式返回 */
	public static void snmpAsynGetList(String ip, String community, List<String> oidList, int version, long timeout, int retires) {
		final ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		try {
			PDU pdu = new PDU();

			for (String oid : oidList) {
				pdu.add(new VariableBinding(new OID(oid)));
			}

			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			logger.info("-------> 发送PDU <-------");
			pdu.setType(PDU.GET);
			ResponseEvent respEvent = snmp.send(pdu, target);
			logger.info("PeerAddress:" + respEvent.getPeerAddress());
			PDU response = respEvent.getResponse();

			/* 异步获取 */
			final CountDownLatch latch = new CountDownLatch(1);
			ResponseListener listener = new ResponseListener() {
				public void onResponse(ResponseEvent event) {
					((Snmp) event.getSource()).cancel(event.getRequest(), this);
					PDU response = event.getResponse();
					PDU request = event.getRequest();
					logger.info("[request]:" + request);
					if (response == null) {
						logger.info("[ERROR]: response is null");
					} else if (response.getErrorStatus() != 0) {
						logger.info("[ERROR]: response status" + response.getErrorStatus() + " Text:" + response.getErrorStatusText());
					} else {
						logger.info("Received response Success!");
						for (int i = 0; i < response.size(); i++) {
							VariableBinding vb = response.get(i);
							logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
							result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
						}
						logger.info("SNMP Asyn GetList OID finished. ");
						latch.countDown();
					}
				}
			};

			pdu.setType(PDU.GET);
			snmp.send(pdu, target, null, listener);
			logger.info("asyn send pdu wait for response...");

			boolean wait = latch.await(30, TimeUnit.SECONDS);
			logger.info("latch.await =:" + wait);

			snmp.close();

			logger.info("SNMP GET one OID value finished !");
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("SNMP Get Exception:" + e);
			result.add("SNMP Get Exception:" + e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}

		}
	}

	/* 根据targetOID，获取树形数据 */
	public static ArrayList<String> snmpWalk(String ip, String community, String targetOid, int version, long timeout, int retires) {
		ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		TransportMapping transport = null;
		Snmp snmp = null;
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();

			PDU pdu = new PDU();
			OID targetOID = new OID(targetOid);
			pdu.add(new VariableBinding(targetOID));

			boolean finished = false;
			logger.info("----> snmpwalk start <----");
			while (!finished) {
				VariableBinding vb = null;
				ResponseEvent respEvent = snmp.getNext(pdu, target);

				PDU response = respEvent.getResponse();

				if (respEvent == null || respEvent.getResponse() == null) {
					logger.info("snmp responseEvent is null or respEvnt.getResponse() is null ");
					result.add("responsePDU == null");
					finished = true;
					break;
				} else {
					vb = response.get(0);
				}
				// check finish
				finished = checkWalkFinished(targetOID, pdu, vb,result);
				if (!finished) {
					logger.info("==== walk each vlaue :");
					logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
					result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
					// Set up the variable binding for the next entry.
					pdu.setRequestID(new Integer32(0));
					pdu.set(0, vb);
				} else {
					logger.info("SNMP walk OID has finished.");
					snmp.close();
				}
			}
			logger.info("----> snmpwalk end <----");
		} catch (Exception e) {
			logger.error("Snmpwalk Exception: " + e);
			result.add("Snmpwalk Exception: " + e);
			//e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}
		}
		return result;
	}

	private static boolean checkWalkFinished(OID targetOID, PDU pdu, VariableBinding vb, ArrayList<String> result) {
		boolean finished = false;
		if (pdu.getErrorStatus() != 0) {
			//result.add("[true] responsePDU.getErrorStatus() != 0 ");
			logger.info("[true] responsePDU.getErrorStatus() != 0 ");
			logger.info(pdu.getErrorStatusText());
			finished = true;
		} else if (vb.getOid() == null) {
			//result.add("[true] vb.getOid() == null");
			logger.info("[true] vb.getOid() == null");
			finished = true;
		} else if (vb.getOid().size() < targetOID.size()) {
			//result.add("[true] vb.getOid().size() < targetOID.size()");
			logger.info("[true] vb.getOid().size() < targetOID.size()");
			finished = true;
		} else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
			//result.add("[true] targetOID.leftMostCompare() != 0");
			logger.info("[true] targetOID.leftMostCompare() != 0");
			finished = true;
		} else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
			//result.add("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
			logger.info("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
			finished = true;
		} else if (vb.getOid().compareTo(targetOID) <= 0) {
			//result.add("[true] Variable received is not " + "lexicographic successor of requested " + "one:");
			logger.info("[true] Variable received is not " + "lexicographic successor of requested " + "one:");
			logger.info(vb.toString() + " <= " + targetOID);
			finished = true;
		}
		return finished;

	}

	/* 根据targetOID，异步获取树形数据 */
	public static ArrayList<String> snmpAsynWalk(String ip, String community, String oid, int version, long timeout, int retires) {
		final ArrayList<String> result = new ArrayList<>();
		final CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		try {
			logger.info("----> demo start <----");
			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();

			final PDU pdu = new PDU();
			final OID targetOID = new OID(oid);
			final CountDownLatch latch = new CountDownLatch(1);
			pdu.add(new VariableBinding(targetOID));

			ResponseListener listener = new ResponseListener() {
				public void onResponse(ResponseEvent event) {
					((Snmp) event.getSource()).cancel(event.getRequest(), this);

					try {
						PDU response = event.getResponse();
						// PDU request = event.getRequest();
						// logger.info("[request]:" + request);
						if (response == null) {
							logger.info("[ERROR]: response is null");
						} else if (response.getErrorStatus() != 0) {
							logger.info("[ERROR]: response status" + response.getErrorStatus() + " Text:" + response.getErrorStatusText());
						} else {
							logger.info("Received Walk response value :");
							VariableBinding vb = response.get(0);

							boolean finished = checkWalkFinished(targetOID, pdu, vb,result);
							if (!finished) {
								logger.info(vb.getOid() + " = " + vb.getVariable());
								result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
								pdu.setRequestID(new Integer32(0));
								pdu.set(0, vb);
								((Snmp) event.getSource()).getNext(pdu, target, null, this);
							} else {
								logger.info("SNMP Asyn walk OID value success !");
								latch.countDown();
							}
						}
					} catch (Exception e) {
						//e.printStackTrace();
						latch.countDown();
					}

				}
			};

			snmp.getNext(pdu, target, null, listener);
			logger.info("pdu 已发送,等到异步处理结果...");

			boolean wait = latch.await(30, TimeUnit.SECONDS);
			logger.info("latch.await =:" + wait);
			snmp.close();

			logger.info("----> demo end <----");
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("SNMP Asyn Walk Exception:" + e);
		}
		return result;
	}

	/* 根据OID和指定string来设置设备的数据 */
	public static ArrayList<String> setPDU(String ip, String community, String oid, String val, int version, long timeout, int retires) throws IOException {
		ArrayList<String> result = new ArrayList<>();
		CommunityTarget target = createDefault(ip, community, version, timeout, retires);
		Snmp snmp = null;
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid), new OctetString(val)));
		pdu.setType(PDU.SET);

		DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		snmp.listen();
		logger.info("-------> 发送PDU <-------");
		
		ResponseEvent respEvent = snmp.send(pdu, target);
		logger.info("PeerAddress:" + respEvent.getPeerAddress());
		PDU response = respEvent.getResponse();

		if (response == null) {
			logger.warn("response is null, request time out");
			result.add("response is null, request time out");
		} else {
			logger.info("response pdu size is " + response.size());
			for (int i = 0; i < response.size(); i++) {
				VariableBinding vb = response.get(i);
				logger.info(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
				result.add(vb.getOid() + " = " + vb.getVariable().getSyntaxString() + ": " + vb.getVariable());
			}

		}
		logger.info("SNMP SET  OID value finished !");
		snmp.close();
		return result;
	}
}