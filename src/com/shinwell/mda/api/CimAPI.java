package com.shinwell.mda.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.cim.CIMClass;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;

import org.apache.log4j.Logger;

import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.sample.Jsr48CimSample;

/**
 * @Project_name smis_tool
 * @Author: Shinwell Ku
 * @Date: 2018年10月9日
 * @Version: 1.0
 * @Desc:
 */
public final class CimAPI {
	Logger logger =  Logger.getLogger(CimAPI.class.getName());
	private WBEMClient client = null;
	ThreadLocal<WBEMClient> threadLocal = new ThreadLocal<WBEMClient>();
	CIMObjectPath cop;

	public WBEMClient getClient() {
		return client;
	}

	/**
	 * 
	 * @param pSchema
	 *            (http/https)
	 * @param pHostIp
	 *            (http:5988/https:5989)
	 * @param pHostPort
	 * @param pNamespace
	 * @param pUser
	 * @param pPawd
	 * @return
	 * @throws WBEMException
	 */
	public WBEMClient getWbemClient(String pSchema, String pHostIp, String pHostPort, String pNamespace, String pUser, String pPawd) throws WBEMException {
		if (threadLocal.get() != null) {
			logger.info("..........get wbemclient from cache.............");
			return threadLocal.get();
		} else {
			synchronized (threadLocal) {
				logger.info(".............init wbemclient..............");
				client = initWbemClient(pSchema, pHostIp, pHostPort, pNamespace, pUser, pPawd);
				threadLocal.set(client);
			}
			return client;
		}
	}
	public WBEMClient getWbemClient(SmisProvider provider) throws WBEMException{
		return this.getWbemClient(provider.getProtocol(), provider.getHost(), String.valueOf(provider.getPort()), provider.getNamespace(), provider.getUserName(), provider.getPassword());
	}
	public boolean testConnection() throws Exception {
		this.getClient().enumerateInstanceNames(this.buildCIMObjectPath("CIM_ComputerSystem"));
		return true;
	}
	/**
	 * 
	 * @param pSchema
	 *            (http/https)
	 * @param pHostIp
	 * @param pHostPort
	 *            (http:5988/https:5989)
	 * @param pNamespace
	 * @param pUser
	 * @param pPawd
	 * @return
	 * @throws WBEMException
	 */
	private WBEMClient initWbemClient(String pSchema, String pHostIp, String pHostPort, String pNamespace, String pUser, String pPawd) throws WBEMException {
		client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
		cop = new CIMObjectPath(pSchema, pHostIp, pHostPort, pNamespace, null, null);
		Subject subject = new Subject();
		subject.getPrincipals().add(new UserPrincipal(pUser));
		subject.getPrivateCredentials().add(new PasswordCredential(pPawd.toCharArray()));
		client.initialize(cop, subject, null);
		logger.info("WBEMClient has been initialized");
		return client;
	}

	/**
	 * 
	 * @param pObjectName
	 * @return
	 */
	public CIMObjectPath buildCIMObjectPath(String pObjectName) {
		return new CIMObjectPath(cop.getScheme(), cop.getHost(), cop.getPort(), cop.getNamespace(), pObjectName, null);
	}

