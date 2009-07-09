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
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;
import com.peterfranza.staticanalysis.tools.tasks.CPDWrapper.AddMode;

/**
 * The Class CpdTool.
 * 
 * @author Peter.Franza
 */
public class CpdTool extends AbstractAnalysisTool {

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(final Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		final CommandlineJava commandline = new CommandlineJava();
		commandline.setClassname("com.peterfranza.staticanalysis.tools.tasks.CPDWrapper");
		commandline.createClasspath(project).setLocation(new File(analysis.getLibraryRoot() + "/pmd/pmd-4.2.5.jar"));
		commandline.createClasspath(project).setLocation(analysis.getLibraryRoot());
		commandline.createArgument().setFile(analysis.createReportFileHandle("cpd.xml"));
		commandline.createArgument().setValue(analysis.getCpdMinTokens());

		commandline.setMaxmemory(analysis.getMaxMem());

		for (AnalysisHolder item : items) {
			if (item.useDirSet()) {
				commandline.createArgument().setValue(AddMode.FILE.toString());
				for (File file: getSourceFiles(item)) {
					commandline.createArgument().setFile(file);
				}
			} else {
				commandline.createArgument().setValue(AddMode.DIRECTORY.toString());
				commandline.createArgument().setFile(item.getSourceDirectory());
			}
		}

		final Execute exe = new Execute();
		exe.setAntRun(project.createSubProject());
		exe.setCommandline(commandline.getCommandline());

		try {
			exe.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
