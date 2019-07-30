package com.shinwell.mda.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @Project_name MockDebugAssistant
 * @Author: Shinwell Ku
 * @Date: 2017年8月15日下午5:40:07
 * @Version: 1.0
 * @Desc:
 */
public class FabricModel {
	String fabricName;
	Object fabricData;
	List<SwitchModel> switchList = new ArrayList<SwitchModel>();
	public String getFabricName() {
		return fabricName;
	}
	public void setFabricName(String fabricName) {
		this.fabricName = fabricName;
	}
	public Object getFabricData() {
		return fabricData;
	}
	public void setFabricData(Object fabricData) {
		this.fabricData = fabricData;
	}
	public List<SwitchModel> getSwitchList() {
		return switchList;
	}
	public void setSwitchList(List<SwitchModel> switchList) {
		this.switchList = switchList;
	}

	
}
