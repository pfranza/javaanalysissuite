package com.peterfranza.staticanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * The Class AnalysisItem.
 * @author Peter.Franza
 */
public class AnalysisItem extends Task {

	/** The projects. */
	private static List<AnalysisItem> projects = new ArrayList<AnalysisItem>();

	/**
	 * Adds the analysis item.
	 * 
	 * @param item the item
	 */
	public static void addAnalysisItem(AnalysisItem item) {
		projects.add(item);
	}

	/**
	 * Gets the analysis items.
	 * 
	 * @return the analysis items
	 */
	public static List<AnalysisItem> getAnalysisItems() {
		return projects;
	}

	/** The source directory. */
	private File sourceDirectory;
	
	/** The cls directory. */
	private File clsDirectory;

	/**
	 * Sets the srcdir.
	 * 
	 * @param sourceDirectory the new srcdir
	 */
	public void setSrcdir(String sourceDirectory) {
		this.sourceDirectory = getProject().resolveFile(sourceDirectory);
	}

	/**
	 * Sets the builddir.
	 * 
	 * @param buildDirectory the new builddir
	 */
	public void setBuilddir(String buildDirectory) {
		this.clsDirectory = getProject().resolveFile(buildDirectory);
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		super.execute();
		addAnalysisItem(this);
	}

	/**
	 * Gets the source directory.
	 * 
	 * @return the source directory
	 */
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	/**
	 * Gets the builds the directory.
	 * 
	 * @return the builds the directory
	 */
	public File getBuildDirectory() {
		return clsDirectory;
	}
	
}
