package com.peterfranza.staticanalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class AnalysisDef extends Taskdef {

	private File jarFile;
	private boolean quiet = true;

	public void setJar(File jarFile) {
		this.jarFile = jarFile;
	}
	
	public void setQuiet(boolean b) {
		this.quiet = b;
	}

	@Override
	public void execute() throws BuildException {
		
		try { 
			File root = extractToTemp(jarFile);

			FileSet allJars = new FileSet();
			allJars.setProject(getProject());
			allJars.setDir(root);
			allJars.setIncludes("**/*.jar");

			Path p = new Path(getProject());
			p.setProject(getProject());
			p.addFileset(allJars);
			p.createPathElement().setLocation(jarFile);

			setClasspath(p);
			
			setResource("analysis.properties");
			getProject().setNewProperty("analyzer.unpack_home", root.getAbsolutePath());

		} catch (IOException e) {
			throw new BuildException(e);
		}
		
		super.execute();
	}
	

	private File extractToTemp(File jarFile) throws IOException {
		
		File tempdir = new File(System.getProperty("java.io.tmpdir"));
		File temp = new File(tempdir, "analysis");
		Analysis.createFolder(temp);
		boolean noTimeCompare = false;
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {
			JarEntry file = en.nextElement();
			File f = new File(temp, file.getName());
			if (file.isDirectory()) { // if its a directory, create it
				Analysis.createFolder(f);
			} else {
				print("\t\t" + file.getName()+ ":  ");
				if(f.exists() && f.lastModified() == file.getTime() && f.length() == file.getSize()) {
					println(" skipped.");
				} else {
					InputStream is = jar.getInputStream(file); // get the input stream
					FileOutputStream fos = new FileOutputStream(f);
					while (is.available() > 0) {  // write contents of 'is' to 'fos'
						fos.write(is.read());
					}
					fos.close();
					is.close();
					if(!f.setLastModified(file.getTime())){
						noTimeCompare = true;
					}
					println(" extracted.");
				}
			}
		}
		println("\n");
		
		if(noTimeCompare) {
			System.out.println("Can't alter timestamps: this will cause unpack every run.");
		}
		
		return temp;
	}
	
	private void print(String msg) {
		if(!quiet) {
			System.out.print(msg);
		}
	}
	
	private void println(String msg) {
		if(!quiet) {
			System.out.println(msg);
		}
	}
	
}
