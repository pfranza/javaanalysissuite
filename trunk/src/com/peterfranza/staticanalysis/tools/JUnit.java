package com.peterfranza.staticanalysis.tools;

import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

public class JUnit extends AbstractAnalysisTool {

	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
	
		try {
			JUnitTask task = new JUnitTask();
		
			
			
			task.perform();
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
