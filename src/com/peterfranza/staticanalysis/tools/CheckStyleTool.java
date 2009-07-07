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
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;
import com.puppycrawl.tools.checkstyle.CheckStyleTask;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.Formatter;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.FormatterType;

/**
 * The Class CheckStyleTool.
 */
public class CheckStyleTool extends AbstractAnalysisTool {

	private File report;
	private File configFile;

	/**
	 * Instantiates a new check style tool.
	 * 
	 * @param report the report
	 * @param configFile the config file
	 */
	public CheckStyleTool(File report, File configFile) {
		this.report = report;
		this.configFile = configFile;
	}
	
	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		
		CheckStyleTask task = new CheckStyleTask();
		task.setProject(project);
		
		for(AnalysisItem item: items) {
			List<FileSet> fileSets = getSourceFileSets(item);
			for (FileSet fileSet: fileSets) {
				task.addFileset(fileSet);
			}
		}

		FormatterType type = new FormatterType();
		type.setValue("xml");

		Formatter format = new Formatter();
		format.setType(type);
		format.setTofile(report);
		
		
		task.setConfig(configFile);

		task.setFailOnViolation(false);
		task.addFormatter(format);
		task.perform();
	}


}
