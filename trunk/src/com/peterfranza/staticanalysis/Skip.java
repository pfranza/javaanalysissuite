/*
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 * 
 * @author peter.franza
 * 
 * Copyright (c) 2009 Open Roads Consulting, Inc. All rights reserved.
 * 
 */
package com.peterfranza.staticanalysis;

import org.apache.tools.ant.types.DataType;

import com.peterfranza.staticanalysis.tools.AnalysisToolInterface;

/**
 * The Class Skip.
 */
public class Skip extends DataType{

	private String tool;

	/**
	 * Gets the tool.
	 * 
	 * @return the tool
	 */
	public synchronized final String getTool() {
		return tool;
	}

	/**
	 * Sets the tool.
	 * 
	 * @param tool the new tool
	 */
	public synchronized final void setTool(String tool) {
		this.tool = tool;
	}

	/**
	 * Should skip.
	 * 
	 * @param t the t
	 * 
	 * @return true, if successful
	 */
	public boolean shouldSkip(AnalysisToolInterface t) {
		return tool.equalsIgnoreCase(t.getClass().getSimpleName());
	}
	
}
