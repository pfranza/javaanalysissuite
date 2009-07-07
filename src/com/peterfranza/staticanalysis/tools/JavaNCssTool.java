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
package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

/**
 * The Class JavaNCssTool.
 */
public class JavaNCssTool implements AnalysisToolInterface {

	private File reportFile;

	/**
	 * Instantiates a new java n css tool.
	 * 
	 * @param reportFile the report file
	 */
	public JavaNCssTool(File reportFile) {
		this.reportFile = reportFile;
	}
	
	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		List<String> arg = new ArrayList<String>();
			arg.add("-xml");
			arg.add("-all");
			arg.add("-recursive");
			arg.add("-out");
			arg.add(reportFile.getAbsolutePath());
			for(AnalysisItem item: items) {
				arg.add(item.getSourceDirectory().getAbsolutePath());
			}

		javancss.Main.main((String[]) arg.toArray(new String[arg.size()]));
	}

}
