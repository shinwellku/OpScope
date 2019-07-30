package com.shinwell.mda.api;

import java.util.ArrayList;
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
public class FabricCimAPI {
	Logger logger =  Logger.getLogger(FabricCimAPI.class.getName());
	CimAPI cimAPI;
	public FabricCimAPI(CimAPI api) {
		this.cimAPI =  api;
	}
	public List<CIMInstance> queryFabric(){
		return cimAPI.enumerateInstances(cimAPI.getPath().getNamespace(), "Brocade_Fabric");
	}
	
	public List<CIMInstance> querySwitch(CIMInstance fabricInstance){
		return cimAPI.associatorInstances(fabricInstance.getObjectPath(), "Brocade_SwitchInFabric", "Brocade_Switch");
	}
	
	public List<CIMInstance> queryModule(CIMInstance switchInstance){
		return cimAPI.associatorInstances(switchInstance.getObjectPath(), "Brocade_PortModuleInSwitch", "Brocade_PortModule");
	}
	
	public List<CIMInstance> queryBlade(CIMInstance moduleInstance){
		return cimAPI.associatorInstances(moduleInstance.getObjectPath(), "Brocade_PortModuleRealizes", "Brocade_Blade");
	}
	public List<CIMInstance> queryPort(CIMInstance moduleInstance){
		return cimAPI.associatorInstances(moduleInstance.getObjectPath(), "Brocade_ModulePort", "Brocade_SwitchFCPort");
	}
	public List<CIMInstance> queryPortWithoutModule(CIMInstance moduleInstance){
		return cimAPI.associatorInstances(moduleInstance.getObjectPath(), "CIM_SystemDevice", "CIM_FCPort");
	}
	public List<CIMInstance> queryPortWithExtendPort(CIMInstance switchInstance){
		List<CIMInstance> instances = new ArrayList<CIMInstance>();
		CIMInstance instance = cimAPI.associatorInstance(switchInstance.getObjectPath(), "Brocade_SwitchInPCS",null,null,null);
		List<CIMInstance> netports = cimAPI.associatorInstances(instance.getObjectPath(), "Brocade_PortInPCS",null);
		for (CIMInstance cimInstance : netports) {
			List<CIMInstance> _instances = cimAPI.associatorInstances(cimInstance.getObjectPath(), "Brocade_SwitchFCPortOfPCSNetworkPort",null);
			instances.addAll(_instances);
		}
		return instances;
	}
	
	public CIMInstance querySwitchSoftware(CIMInstance switchInstance) {
		return cimAPI.associatorInstance(switchInstance.getObjectPath(),"CIM_InstalledSoftwareIdentity", "CIM_SoftwareIdentity","System", "InstalledSoftware");
	}
	
	public CIMInstance querySwitchAccessPoint(CIMInstance switchInstance) {
		return cimAPI.associatorInstance(switchInstance.getObjectPath(),"CIM_HostedAccessPoint", "Brocade_MgmtAccessPoint");
	}
	
	public CIMInstance querySwitchPhysicalPackages(CIMInstance switchInstance) {
		return cimAPI.associatorInstance(switchInstance.getObjectPath(),"CIM_SystemPackaging", "CIM_PhysicalPackage","Dependent", "Antecedent");
	}
	
	public List<CIMInstance> queryPowerSupply(CIMInstance switchInstance){
		CIMInstance instance = cimAPI.associatorInstance(switchInstance.getObjectPath(), "Brocade_SwitchInPCS", null);
		return cimAPI.associatorInstances(instance.getObjectPath(), "Brocade_PowerSupplyInComputerSystem", null);
	}
	public List<CIMInstance> queryFan(CIMInstance switchInstance){
		CIMInstance instance = cimAPI.associatorInstance(switchInstance.getObjectPath(), "Brocade_SwitchInPCS", null);
		return cimAPI.associatorInstances(instance.getObjectPath(), "Brocade_FanInComputerSystem", null);
	}
	public List<CIMInstance> queryZone(CIMInstance fabricInstance){
		return cimAPI.associatorInstances(fabricInstance.getObjectPath(), "Brocade_ZoneInFabric", "Brocade_Zone");
	}
	
	public List<CIMInstance> queryZoneSet(CIMInstance fabricInstance){
		return cimAPI.associatorInstances(fabricInstance.getObjectPath(), "CIM_HostedCollection", "CIM_ZoneSet");
	}
	
	public List<CIMInstance> queryZoneAlias(CIMInstance zoneInstance){
		return cimAPI.associatorInstances(zoneInstance.getObjectPath(), "Brocade_ZoneAliasInZone", "Brocade_ZoneAlias");
	}
	public CIMInstance queryZoneMembersInAlias1(CIMInstance aliasInstance){
		return cimAPI.associatorInstance(aliasInstance.getObjectPath(), "Brocade_ZoneMembershipSettingDataInZoneAlias", null);
	}
	
	public List<CIMInstance> queryAllZoneAlias(CIMInstance fabricInstance) {
		return cimAPI.associatorInstances(fabricInstance.getObjectPath(), null, "Brocade_ZoneAlias");
	}
	
	public List<CIMInstance> queryZoneMembersInAlias(CIMInstance aliasInstance) {
		return cimAPI.associatorInstances(aliasInstance.getObjectPath(), "CIM_ElementSettingData", "CIM_ZoneMembershipSettingData");
	}
	
	public List<CIMInstance> queryBrocadePlatform(CIMInstance fabricInstance) {
		List<CIMInstance> ports = new ArrayList<CIMInstance>();
		List<CIMInstance> ports2 = new ArrayList<CIMInstance>();
		List<CIMInstance> forms = cimAPI.associatorInstances(fabricInstance.getObjectPath(), null, "Brocade_Platform");
		for (CIMInstance f : forms) {
			 List<CIMInstance> ports1 = cimAPI.associatorInstances(f.getObjectPath(), null, "Brocade_NodeFcPort");
			if (ports1 != null && ports1.size() > 0) {
				ports.addAll(ports1);
			}
		}
		ports2 = cimAPI.associatorInstances(fabricInstance.getObjectPath(), null, "Brocade_NodeFcPort");
		ports.addAll(ports2);
		return ports;
	}
	
	public CIMInstance queryStatisticalData(CIMInstance portInstance) {
		//return cimAPI.associatorInstance(portInstance.getObjectPath(),"CIM_ElementStatisticalData", "CIM_FCPortStatistics");
		return cimAPI.associatorInstance(portInstance.getObjectPath(), "CIM_ElementStatisticalData", "Brocade_SwitchFCPortStats");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CimAPI cimAPI = new CimAPI();
		try {
			cimAPI.getWbemClient("http", "10.20.66.15", "6988", "root/brocade1", "admin", "Raysdata@2016");
			FabricCimAPI api = new FabricCimAPI(cimAPI);
			List<CIMInstance> systemList = api.queryFabric();
			for (CIMInstance cimInstance : systemList) {
				System.out.println(cimInstance.getPropertyValue("Name"));
			}
		} catch (WBEMException e) {
			e.printStackTrace();
		}
	}

}
