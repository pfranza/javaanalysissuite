package com.peterfranza.staticanalysis.tools;

import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;

/**
 * The Interface AnalysisToolInterface.
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
	void analyze(Analysis analysis, Project project, List<AnalysisItem> items);
}
