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
package com.peterfranza.staticanalysis.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

/**
 * Contains some helper methods for extracting FileSets and Files from
 * AnalysisItems.
 * 
 * @author Craig.McIlwee
 */
public abstract class AbstractAnalysisTool extends DataType implements AnalysisToolInterface {

	/**
	 * Get all of the AnalysisItem's FileSets.
	 * 
	 * @param item to create FileSets from
	 * 
	 * @return the source file sets
	 */
	protected List<FileSet> getSourceFileSets(AnalysisHolder item) {
		return getFileSets(item, "java");
	}

	/**
	 * Get all of the AnalysisItem's FileSets.
	 * 
	 * @param item to create FileSets from
	 * 
	 * @return the builds the file sets
	 */
	protected List<FileSet> getBuildFileSets(AnalysisHolder item) {
		return getFileSets(item, "class");
	}

	private List<FileSet> getFileSets(AnalysisHolder item, String extension) {
		List<FileSet> fileSets = new ArrayList<FileSet>();
		if (item.useDirSet()) {
			for (File file: item.getDirectories()) {
				fileSets.add(createFileSet(file, extension, false));
			}
		} else {
			File srcDir = item.getSourceDirectory();
			fileSets.add(createFileSet(srcDir, extension, true));
			File clsDir = item.getBuildDirectory();
			fileSets.add(createFileSet(clsDir, extension, true));
		}
		return fileSets;
	}

	/**
	 * Create a FileSet that includes all .java files.
	 * @param directory that will have all files added
	 * @param recursive true if fileset should be recursive
	 * @return the FileSet
	 */
	private FileSet createFileSet(File directory, String extension, boolean recursive) {
		FileSet fs = new FileSet();
		fs.setDir(directory);
		String pattern = "";
		if (recursive) {
			pattern += "**/";
		}
		pattern += "*." + extension;
		fs.setIncludes(pattern);
		return fs;
	}

	/**
	 * Get all of the AnalysisItem's .java Files.
	 * 
	 * @param item to get Files from
	 * 
	 * @return the source files
	 */
	protected List<File> getSourceFiles(AnalysisHolder item) {
		return getFiles(item, "java");
	}

	/**
	 * Get all of the AnalysisItem's .class Files.
	 * 
	 * @param item to get Files from
	 * 
	 * @return the builds the files
	 */
	protected List<File> getBuildFiles(AnalysisHolder item) {
		return getFiles(item, "class");
	}

	private List<File> getFiles(AnalysisHolder item, String extension) {
		List<File> files = new ArrayList<File>();
		for (File directory: item.getDirectories()) {
			files.addAll(getFilesInDir(directory, extension));
		}
		return files;
	}

	private List<File> getFilesInDir(File directory, String extension) {
		List<File> files = new ArrayList<File>();
		File[] fileArray = directory.listFiles(new ExtensionFilter(extension));
		for (File file: fileArray) {
			files.add(file);
		}
		return files;
	}

	/**
	 * Filters files by extension.
	 */
	private static class ExtensionFilter implements FilenameFilter {
		private final String extension;
		public ExtensionFilter(String extension) {
			this.extension = extension;
		}
		public boolean accept(File dir, String name) {
			return name.endsWith(extension);
		}

	}

}
