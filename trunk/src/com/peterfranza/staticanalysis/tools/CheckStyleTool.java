package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;
import com.puppycrawl.tools.checkstyle.CheckStyleTask;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.Formatter;
import com.puppycrawl.tools.checkstyle.CheckStyleTask.FormatterType;

public class CheckStyleTool extends AbstractAnalysisTool {

	private File report;
	private File configFile;

	public CheckStyleTool(File report, File configFile) {
		this.report = report;
		this.configFile = configFile;
	}
	
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		
		CheckStyleTask task = new CheckStyleTask();
		task.setProject(project);
		
		for(AnalysisItem item: items) {
			List<FileSet> fileSets = getSourceFileSets(item);
			for (FileSet fileSet: fileSets) {
				task.addFileset(fileSet);
			}
		}

		FormatterType type = new FormatterType();
		type.setValue("xml");

		Formatter format = new Formatter();
		format.setType(type);
		format.setTofile(report);
		
		
		task.setConfig(configFile);

		task.setFailOnViolation(false);
		task.addFormatter(format);
		task.perform();
	}


}
