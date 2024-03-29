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
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * The Class AnalysisItem.
 * 
 * @author Peter.Franza
 */
public class AnalysisItem extends Task {

	/** The projects. */
	private static List<AnalysisHolder> Projects = new ArrayList<AnalysisHolder>();

	/**
	 * Adds the analysis item.
	 * 
	 * @param item the item
	 */
	public static synchronized void addAnalysisItem(AnalysisHolder item) {
		Projects.add(item);
	}

	/**
	 * Gets the analysis items.
	 * 
	 * @return the analysis items
	 */
	public static List<AnalysisHolder> getAnalysisItems() {
		return Projects;
	}

	private final AnalysisHolder holder = new AnalysisHolder();


	/**
	 * Sets the srcdir.
	 * 
	 * @param sourceDirectory the new srcdir
	 */
	public void setSrcdir(String sourceDirectory) {
		holder.sourceDirectory = getProject().resolveFile(sourceDirectory);
	}

	/**
	 * Sets the builddir.
	 * 
	 * @param buildDirectory the new builddir
	 */
	public void setBuilddir(String buildDirectory) {
		holder.clsDirectory = getProject().resolveFile(buildDirectory);
	}

	public void setTestdir(String td) {
		holder.testDirectory = getProject().resolveFile(td);
	}

	public void addDirset(DirSet dirSet) {
		// for some reason a null check on dirSet won't work here so we have to
		// keep a boolean tracking calls to this method
		if (holder.dirSetAdded) {
			throw new BuildException("Each analysis item may only have a single dirset");
		}
		holder.dirSetAdded = true;
		holder.dirSet = dirSet;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		super.execute();
		validateInputs();
		addAnalysisItem(holder);
	}

	/**
	 * Ensure that this analysis item has both a sourceDirectory and
	 * buildDirectory OR a directory set.
	 */
	private void validateInputs() {
		if (holder.dirSet != null && (holder.sourceDirectory != null || holder.clsDirectory != null)) {
			throw new BuildException("analysisItem has a dirSet and a srcdir or"
					+ " builddir attribute.  These may not be mixed");
		} else if (holder.dirSet == null && (holder.sourceDirectory == null || holder.clsDirectory == null)) {
			throw new BuildException("analysisITem does not have a nested "
					+ "dirset, then it must have both srcdir and builddir "
					+ "attributes");
		}
	}








	public static class AnalysisHolder {

		/** The source directory. */
		private File sourceDirectory;

		/** The cls directory. */
		private File clsDirectory;

		private File testDirectory;

		/** Both source and cls directories */
		private DirSet dirSet;
		private boolean dirSetAdded;

		private AnalysisHolder() {

		}

		public List<File> getDirectories() {
			List<File> dirs = new ArrayList<File>();
			if (dirSet != null) {
				Iterator<?> it = dirSet.iterator();
				while (it.hasNext()) {
					Object o = it.next();
					if (o instanceof FileResource) {
						File file = ((FileResource) o).getFile();
						dirs.add(file);
					}
				}
			}
			return dirs;
		}

		/**
		 * @return true if the DirSet should be used instead of source and build
		 *         directories
		 */
		public boolean useDirSet() {
			return dirSetAdded;
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

		/**
		 * Gets a combined set of source and build directories.
		 * 
		 * @return the directories
		 */
		public DirSet getDirSet() {
			return dirSet;
		}

		public File getTestDirectory() {
			return testDirectory;
		}

	}

}
