package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;
import com.vladium.emma.emmaTask;
import com.vladium.emma.ant.XFileSet;
import com.vladium.emma.ant.VerbosityCfg.VerbosityAttribute;
import com.vladium.emma.instr.instrTask;
import com.vladium.emma.instr.FilterCfg.filterElement;
import com.vladium.emma.instr.instrTask.ModeAttribute;
import com.vladium.emma.report.reportTask;

public class Emma extends AbstractAnalysisTool {

	private String filter;
	private final List<File> metaDatas = new ArrayList<File>();

	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		emmaTask task = new emmaTask();
		task.setProject(project);
		task.init();
		task.setTaskName("Emma Coverage");
		task.setEnabled(true);

		VerbosityAttribute verbosity = new VerbosityAttribute();
		verbosity.setValue("quiet");
		task.setVerbosity(verbosity);

		for (AnalysisHolder h : items) {

			instrTask instr = (instrTask) task.createInstr();
			instr.setMerge(true);

			ModeAttribute mode = new ModeAttribute();
			mode.setValue("overwrite");
			instr.setMode(mode);

			File metaData = analysis.createReportFileHandle("metadata_"
					+ h.getSourceDirectory().getAbsolutePath().hashCode()
					+ ".emma");

			instr.setMetadatafile(metaData);
			instr.setInstrpath(new Path(project, h.getBuildDirectory().getAbsolutePath()));

			if (getFilter() != null) {
				filterElement filt = instr.createFilter();
				filt.setIncludes(getFilter());
			}

			metaDatas.add(metaData);

		}

		task.perform();

	}

	@Override
	public void postAnalyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		emmaTask task = new emmaTask();
		task.setProject(project);
		task.init();
		task.setTaskName("Emma Report");

		VerbosityAttribute verbosity = new VerbosityAttribute();
		verbosity.setValue("quiet");
		task.setVerbosity(verbosity);

		reportTask report = (reportTask) task.createReport();

		for (File f : metaDatas) {
			report.addInfileset(createFileset(f));
		}

		report.createXml().setOutfile(
				analysis.createReportFileHandle("coverage.xml")
				.getAbsolutePath());

		report.createHtml().setOutfile(
				analysis.createReportFileHandle("coverage.html")
				.getAbsolutePath());


		task.perform();

	}

	private XFileSet createFileset(File f) {
		XFileSet fs = new XFileSet();
		fs.setFile(f);
		return fs;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}

}
