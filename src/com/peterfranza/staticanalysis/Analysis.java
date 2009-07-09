/*
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 * 
 * @author peter.franza
 * 
 * Copyright (c) 2009 Open Roads Consulting, Inc. All rights reserved.
 * 
 */
package com.peterfranza.staticanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.peterfranza.staticanalysis.tools.AnalysisToolInterface;
import com.peterfranza.staticanalysis.tools.CheckStyle;
import com.peterfranza.staticanalysis.tools.Cpd;
import com.peterfranza.staticanalysis.tools.FindBugs;
import com.peterfranza.staticanalysis.tools.JDepend;
import com.peterfranza.staticanalysis.tools.Pmd;
import com.peterfranza.staticanalysis.tools.TestabilityExplorer;

/**
 * The Class Analysis.
 * 
 * @author Peter.Franza
 */
public class Analysis extends Task {

	/** The tools. */
	private List<AnalysisToolInterface> tools = new ArrayList<AnalysisToolInterface>();
	
	/** The base filename. */
	private String baseFilename = "analysis_";
	
	/** The parent. */
	private File parent;

	/**
	 * Sets the basename.
	 * 
	 * @param name the new basename
	 */
	public void setBasename(String name) {
		baseFilename = name;
	}

	/**
	 * Sets the todir.
	 * 
	 * @param parent the new todir
	 */
	public void setTodir(File parent) {
		this.parent = parent;
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
	
	public void addCpd(Cpd tool) {
		tools.add(tool);
	}
	
	public void addJDepend(JDepend tool) {
		tools.add(tool);
	}
	
	public void addTestabilityExplorer(TestabilityExplorer tool) {
		tools.add(tool);
	}
	
	public void addPmd(Pmd tool) {
		tools.add(tool);
	}
	
	public void addCheckstyle(CheckStyle tool) {
		tools.add(tool);
	}
	
	public void addFindBugs(FindBugs tool) {
		tools.add(tool);
	}
	
//	public void addJavaNCss(JavaNCss tool) {
//		tools.add(tool);
//	}

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


}
