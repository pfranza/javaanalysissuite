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
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisDef;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;
import com.peterfranza.staticanalysis.tools.util.DirectoryMD5Sum;

import edu.umd.cs.findbugs.anttask.FindBugsTask;
import edu.umd.cs.findbugs.anttask.UnionBugs;

/**
 * The Class FindBugsTool.
 */
public class FindBugs extends AbstractAnalysisTool {

	private File excludes;
	private String maxMem = "64m";
	private String timeout = String.valueOf(20 * 60 * 1000);
	private String effort = "max";
	private boolean incremental;

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		if(!isIncremental()) {
			performFullFindbugs(analysis, project, items);
		} else {

			List<File> partialFiles = new ArrayList<File>();

			for (AnalysisHolder item : items) {
				if(isPartialFileStale(item)) {
					performPartialFindbugs(analysis, project, item, items);
				}

				cleanStaleFiles(item);
				partialFiles.add(getPartialFileHandle(item));
			}

			File masterFile = analysis.createReportFileHandle("findbugs.xml");
			mergePartialData(project, masterFile, partialFiles);

		}
	}

	private void cleanStaleFiles(AnalysisHolder item) {

		Project p = new Project();
		p.setBaseDir(item.getBuildDirectory());

		FileSet fs = new FileSet();
		fs.setProject(p);
		fs.setDir(item.getBuildDirectory());
		fs.setIncludes("**/findbugs_partial_*.xml.part");

		File currentHandle = getPartialFileHandle(item);

		for (String s : fs.getDirectoryScanner().getIncludedFiles()) {
			File file = p.resolveFile(s);
			if (!file.equals(currentHandle)) {
				if (!AnalysisDef.isQuiet()) {
					System.out.println("   Clean up stale file: " + file);
				}
				file.delete();
			}
		}

	}

	private boolean isPartialFileStale(AnalysisHolder item) {
		return !getPartialFileHandle(item).exists();
	}

	private void performPartialFindbugs(Analysis analysis, Project project,
			AnalysisHolder item, List<AnalysisHolder> items) {

		if (!AnalysisDef.isQuiet()) {
			System.out.println("     Analyzing: " + item.getBuildDirectory());
		}

		FindBugsTask task = new FindBugsTask();
		task.setProject(project);
		task.setHome(new File(analysis.getLibraryRoot(), "findbugs"));

		task.setOutput("xml");
		task.setOutputFile(getPartialFileHandle(item).getAbsolutePath());
		task.setJvmargs("-Xmx" + maxMem);
		task.setTimeout(Long.valueOf(timeout));
		task.setEffort(effort);
		task.setExcludeFilter(excludes);
		
		if(analysis.getFilter() != null) {
			task.setOnlyAnalyze(analysis.getFilter());
		}

		Path auxPath = new Path(project);

		if (analysis.getAuxRef() != null) {
			auxPath.createPath().setRefid(analysis.getAuxRef());
		}

		for (AnalysisHolder h : items) {
			auxPath.createPath().setLocation(h.getBuildDirectory());
		}

		task.setAuxClasspath(auxPath);

		addDirectories(task, item.getSourceDirectory(), item
				.getBuildDirectory());

		task.perform();

	}

	private File getPartialFileHandle(AnalysisHolder item) {
		return new File(item.getBuildDirectory(),
				"findbugs_partial_"
				+ getHash(item) + ".xml.part"
		);
	}

	private String getHash(AnalysisHolder item) {
		Project p = new Project();
		p.setBaseDir(item.getBuildDirectory());

		try {
			return DirectoryMD5Sum.getHashForDirectory(p, item
					.getBuildDirectory(), "**/*.class");
		} catch (Exception e) {
			System.err.println("Unable to compute hash for: "
					+ item.getBuildDirectory());
		}

		return "" + System.currentTimeMillis();

	}

	private void mergePartialData(Project p, File masterFile,
			List<File> partialFiles) {

		UnionBugs union = new UnionBugs();
		union.setProject(p);
		union.setTo(masterFile.getAbsolutePath());

		for (File f : partialFiles) {
			FileSet s = new FileSet();
			s.setProject(p);
			s.setFile(f);

			union.addFileset(s);
		}

		if (!AnalysisDef.isQuiet()) {
			System.out.println("   Merging " + partialFiles.size()
					+ " bug files");
		}

		union.perform();

	}

	private void performFullFindbugs(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		FindBugsTask task = new FindBugsTask();
		task.setProject(project);
		task.setHome(new File(analysis.getLibraryRoot(), "findbugs"));

		task.setOutput("xml");
		task.setOutputFile(analysis.createReportFileHandle("findbugs.xml").getAbsolutePath());
		task.setJvmargs("-Xmx" + maxMem);
		task.setTimeout(Long.valueOf(timeout));
		task.setEffort(effort);
		task.setExcludeFilter(excludes);

		if (analysis.getAuxRef() != null) {
			task.setAuxClasspathRef(analysis.getAuxRef());
		}

		for (AnalysisHolder item : items) {
			if (item.useDirSet()) {
				for(File file: item.getDirectories()) {
					addDirectories(task, file, file);
				}
			} else {
				addDirectories(task, item.getSourceDirectory(), item.getBuildDirectory());
			}
		}

		task.perform();
	}

	private void addDirectories(FindBugsTask task, File src, File build) {
		task.createClass().setLocation(build);
		task.createSourcePath().setLocation(src);
	}

	public File getExcludes() {
		return excludes;
	}

	public void setExcludes(File excludes) {
		this.excludes = excludes;
	}

	public String getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(String maxMem) {
		this.maxMem = maxMem;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getEffort() {
		return effort;
	}

	public void setEffort(String effort) {
		this.effort = effort;
	}

	public final boolean isIncremental() {
		return incremental;
	}

	public final void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	public boolean isForkable() {
		return false;
	}


}
