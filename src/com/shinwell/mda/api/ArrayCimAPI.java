/**
 * 
 */
package com.shinwell.mda.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.WBEMException;

import org.apache.log4j.Logger;


/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年10月9日
 * @Version: 1.0
 * @Desc:
 */
public class ArrayCimAPI {
	Logger logger =  Logger.getLogger(ArrayCimAPI.class.getName());
	CimAPI cimAPI;
	public ArrayCimAPI(CimAPI api) {
		this.cimAPI =  api;
	}
	public List<CIMInstance> queryComputerSystem(){
		return cimAPI.enumerateInstances("CIM_ComputerSystem");
	}
	public List<CIMInstance> queryController(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_ComponentCS", "CIM_ComputerSystem");
	}
	public List<CIMInstance> queryPool(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_HostedStoragePool", "CIM_StoragePool");
	}
	public List<CIMInstance> queryDisk(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_DiskDrive");
	}
	public CIMInstance queryStorageExtent(CIMInstance instance){
		return cimAPI.associatorInstance(instance.getObjectPath(), "CIM_MediaPresent", null);
	}
	public List<CIMInstance> queryVolume(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_StorageVolume");
	}
	public List<CIMInstance> queryPort(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_FCPort");
	}
	public List<CIMInstance> queryPower(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_PowerSupply");
	}
	public List<CIMInstance> queryFan(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_Fan");
	}
	public List<CIMInstance> queryBattery(CIMInstance instance){
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice", "CIM_Battery");
	}
	public CIMInstance queryPhysicalPackage(CIMInstance instance){
		return cimAPI.associatorInstance(instance.getObjectPath(), "CIM_SystemPackaging", "CIM_PhysicalPackage");
	}
	public CIMInstance querySoftwareIdentity(CIMInstance instance){
		CIMInstance _instance = cimAPI.associatorInstance(instance.getObjectPath(), "CIM_ElementSoftwareIdentity", "CIM_SoftwareIdentity");
		if (null==_instance) {
			_instance = cimAPI.associatorInstance(instance.getObjectPath(), "CIM_InstalledSoftwareIdentity", "CIM_SoftwareIdentity");
		}
		return _instance;
	}
	public CIMInstance queryDiskPhysicalPackage(CIMInstance diskInstance){
		return cimAPI.associatorInstance(diskInstance.getObjectPath(), "CIM_Realizes", "CIM_PhysicalPackage");
	}
	
	public CIMInstance queryStatisticalData(CIMInstance instance){
		return cimAPI.associatorInstance(instance.getObjectPath(), "CIM_ElementStatisticalData", null);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CimAPI cimAPI = new CimAPI();
		try {
			cimAPI.getWbemClient("http", "192.168.1.104", "5988", "root/ibm", "administrator", "password");
			ArrayCimAPI api = new ArrayCimAPI(cimAPI);
			List<CIMInstance> systemList = api.queryComputerSystem();
			for (CIMInstance cimInstance : systemList) {
				List<CIMInstance> controllers = api.queryController(cimInstance);
				for (CIMInstance controller : controllers) {
					List<CIMInstance> ports = api.queryPort(controller);
				}
			
				List<CIMInstance> pools = api.queryPool(cimInstance);
				List<CIMInstance> disks = api.queryDisk(cimInstance);
				List<CIMInstance> volumes = api.queryVolume(cimInstance);
			}
		} catch (WBEMException e) {
			e.printStackTrace();
		}

	}

}
