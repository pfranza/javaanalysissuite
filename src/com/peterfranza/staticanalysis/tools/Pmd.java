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

import java.util.List;

import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

/**
 * The Class PmdTool.
 */
public class Pmd extends AbstractAnalysisTool {

	private String reportFile;
	private String rules = "rulesets/favorites.xml";


	public String getRules() {
		return rules;
	}


	public void setRules(String rules) {
		this.rules = rules;
	}


	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		PMDTask task = new PMDTask();
		task.setProject(project);
		task.setShortFilenames(true);
		task.setRuleSetFiles(rules);

		for (AnalysisHolder item : items) {
			List<FileSet> fileSets = getSourceFileSets(item);
			for (FileSet fileSet: fileSets) {
				task.addFileset(fileSet);
			}
		}

		Formatter format = new Formatter();
		format.setToFile(analysis.createReportFileHandle(reportFile).getAbsoluteFile());
		format.setType("xml");

		task.addFormatter(format);
		task.setFailOnError(false);
		task.setFailOnRuleViolation(false);
		task.perform();

	}
	
	

}
