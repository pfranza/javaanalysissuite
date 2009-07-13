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
import org.apache.tools.ant.types.DirSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;
import com.peterfranza.staticanalysis.tools.tasks.JDependTask;
import com.peterfranza.staticanalysis.tools.tasks.JDependTask.FormatAttribute;

/**
 * The Class JDependTool.
 */
public class JDepend extends AbstractAnalysisTool {

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		JDependTask task = new JDependTask();
		task.setProject(project);
		task.setOutputFile(analysis.createReportFileHandle("jdepend.xml"));
		task.createExclude().setName("java.*");
		task.createExclude().setName("javax.*");

		task.setFormat(new FormatAttribute(){{
			setValue("xml");
		}});

		for (AnalysisHolder item : items) {
			if (item.useDirSet()) {
				task.createSourcespath().addDirset(item.getDirSet());
				task.createClassespath().addDirset(item.getDirSet());
				task.createClasspath().addDirset(item.getDirSet());
			} else {
				task.createClassespath().addDirset(createDirSetFromFile(item.getBuildDirectory()));
				task.createClasspath().addDirset(createDirSetFromFile(item.getBuildDirectory()));
				task.createSourcespath().addDirset(createDirSetFromFile(item.getSourceDirectory()));
			}
		}

		task.perform();
	}

	private DirSet createDirSetFromFile(File f) {
		DirSet d2 = new DirSet();
		d2.setDir(f);
		return d2;
	}

}
