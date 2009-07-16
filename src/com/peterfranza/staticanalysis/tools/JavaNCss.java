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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

/**
 * The Class JavaNCssTool.
 */
public class JavaNCss extends AbstractAnalysisTool {

	private final String reportFile = "javancss.xml";

	private String level = "package";

	/* (non-Javadoc)
	 * @see com.peterfranza.staticanalysis.tools.AnalysisToolInterface#analyze(com.peterfranza.staticanalysis.Analysis, org.apache.tools.ant.Project, java.util.List)
	 */
	public void analyze(Analysis analysis, Project project,
			List<AnalysisHolder> items) {
		try {
			List<String> arg = new ArrayList<String>();
			String s = System.getProperty("file.separator");
			arg.add(System.getProperty("java.home") + s + "bin" + s + "java");
			arg.add("-classpath");
			arg.add(createClassPathString(analysis));

			arg.add(javancss.Main.class.getName());
			arg.add("-xml");
			arg.add("-" + level);
			arg.add("-recursive");
			arg.add("-out");
			arg.add(analysis.createReportFileHandle(reportFile)
					.getAbsolutePath());

			for (AnalysisHolder item : items) {
				arg.add(item.getSourceDirectory().getAbsolutePath());
			}

			// javancss.Main.main(arg.toArray(new String[arg.size()]));

			ProcessBuilder pb = new ProcessBuilder(arg);
			pb.redirectErrorStream(true);

			Process process = pb.start();

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			// System.out.println("Output of running %s is:" +
			// Arrays.toString(arg));
			while ((line = br.readLine()) != null) {
				System.out.println(" >> " + line);
			}



			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e.getMessage());
		}
	}

	public String createClassPathString(Analysis p) {
		final FileSet allJars = new FileSet();
		allJars.setProject(p.getProject());
		allJars.setDir(p.getLibraryRoot());
		allJars.setIncludes("**/*.jar");

		String[] files = allJars.getDirectoryScanner().getIncludedFiles();

		StringBuffer buf = new StringBuffer();
		String sep = System.getProperty("path.separator");

		List<String> list = Arrays.asList(files);
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = iterator.next();

			buf.append(p.getLibraryRoot().getAbsolutePath());
			buf.append(System.getProperty("file.separator"));
			buf.append(string);

			if (iterator.hasNext()) {
				buf.append(sep);
			}

		}

		return System.getProperty("java.class.path") + sep + buf.toString();
	}

	public synchronized final String getLevel() {
		return level;
	}

	public synchronized final void setLevel(String level) {

		if (level.equalsIgnoreCase("package")) {
			this.level = "package";
		} else if (level.equalsIgnoreCase("object")) {
			this.level = "object";
		} else if (level.equalsIgnoreCase("function")) {
			this.level = "function";
		} else if (level.equalsIgnoreCase("all")) {
			this.level = "all";
		} else {
			System.out.println("Error setting level acceptable "
					+ "values are: package, object, function, all");
			throw new BuildException("error setting javancss level");
		}

	}

}
