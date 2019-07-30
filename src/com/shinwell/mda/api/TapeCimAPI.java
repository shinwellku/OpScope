package com.shinwell.mda.api;

import java.util.List;

import javax.cim.CIMInstance;

import org.apache.log4j.Logger;


/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2018年10月9日
 * @Version: 1.0
 * @Desc:
 */
public class TapeCimAPI {
	Logger logger =  Logger.getLogger(TapeCimAPI.class.getName());
	CimAPI cimAPI;
	public TapeCimAPI(CimAPI api) {
		this.cimAPI =  api;
	}
	
	public List<CIMInstance> queryComputerSystem(){
		return cimAPI.enumerateInstances("CIM_ComputerSystem");
	}
	
	public List<CIMInstance> queryRemoteAccessPoint(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_HostedAccessPoint","CIM_RemoteServiceAccessPoint");
	}
	
	public List<CIMInstance> queryTapeImformation(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(),"CIM_SAPAvailableForElement", "CIM_RemoteServiceAccessPoint","ManagedElement", "AvailableSAP");
	}
	
	public List<CIMInstance> querySoftwareIdentity(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_InstalledSoftwareIdentity","CIM_SoftwareIdentity");
	}
	
	public List<CIMInstance> queryChangerDevice(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice","CIM_ChangerDevice", "GroupComponent", "PartComponent");
	}
	
	public List<CIMInstance> queryTapeDrive(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice","CIM_TapeDrive", "GroupComponent", "PartComponent");
	}
	
	public List<CIMInstance> queryFCPorts(CIMInstance instance) {
		return cimAPI.associatorInstances(instance.getObjectPath(), "CIM_SystemDevice","CIM_FCPort", "GroupComponent", "PartComponent");
	}
	
	public List<CIMInstance> getPhysicalTape(CIMInstance instance) {
		return cimAPI.enumerateInstances(cimAPI.getPath().getNamespace(), "CIM_PhysicalTape");
	}
}
