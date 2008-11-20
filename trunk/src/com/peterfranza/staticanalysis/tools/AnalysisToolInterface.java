package com.peterfranza.staticanalysis.tools;

import java.util.List;

import org.apache.tools.ant.Project;

import com.peterfranza.staticanalysis.Analysis;
import com.peterfranza.staticanalysis.AnalysisItem;


public interface AnalysisToolInterface {
	void analyze(Analysis analysis, Project project, List<AnalysisItem> items);
}
