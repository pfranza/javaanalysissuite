package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

public class PmdTool extends AbstractAnalysisTool {

	private File reportFile;
	private String ruleSets;

	public PmdTool(File reportFile, String ruleSets) {
		this.reportFile = reportFile;
		this.ruleSets = ruleSets;
	}
	
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		PMDTask task = new PMDTask();
		task.setProject(project);
		task.setShortFilenames(true);
		task.setRuleSetFiles(ruleSets);
		
		for(AnalysisItem item: items) {
			List<FileSet> fileSets = getSourceFileSets(item);
			for (FileSet fileSet: fileSets) {
				task.addFileset(fileSet);
			}
		}
		
		Formatter format = new Formatter();
			format.setToFile(reportFile);
			format.setType("xml");
			
		task.addFormatter(format);
		task.setFailOnError(false);
		task.setFailOnRuleViolation(false);
		task.perform();
		
	}

}
