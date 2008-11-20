package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

public class JavaNCssTool implements AnalysisToolInterface {

	private File reportFile;

	public JavaNCssTool(File reportFile) {
		this.reportFile = reportFile;
	}
	
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		List<String> arg = new ArrayList<String>();
			arg.add("-xml");
			arg.add("-all");
			arg.add("-recursive");
			arg.add("-out");
			arg.add(reportFile.getAbsolutePath());
			for(AnalysisItem item: items) {
				arg.add(item.getSourceDirectory().getAbsolutePath());
			}

		javancss.Main.main((String[]) arg.toArray(new String[arg.size()]));
	}

}
