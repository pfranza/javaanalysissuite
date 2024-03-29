package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement.TypeAttribute;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.ForkMode;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

public class JUnit
extends AbstractAnalysisTool {

	private String includes = "**/*Test.java";

	private String forkMode = ForkMode.ONCE;
	private boolean fork = true;

	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		try {
			JUnitTask task = new JUnitTask();
			task.setProject(project);
			task.init();

			task.createJvmarg().setValue(
					"-Demma.coverage.out.file="
					+ analysis.createReportFileHandle("coverage.emma")
					.getAbsolutePath());

			task.setHaltonfailure(false);
			task.setFork(fork);
			task.setForkMode(new ForkMode(forkMode));
			task.setShowOutput(true);

			FormatterElement format = new FormatterElement();
			format.setType(new TypeAttribute() {
				{
					setValue("xml");
				}
			});

			task.addFormatter(format);

			FileSet cfs = new FileSet();
			cfs.setDir(analysis.getLibraryRoot());
			cfs.setIncludes("**/*.jar");

			Path cp = new Path(project);
			cp.addJavaRuntime();
			cp.addFileset(cfs);

			task.createClasspath().add(cp);

			if (analysis.getAuxRef() != null) {
				task.createClasspath().setRefid(analysis.getAuxRef());
			}

			File results = analysis.createReportFileHandle("test_results");
			results.mkdirs();

			for (AnalysisHolder h : items) {

				if (h.getTestDirectory() != null) {
					BatchTest batch = task.createBatchTest();

					batch.setTodir(results);

					FileSet fs = new FileSet();
					fs.setDir(h.getTestDirectory());
					fs.setIncludes(includes);
					batch.addFileSet(fs);

					task.createClasspath().add(
							new Path(project, h.getBuildDirectory()
									.getAbsolutePath()));

				}

			}

			File coverageFile = analysis
			.createReportFileHandle("coverage.emma");

			// Argument arg1 = task.createJvmarg();
			// arg1.setValue("-Demma.coverage.out.merge=true");

			Argument arg2 = task.createJvmarg();
			arg2.setValue("-Demma.coverage.out.file="
					+ coverageFile.getAbsolutePath());

			task.perform();

			Thread.sleep(500);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public synchronized final String getIncludes() {
		return includes;
	}



	public synchronized final void setFork(boolean fork) {
		this.fork = fork;
	}

	public synchronized final void setForkMode(String forkMode) {
		this.forkMode = forkMode;
	}

	public synchronized final void setIncludes(String includes) {
		this.includes = includes;
	}

}
