package com.shinwell.mda.util.snmp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import com.shinwell.mda.domain.SnmpTrap;
import com.shinwell.mda.util.CommonUtil;

/**
 * 
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年4月28日下午7:57:04
 * @Version: 1.0
 * @Desc:This class is used to listen for Trap information about the agent process
 */
public class SnmpMTTrapReceiver implements CommandResponder {
	static Logger logger = Logger.getLogger(SnmpMTTrapReceiver.class.getSimpleName());
    static ArrayList<SnmpTrap> traps =  new ArrayList<>();
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;
	private String host="127.0.0.1";
	private int port=162;
	private String addr ;
	TransportMapping transport;
    public static SnmpMTTrapReceiver instance = null;
    public   boolean status = false;
    private Table trapTable;
    private static String community;
    
	public  boolean isStatus() {
		return this.status;
	}

	public  void setStatus(boolean status) {
		this.status = status;
	}
	
	public static ArrayList<SnmpTrap> getTraps() {
		return traps;
	}

	public static SnmpMTTrapReceiver getInstance(String host,int port, String community, Table trapTable) throws UnknownHostException, IOException{
		//synchronized (instance) {
			 if (null==instance) {
				 instance = new SnmpMTTrapReceiver(host, port,community,trapTable);
			}
	//	}
			 SnmpMTTrapReceiver.community = community;
		return instance;
	}
	

	private SnmpMTTrapReceiver(String host,int port, String community, Table trapTable) throws UnknownHostException, IOException {
		this.host = host;
		this.port =  port;
		SnmpMTTrapReceiver.community = community;
		this.addr = "udp:"+this.host+"/"+this.port;
		this.trapTable =  trapTable;
			init();
			snmp.addCommandResponder(this);
	}

	public void run() {
		try {
				setStatus(true);
				logger.info("--------------->Start receiving Trap information @"+this.addr+"<---------------");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close(){
		if (null!=snmp) {
			try {
				transport.close();
//				snmp.removeCommandResponder(this);
//				snmp.removeTransportMapping(transport);
//				snmp.removeNotificationListener(GenericAddress.parse(System.getProperty("snmp4j.listenAddress", this.addr)));
				snmp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", this.addr)); // 本地IP与监听端口
		// 对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
		}
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		transport.listen();
		snmp.listen();
	}

	/**
	 * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息 当接收到trap时，会自动进入这个方法
	 */
	@SuppressWarnings("unchecked")
	public void processPdu(CommandResponderEvent event) {
		//System.out.println("============="+new String(event.getSecurityName()));
		// 解析Response
		if (event != null && event.getPDU() != null) {
			String community = new String(event.getSecurityName());
			if (status) {
				if (null==SnmpMTTrapReceiver.community||SnmpMTTrapReceiver.community.trim().length()==0) {
					handleTrapEvent(event);
				}else if (SnmpMTTrapReceiver.community.trim().equalsIgnoreCase(community)) {
					handleTrapEvent(event);
				}else {
					logger.warn("The trap received cannot be identified or the Community does not match.");
				}
			}
		}
	}

	private void handleTrapEvent(CommandResponderEvent event) {
		final SnmpTrap trap = new SnmpTrap();
		trap.setSource(event.getPeerAddress().toString());//source
		trap.setClassType(event.getSecurityLevel()+"");
		trap.setType(event.getPDU().getType()==PDU.V1TRAP?"V1TRAP":"TRAP");
		trap.setDate(CommonUtil.formatDate(Calendar.getInstance().getTime()));
		Vector<VariableBinding> recVbs = event.getPDU().getVariableBindings();
		HashMap<Object, Object> vbs = new HashMap<>();
		trap.setVbs(vbs);
		trap.setPud(event.getPDU());
		trap.setMsg(recVbs.toString().replace("[", "").replace("]", ""));
		for (int i = 0; i < recVbs.size(); i++) {
			VariableBinding vb = recVbs.elementAt(i);
			vbs.put(vb.getOid(), vb.getVariable());
			logger.info(vb.getOid() + " =" + vb.getVariable());
		}
		//final String[] values = {trap.getClassType(),trap.getType(),trap.getSource(),trap.getDate(),trap.getMsg()};
		final String[] values = {trap.getType(),trap.getSource(),trap.getDate(),trap.getMsg()};
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				TableItem item = new TableItem(trapTable, SWT.NONE);
				item.setData(trap);
				item.setText(values);
				trapTable.layout();
			}
		});
	}

	public void stop() {
		logger.info("--------------->Stop receiving Trap information @"+this.addr+"<---------------");
		setStatus(false);
	}

	public static void main(String[] args) {
		SnmpMTTrapReceiver trapreceiver;
		try {
			trapreceiver = new SnmpMTTrapReceiver("127.0.0.1",162,null,null);
			trapreceiver.run();
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
}