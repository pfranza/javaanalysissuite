package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

/**
 * The Class CpdTool.
 * @author Peter.Franza
 */
public class CpdTool implements AnalysisToolInterface {

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(final Analysis analysis, Project project,
			List<AnalysisItem> items) {

		final CommandlineJava commandline = new CommandlineJava();
		commandline.setClassname("com.peterfranza.staticanalysis.tools.tasks.CPDWrapper");
		commandline.createClasspath(project).setLocation(new File(analysis.getLibraryRoot() + "/pmd/pmd-4.2.4.jar"));
		commandline.createClasspath(project).setLocation(analysis.getLibraryRoot());
		commandline.createArgument().setFile(analysis.createReportFileHandle("cpd.xml"));
		commandline.createArgument().setValue(analysis.getCpdMinTokens());

		commandline.setMaxmemory(analysis.getMaxMem());

		for(AnalysisItem item: items) {
			commandline.createArgument().setFile(item.getSourceDirectory());
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
