package com.peterfranza.staticanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.peterfranza.staticanalysis.tools.AnalysisToolInterface;
import com.peterfranza.staticanalysis.tools.CheckStyleTool;
import com.peterfranza.staticanalysis.tools.FindBugsTool;
import com.peterfranza.staticanalysis.tools.JDependTool;
import com.peterfranza.staticanalysis.tools.PmdTool;

public class Analysis extends Task {

	private List<AnalysisToolInterface> tools = new ArrayList<AnalysisToolInterface>();

	private String pmdRuleSets = "rulesets/favorites.xml";
	private String checkStyleConfig = "libs/checkstyle/checkstyle_checks.xml";
	private String baseFilename = "analysis_";
	private File parent;

	private String jvmArgs;

	public void setBasename(String name) {
		baseFilename = name;
	}

	public void setPmdrulesets(String rulesets) {
		pmdRuleSets = rulesets;
	}

	public void setCheckstyleconfig(File config) {
		checkStyleConfig = config.getAbsolutePath();
	}

	public void setTodir(File parent) {
		this.parent = parent;
	}

	public void setJvmargs(String s) {
		this.jvmArgs = s;
	}

	public String getJvmArgs() {
		return jvmArgs;
	}

	@Override
	public void init() throws BuildException {
		if (parent == null) {
			parent = getProject().getBaseDir();
		}

		createFolder(parent);
		super.init();
	}

	@Override
	public void execute() throws BuildException {

		initializeTools();
		
		long start = System.currentTimeMillis();

		if (AnalysisItem.getAnalysisItems().isEmpty()) {
			System.out.println("Nothing to analyize.");
			return;
		}

		System.out.println("Starting Analysis");
		
		for (AnalysisToolInterface t : tools) {
			t.analyze(this, getProject().createSubProject(), AnalysisItem
					.getAnalysisItems());
		}

		long end = System.currentTimeMillis();
		System.out.println("Analysis took: " + ((end - start) / 1000)
				+ " seconds.");

		super.execute();
	}

	private void initializeTools() {
		tools.add(new CheckStyleTool(
				createReportFileHandle("checkstyle.xml"),
				getProject().resolveFile(checkStyleConfig)));
		
		tools.add(new PmdTool(createReportFileHandle("pmd.xml"),
				pmdRuleSets));

		tools.add(createToolInstance(FindBugsTool.class));
		tools.add(createToolInstance(JDependTool.class));

		// // tools.add(new JavaNCssTool(new File(parent, baseFilename
		// +"ncss.xml")));
	}

	public File getLibraryRoot() {
		return new File(getProject().getProperty("analyzer.unpack_home"));
	}

	public File createReportFileHandle(String name) {
		createFolder(parent);
		File f = new File(parent, baseFilename + name);
		return f;
	}
	
	public static void createFolder(File f) {
		if(!f.exists()) {
			if(!f.mkdirs()) {
				System.err.println("Failed to create: " + f.getAbsolutePath());
			}	
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends AnalysisToolInterface> T createToolInstance(Class<T> s) {
		try {
			return s.newInstance();
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
