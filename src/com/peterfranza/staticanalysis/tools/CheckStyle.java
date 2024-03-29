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
import org.apache.tools.ant.types.Reference;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;
import com.puppycrawl.tools.checkstyle.CheckStyleTask;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.Formatter;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.FormatterType;

/**
 * The Class CheckStyleTool.
 */
public class CheckStyle extends AbstractAnalysisTool {

	private final String report = "checkstyle.xml";
	private File configFile;
	private Reference classpathRef;


	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		CheckStyleTask task = new CheckStyleTask();
		task.setProject(project);

		for (AnalysisHolder item : items) {
			List<FileSet> fileSets = getSourceFileSets(item);
			for (FileSet fileSet: fileSets) {
				task.addFileset(fileSet);
			}
		}

		FormatterType type = new FormatterType();
		type.setValue("xml");

		Formatter format = new Formatter();
		format.setType(type);
		format.setTofile(analysis.createReportFileHandle(report));


		if (configFile != null) {
			task.setConfig(configFile);
		} else {
			task.setConfig(new File(analysis.getLibraryRoot(),
					"checkstyle/checkstyle_checks.xml").getAbsoluteFile());
		}

		task.setFailOnViolation(false);
		task.addFormatter(format);
		
		if (classpathRef != null) {
			task.setClasspathRef(classpathRef);
		}
		
		task.perform();
	}


	public File getConfig() {
		return configFile;
	}


	public void setConfig(File configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * Set a reference to additional classpath entries.  Used to work around
	 * http://jira.codehaus.org/browse/MPCHECKSTYLE-20.
	 * @param classpathRef to set
	 */
	public void setClasspathRef(Reference classpathRef) {
		this.classpathRef = classpathRef;
	}

}
