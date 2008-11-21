package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

import edu.umd.cs.findbugs.anttask.FindBugsTask;

public class FindBugsTool implements AnalysisToolInterface {

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
			task.createClass().setLocation(item.getBuildDirectory());
			task.createSourcePath().setLocation(item.getSourceDirectory());
		}
		
		task.perform();		
	}

}
