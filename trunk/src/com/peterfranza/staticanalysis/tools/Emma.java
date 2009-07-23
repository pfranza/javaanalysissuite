package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
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

	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}

	}

	private String filter;

	private final List<File> metaDatas = new ArrayList<File>();

	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		for (AnalysisHolder h : items) {

			if (h.getTestDirectory() != null) {

				int ident = h.getSourceDirectory().getAbsolutePath().hashCode();

				File metaData = new File(h.getBuildDirectory(), "metadata_"
						+ ident + ".emma");

				if (!isInstrumented(metaData)) {
					doInstrumentClasses(project, h, metaData);
				}

				File local = analysis.createReportFileHandle("metadata_"
						+ ident + ".emma");
				
				metaDatas.add(local);
				
				try {
					copyFile(metaData, local);
				} catch (IOException e) {
					throw new BuildException(e);
				}

			}

		}

	}

	private XFileSet createFileset(File f) {
		XFileSet fs = new XFileSet();
		fs.setFile(f);
		return fs;
	}

	private void doInstrumentClasses(Project project, AnalysisHolder h,
			File metaData) {
		emmaTask task = new emmaTask();
		task.setProject(project);
		task.init();
		task.setTaskName("Emma Coverage");
		task.setEnabled(true);

		VerbosityAttribute verbosity = new VerbosityAttribute();
		verbosity.setValue("quiet");
		task.setVerbosity(verbosity);

		instrTask instr = (instrTask) task.createInstr();

		instr.setMerge(true);

		ModeAttribute mode = new ModeAttribute();
		mode.setValue("overwrite");
		instr.setMode(mode);

		instr.setMetadatafile(metaData);

		instr.setOutdir(h.getBuildDirectory());

		instr.setInstrpath(new Path(project, h.getBuildDirectory()
				.getAbsolutePath()));

		if (getFilter() != null) {
			filterElement filt = instr.createFilter();
			filt.setIncludes(getFilter());
		}

		task.perform();
	}

	public String getFilter() {
		return filter;
	}

	private boolean isInstrumented(File metaData) {
		return metaData.exists();
	}

	@Override
	public void postAnalyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		emmaTask task = new emmaTask();
		task.setProject(project);
		task.init();
		task.setTaskName("Emma Report");

		VerbosityAttribute verbosity = new VerbosityAttribute();
		verbosity.setValue("quiet");
		task.setVerbosity(verbosity);

		reportTask report = (reportTask) task.createReport();

		File hand = analysis.createReportFileHandle("coverage.emma");
		report.addInfileset(createFileset(hand));

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

		for (File f : metaDatas) {
			f.deleteOnExit();
		}

		hand.deleteOnExit();

	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
