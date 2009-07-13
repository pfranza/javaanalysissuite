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

import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem.AnalysisHolder;

/**
 * The Interface AnalysisToolInterface.
 * 
 * @author Peter.Franza
 */
public interface AnalysisToolInterface {

	/**
	 * Analyze.
	 * 
	 * @param analysis the analysis
	 * @param project the project
	 * @param items the items
	 */
	void analyze(Analysis analysis, Project project, List<AnalysisHolder> items);

	/**
	 * Post analyze.
	 * 
	 * @param analysis
	 *            the analysis
	 * @param project
	 *            the project
	 * @param items
	 *            the items
	 */
	void postAnalyze(Analysis analysis, Project project,
			List<AnalysisHolder> items);
}
