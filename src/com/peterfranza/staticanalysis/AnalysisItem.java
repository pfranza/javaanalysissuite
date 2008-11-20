package com.peterfranza.staticanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AnalysisItem extends Task {

	private static List<AnalysisItem> projects = new ArrayList<AnalysisItem>();
	public static void addAnalysisItem(AnalysisItem item) {
		projects.add(item);
	}
	
	public static List<AnalysisItem> getAnalysisItems() {
		return projects;
	}

	private File sourceDirectory;
	private File clsDirectory;

	public void setSrcdir(String sourceDirectory) {
		this.sourceDirectory = getProject().resolveFile(sourceDirectory);
	}
	
	public void setBuilddir(String buildDirectory) {
		this.clsDirectory = getProject().resolveFile(buildDirectory);
	}
	
	@Override
	public void execute() throws BuildException {
		super.execute();
		addAnalysisItem(this);
	}

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public File getBuildDirectory() {
		return clsDirectory;
	}
	
}