	/**
	 * Enumerate the instances of a class. The instances of all subclasses are also returned.
	 * @param pObjectName
	 * @return
	 */
	public List<CIMInstance> enumerateInstances(String pObjectName) {
		try {
			CloseableIterator<CIMInstance> iterator = getClient().enumerateInstances(this.buildCIMObjectPath(pObjectName), true, false, false, null);
			try {
				final List<CIMInstance> result = new ArrayList<CIMInstance>();
				while (iterator.hasNext()) {
					final CIMInstance instance = iterator.next();
					result.add(instance);
					logger.debug(Jsr48CimSample.toMof(instance));
				}
				return result;
			} finally {
				iterator.close();
			}
		} catch (WBEMException e) {
			logger.error("EnumerateInstances Error:", e);
			//e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Enumerate the names of the instances for a specified class. The names of all subclass instances are returned.
	 * @param pClassName
	 * @return
	 */
	public List<CIMObjectPath> enumerateInstanceNames(String pClassName){
		try {
			CloseableIterator<CIMObjectPath> iterator = getClient().enumerateInstanceNames(this.buildCIMObjectPath(pClassName));
			try {
				final List<CIMObjectPath> result = new ArrayList<CIMObjectPath>();
				while (iterator.hasNext()) {
					final CIMObjectPath path = iterator.next();
					result.add(path);
					logger.debug(Jsr48CimSample.toMof(path));
				}
				return result;
			} finally {
				iterator.close();
			}
		} catch (WBEMException e) {
			logger.error("EnumerateInstanceNames Error:", e);
		}
		return null;
	}

	/**
	 * 
	 * @param pNamespace
	 * @param pObjectName
	 * @return
	 */
	public List<CIMInstance> enumerateInstances(String pNamespace, String pObjectName) {
		try {
			final CloseableIterator<CIMInstance> iterator = getClient().enumerateInstances(new CIMObjectPath(null, null, null, pNamespace, pObjectName, null), true, false, false, null);
			try {
				final List<CIMInstance> result = new ArrayList<CIMInstance>();
				while (iterator.hasNext()) {
					final CIMInstance instance = iterator.next();
					result.add(instance);
					logger.debug(Jsr48CimSample.toMof(instance));
				}
				return result;
			} finally {
				iterator.close();
			}
		} catch (final WBEMException e) {
			logger.error("EnumerateInstances Error:", e);
		}
		return null;
	}

	/**
	 * 
	 * @param pObjectName： CIMObjectPath defining the source CIM Instance whose associated instances are to be returned. 
	 * The pObjectName must contain the host, namespace, 
	 * object name and keys for the instance
	 * @param pAssociationClass 关联类
	 * @param pResultClass 结果类
	 * @return
	 */
	public List<CIMInstance> associatorInstances(CIMObjectPath pObjectName , String pAssociationClass, String pResultClass,String pRole,String pResultRole) {
		try {
			final CloseableIterator<CIMInstance> associators = getClient().associatorInstances(pObjectName, pAssociationClass, pResultClass, pRole, pResultRole, false, null);
			try {
				final List<CIMInstance> result = new ArrayList<CIMInstance>();
				while (associators.hasNext()) {
					final CIMInstance instance = associators.next();
					result.add(instance);
					logger.debug(Jsr48CimSample.toMof(instance));
				}
				return result;
			} finally {
				associators.close();
			}
		} catch (final WBEMException e) {
			logger.error("AssociatorInstances Error:", e);
		}
		return null;
	}
	/**
	 * 
	 * @param pObjectName： CIMObjectPath defining the source CIM Instance whose associated instances are to be returned. 
	 * The pObjectName must contain the host, namespace, 
	 * object name and keys for the instance
	 * @param pAssociationClass 关联类
	 * @param pResultClass 结果类
	 * @return
	 */
	public List<CIMInstance> associatorInstances(CIMObjectPath pObjectName , String pAssociationClass, String pResultClass) {
		return this.associatorInstances(pObjectName, pAssociationClass, pResultClass, null, null);
	}
	public CIMInstance associatorInstance(CIMObjectPath pObjectName , String pAssociationClass, String pResultClass,String pRole,String pResultRole) {
		CIMInstance instance = null;
		try {
			final CloseableIterator<CIMInstance> associators = getClient().associatorInstances(pObjectName, pAssociationClass, pResultClass, pRole, pResultRole, false, null);
			try {
				while (associators.hasNext()) {
					instance = associators.next();
					logger.debug(Jsr48CimSample.toMof(instance));
				}
			} finally {
				associators.close();
			}
		} catch (final WBEMException e) {
			logger.error("AssociatorInstance Error:", e);
		}
		return instance;
	}
	
	public CIMInstance associatorInstance(CIMObjectPath pObjectName , String pAssociationClass, String pResultClass) {
		return this.associatorInstance(pObjectName, pAssociationClass, pResultClass, null, null);
	}
	
	public List<CIMObjectPath> enumerateClassNames(String namespace){
		try {
			final CloseableIterator<CIMObjectPath> associators = getClient().enumerateClassNames(new CIMObjectPath(null, null, null, (null==namespace||"".equals(namespace.length()))?cop.getNamespace():namespace,null,null), true);
			try {
				final List<CIMObjectPath> result = new ArrayList<CIMObjectPath>();
				while (associators.hasNext()) {
					final CIMObjectPath instance = associators.next();
					result.add(instance);
					logger.debug(Jsr48CimSample.toMof(instance));
				}
				return result;
			} finally {
				associators.close();
			}
		} catch (final WBEMException e) {
			logger.error("EnumerateClassNames Error:", e);
		}
		return null;
	}
	
	public List<CIMClass> enumerateClasses(String className){
		try {
			final CloseableIterator<CIMClass> classes = getClient().enumerateClasses(new CIMObjectPath(null, null, null, null,className,null), false, true, true, true);
			try {
				final List<CIMClass> result = new ArrayList<CIMClass>();
				while (classes.hasNext()) {
					final CIMClass instance = classes.next();
					result.add(instance);
					logger.debug(Jsr48CimSample.toMof(instance));
				}
				return result;
			} finally {
				classes.close();
			}
		} catch (final WBEMException e) {
			logger.error("EnumerateClasses Error:", e);
		}
		return null;
	}
	public String[] queryClassNames(String namespace){
		List<CIMObjectPath> cops = this.enumerateClassNames(namespace);
		//List<String> resultList =  new ArrayList<String>();
		String cop = "";
		String[] items = new String[cops.size()];
		for (int i = 0; i < cops.size(); i++) {
			cop =  cops.get(i).getObjectName();
			items[i] = cop;
		}
//		for (CIMObjectPath cimObjectPath : cops) {
//			cop =  cimObjectPath.getObjectName();
//			resultList.add(cop);
//		}
		//Arrays.sort(resultList.toArray());
		Arrays.sort(items);
		return items;
	}
	public void close() {
		try {
			getClient().close();
			client = null;
			logger.info("WBEMClient session has been closed.");
		} catch (Exception e) {
			logger.error("WBEMClient session closing Error:", e);
		}
	}
	

	public CIMObjectPath getPath() {
		return cop;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CimAPI api = new CimAPI();
			api.getWbemClient("http", "10.20.66.15", "7988", "root/emc", "admin", "#1Password");
//			List<CIMInstance> insts = api.enumerateInstances("CIM_ComputerSystem");
//			for (CIMInstance cimInstance : insts) {
//				System.out.println("名称:" + cimInstance.getPropertyValue("ElementName"));
//			}
			List<CIMObjectPath> insts = api.enumerateClassNames(null);
			for (CIMObjectPath cimObjectPath : insts) {
				System.out.println("名称:" + cimObjectPath.getObjectName());
			}
			api.close();
		} catch (WBEMException e) {
			e.printStackTrace();
		}

	}
}
