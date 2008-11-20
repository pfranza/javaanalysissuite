package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DirSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;
import com.peterfranza.staticanalysis.tools.tasks.JDependTask;
import com.peterfranza.staticanalysis.tools.tasks.JDependTask.FormatAttribute;

public class JDependTool implements AnalysisToolInterface {
	
	@SuppressWarnings("deprecation")
	public void analyze(Analysis analysis, Project project, List<AnalysisItem> items) {
		JDependTask task = new JDependTask();
		task.setProject(project);
		task.setOutputFile(analysis.createReportFileHandle("jdepend.xml"));
		task.createExclude().setName("java.*");
		task.createExclude().setName("javax.*");

		task.setFormat(new FormatAttribute(){{
			setValue("xml");
		}});
		
		for(AnalysisItem item: items) {
			task.createClassespath().addDirset(createDirSetFromFile(item.getBuildDirectory()));
			task.createClasspath().addDirset(createDirSetFromFile(item.getBuildDirectory()));
			task.createSourcespath().addDirset(createDirSetFromFile(item.getSourceDirectory()));
		}
		
		task.perform();
	}
	
	private DirSet createDirSetFromFile(File f) {
		DirSet d2 = new DirSet();
		d2.setDir(f);
		return d2;
	}

}
