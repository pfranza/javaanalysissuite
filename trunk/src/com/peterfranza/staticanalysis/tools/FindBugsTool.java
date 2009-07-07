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

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

import edu.umd.cs.findbugs.anttask.FindBugsTask;

/**
 * The Class FindBugsTool.
 */
public class FindBugsTool implements AnalysisToolInterface {

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		FindBugsTask task = new FindBugsTask();
		task.setProject(project);
		task.setHome(new File(analysis.getLibraryRoot(), "findbugs"));	
	
		task.setOutput("xml");
		task.setOutputFile(analysis.createReportFileHandle("findbugs.xml").getAbsolutePath());
		task.setJvmargs("-Xmx" + analysis.getMaxMem());
		task.setTimeout(20 * 60 * 1000);
		task.setEffort("min");
		
		
		for(AnalysisItem item: items) {
			if (item.useDirSet()) {
				for(File file: item.getDirectories()) {
					addDirectories(task, file, file);
				}
			} else {
				addDirectories(task, item.getSourceDirectory(), item.getBuildDirectory());
			}
		}
		
		task.perform();		
	}

	private void addDirectories(FindBugsTask task, File src, File build) {
		task.createClass().setLocation(build);
		task.createSourcePath().setLocation(src);
	}
	
}
