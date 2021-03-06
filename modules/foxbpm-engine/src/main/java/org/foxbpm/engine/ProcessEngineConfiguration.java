/**
 * Copyright 1996-2014 FoxBPM ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author kenshin
 * @author ych
 */
package org.foxbpm.engine;

import org.foxbpm.engine.impl.ProcessEngineConfigurationImpl;

public abstract class ProcessEngineConfiguration {

	protected String processEngineName = ProcessEngineManagement.NAME_DEFAULT;
	
	protected ProcessEngineConfiguration() {
		
	}

	public static ProcessEngineConfiguration createProcessEngineConfiguration(String processEngineName) {
		if(processEngineName!=null){
			if(processEngineName.equals(ProcessEngineManagement.NAME_DEFAULT)){
				return new ProcessEngineConfigurationImpl();
			}
			else{
				if(processEngineName.equals(ProcessEngineManagement.NAME_DESIGNER)){
					return new ProcessEngineConfigurationImpl();
				}
			}
		}
		else{
			return new ProcessEngineConfigurationImpl();
		}
		return  new ProcessEngineConfigurationImpl();
	}

	public abstract ProcessEngine buildProcessEngine();

	public String getProcessEngineName() {
		return processEngineName;
	}

	public ProcessEngineConfiguration setProcessEngineName(String processEngineName) {
		this.processEngineName = processEngineName;
		return this;
	}
	
}
