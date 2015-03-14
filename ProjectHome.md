A single ant task that will subject your code to junit, emma,
findbugs, checkstyle, pmd, jDepend, TestabilityExplorer & javaNcss without all the fuss of setting up each of these tools.


More Information @ http://www.peterfranza.com/projects/janalysissuite-a-static-analysis-toolkit/

**Usage:**
```
<target name="analyze" depends="compile">
 
   <!-- define the task set -->
   <property name="jar.name" value="jAnalysisSuite-{latest}.jar">
   <taskdef resource="staticanalysis.properties" classpath="${jar.name}"/>               
   <setupAnalysis jar="${jar.name}" quiet="true"/>
 
   <!-- add individual parts of the project to the batch -->
   <!-- do this as many times as you want for each of the different -->
   <!-- modules you wish to analyze                          -->
   <addAnalysisItem srcdir="${src.dir}"  builddir="${classes.dir}"/>
   <addanalysisitem srcdir="${src2.dir}" builddir="${classes2.dir}">
   <addanalysisitem srcdir="${src3.dir}" builddir="${classes3.dir}" testdir="${unittest.dir}">
 
	        <performStaticAnalysis  todir="reports" basename="report_">
	        	<checkstyle/>
	        	<cpd/>
	        	<jdepend />
	        	<testabilityexplorer />
	        	<pmd/>
                        <javancss/>
	        	<findbugs />
	        	<emma filter="com.peterfranza.*"/>
	        	<JUnit />	        	
	        </performStaticAnalysis>
 
</target>
```

The tools each support a set of configurable paramaters that can override the defaults

### Checkstyle ###
|Parameter|Value|
|:--------|:----|
|config|_path to file_|

### CPD ###
|Parameter|Value|
|:--------|:----|
|mintokens|_integer_|
|maxmemory|_integer_|

### Testability Explorer ###
|Parameter|Value|
|:--------|:----|
|fiter|_package regex to remove_|

### PMD ###
|Parameter|Value|
|:--------|:----|
|rules|_"rulesets/favorites.xml"_|


### Findbugs ###
|Parameter|Value|
|:--------|:----|
|maxmem|_integer + size_ i.e. 64m|
|timeout|_integer_|
|effort|_string_|
|excludes|_path to file_|

### Emma ###
|Parameter|Value|
|:--------|:----|
|filter|_package regex_ i.e. com.peterfranza.`*`|

### JUnit ###
|Parameter|Value|
|:--------|:----|
|includes|_file regex_ i.e. `*``*`/`*`.Test.java|

### JavaNCss ###
|Parameter|Value|
|:--------|:----|
|level| 'package', 'object', 'function', 'all' (default: package)|