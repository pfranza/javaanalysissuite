package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import com.google.ant.TestabilityTask;
import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

public class TestabilityExplorer extends AbstractAnalysisTool {

	private String resultsFile = "testabilityexplorer.xml";
	
	
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		
		TestabilityTask task = new TestabilityTask();
			task.setProject(project);
			
			task.setResultFile(analysis.createReportFileHandle(resultsFile).getAbsolutePath());
			task.setErrorFile("System.err");
			task.setPrint("xml");

			for (AnalysisHolder item : items) {
				if(item.useDirSet()) {
					throw new RuntimeException("dirSet not implemented for TestabilityTask .. yet");
				} else {
					task.addClasspath(asPath(project, item.getBuildDirectory()));
				}
			}
			
			task.perform();
		
	}

	private Path asPath(Project project, File buildDirectory) {
		Path path = new Path(project);
			path.setLocation(buildDirectory);
			
		return path;
	}

}
