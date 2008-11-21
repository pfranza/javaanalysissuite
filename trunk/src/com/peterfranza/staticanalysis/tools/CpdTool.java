package com.peterfranza.staticanalysis.tools;

import java.util.List;

import net.sourceforge.pmd.cpd.CPDTask;
import net.sourceforge.pmd.cpd.CPDTask.FormatAttribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

public class CpdTool implements AnalysisToolInterface {

	public void analyze(Analysis analysis, Project project,
			List<AnalysisItem> items) {

		CPDTask task = new CPDTask();
		task.setProject(project);
		task.setMinimumTokenCount(100);
		task.setFormat(new FormatAttribute(){{
			setValue("xml");
		}});

		for(AnalysisItem item: items) {
			FileSet fs = new FileSet();
			fs.setDir(item.getSourceDirectory());
			fs.setIncludes("**/*.java");		
			task.addFileset(fs);
		}
		
		task.setOutputFile(analysis.createReportFileHandle("cpd.xml"));
		task.perform();
	}

}
