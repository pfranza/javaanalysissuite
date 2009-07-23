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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

import edu.umd.cs.findbugs.anttask.FindBugsTask;

/**
 * The Class FindBugsTool.
 */
public class FindBugs extends AbstractAnalysisTool {

	private File excludes;
	private String maxMem = "64m";
	private String timeout = String.valueOf(20 * 60 * 1000);
	private String effort = "min";
	private boolean incremental;

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		if(!isIncremental()) {
			performFullFindbugs(analysis, project, items);
		} else {
			
			List<File> partialFiles = new ArrayList<File>();
			
			for (AnalysisHolder item : items) {				
				if(isPartialFileStale(item)) {
					performPartialFindbugs(analysis, project, item, items);
				}
				partialFiles.add(getPartialFileHandle(item));
			}
			
			File masterFile = analysis.createReportFileHandle("findbugs.xml");
			mergePartialData(masterFile, partialFiles);
			
			throw new BuildException("Incremental findbugs not supported .. yet");
		}
	}

	private boolean isPartialFileStale(AnalysisHolder item) {
		// TODO Auto-generated method stub
		//Check to see if bug db is stale
		return false;
	}

	private void performPartialFindbugs(Analysis analysis, Project project,
			AnalysisHolder item, List<AnalysisHolder> items) {
		// TODO Auto-generated method stub
		
	}

	private File getPartialFileHandle(AnalysisHolder item) {
		// TODO Auto-generated method stub
		return null;
	}

	private void mergePartialData(File masterFile, List<File> partialFiles) {
		// TODO Auto-generated method stub
		
	}

	private void performFullFindbugs(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		FindBugsTask task = new FindBugsTask();
		task.setProject(project);
		task.setHome(new File(analysis.getLibraryRoot(), "findbugs"));

		task.setOutput("xml");
		task.setOutputFile(analysis.createReportFileHandle("findbugs.xml").getAbsolutePath());
		task.setJvmargs("-Xmx" + maxMem);
		task.setTimeout(Long.valueOf(timeout));
		task.setEffort(effort);
		task.setExcludeFilter(excludes);

		if (analysis.getAuxRef() != null) {
			task.setAuxClasspathRef(analysis.getAuxRef());
		}

		for (AnalysisHolder item : items) {
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

	public File getExcludes() {
		return excludes;
	}

	public void setExcludes(File excludes) {
		this.excludes = excludes;
	}

	public String getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(String maxMem) {
		this.maxMem = maxMem;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getEffort() {
		return effort;
	}

	public void setEffort(String effort) {
		this.effort = effort;
	}

	public final boolean isIncremental() {
		return incremental;
	}

	public final void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}
	
	

}
