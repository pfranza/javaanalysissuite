package com.peterfranza.staticanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.peterfranza.staticanalysis.tools.AnalysisToolInterface;
import com.peterfranza.staticanalysis.tools.CheckStyleTool;
import com.peterfranza.staticanalysis.tools.CpdTool;
import com.peterfranza.staticanalysis.tools.FindBugsTool;
import com.peterfranza.staticanalysis.tools.JDependTool;
import com.peterfranza.staticanalysis.tools.PmdTool;

/**
 * The Class Analysis.
 * @author Peter.Franza
 */
public class Analysis extends Task {

	/** The tools. */
	private List<AnalysisToolInterface> tools = new ArrayList<AnalysisToolInterface>();

	/** The pmd rule sets. */
	private String pmdRuleSets = "rulesets/favorites.xml";
	
	/** The check style config. */
	private String checkStyleConfig = "libs/checkstyle/checkstyle_checks.xml";
	
	/** The base filename. */
	private String baseFilename = "analysis_";
	
	/** The parent. */
	private File parent;

	/** The jvm args. */
	private String jvmArgs;

	/**
	 * Sets the basename.
	 * 
	 * @param name the new basename
	 */
	public void setBasename(String name) {
		baseFilename = name;
	}

	/**
	 * Sets the pmdrulesets.
	 * 
	 * @param rulesets the new pmdrulesets
	 */
	public void setPmdrulesets(String rulesets) {
		pmdRuleSets = rulesets;
	}

	/**
	 * Sets the checkstyleconfig.
	 * 
	 * @param config the new checkstyleconfig
	 */
	public void setCheckstyleconfig(File config) {
		checkStyleConfig = config.getAbsolutePath();
	}

	/**
	 * Sets the todir.
	 * 
	 * @param parent the new todir
	 */
	public void setTodir(File parent) {
		this.parent = parent;
	}

	/**
	 * Sets the jvmargs.
	 * 
	 * @param s the new jvmargs
	 */
	public void setJvmargs(String s) {
		this.jvmArgs = s;
	}

	/**
	 * Gets the jvm args.
	 * 
	 * @return the jvm args
	 */
	public String getJvmArgs() {
		return jvmArgs;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#init()
	 */
	@Override
	public void init() throws BuildException {
		if (parent == null) {
			parent = getProject().getBaseDir();
		}

		createFolder(parent);
		super.init();
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {

		initializeTools();
		
		final long start = System.currentTimeMillis();

		if (AnalysisItem.getAnalysisItems().isEmpty()) {
			System.out.println("Nothing to analyize.");
			return;
		}

		System.out.println("Starting Analysis");
		
		for (AnalysisToolInterface t : tools) {
			System.out.println("Running: " + t.getClass().getSimpleName());
			t.analyze(this, getProject().createSubProject(), AnalysisItem
					.getAnalysisItems());
		}

		final long end = System.currentTimeMillis();
		System.out.println("Analysis took: " + ((end - start) / 1000)
				+ " seconds.");

		super.execute();
	}

	/**
	 * Initialize tools.
	 */
	private void initializeTools() {
		tools.add(new CheckStyleTool(
				createReportFileHandle("checkstyle.xml"),
				getProject().resolveFile(checkStyleConfig)));
		
		tools.add(new PmdTool(createReportFileHandle("pmd.xml"),
				pmdRuleSets));
		
		tools.add(createToolInstance(CpdTool.class));
		tools.add(createToolInstance(FindBugsTool.class));
		tools.add(createToolInstance(JDependTool.class));

		// // tools.add(new JavaNCssTool(new File(parent, baseFilename
		// +"ncss.xml")));
	}

	/**
	 * Gets the library root.
	 * 
	 * @return the library root
	 */
	public File getLibraryRoot() {
		return new File(getProject().getProperty("analyzer.unpack_home"));
	}

	/**
	 * Creates the report file handle.
	 * 
	 * @param name the name
	 * 
	 * @return the file
	 */
	public File createReportFileHandle(String name) {
		createFolder(parent);
		return new File(parent, baseFilename + name);
	}
	
	/**
	 * Creates the folder.
	 * 
	 * @param f the f
	 */
	public static void createFolder(File f) {
		if(!f.exists() && !f.mkdirs()) {
			System.err.println("Failed to create: " + f.getAbsolutePath());
		}
	}

	/**
	 * Creates the tool instance.
	 * 
	 * @param s the s
	 * 
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	private <T extends AnalysisToolInterface> T createToolInstance(Class<T> s) {
		try {
			return s.newInstance();
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
