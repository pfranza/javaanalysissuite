package com.peterfranza.staticanalysis;

import java.io.File;
import java.io.FileNotFoundException;
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


/**
 * The Class AnalysisDef.
 * @author Peter.Franza
 */
public class AnalysisDef extends Taskdef {

	/** The jar file. */
	private File jarFile;
	
	/** The quiet. */
	private boolean quiet = true;

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.taskdefs.Definer#execute()
	 */
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
			getProject().setNewProperty("analyzer.unpack_home",
					root.getAbsolutePath());

		} catch (IOException e) {
			throw new BuildException(e);
		}

		super.execute();
	}

	/**
	 * Extract file.
	 * 
	 * @param noTimeCompare the no time compare
	 * @param jar the jar
	 * @param file the file
	 * @param f the f
	 * 
	 * @return true, if successful
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException the file not found exception
	 */
	private boolean extractFile(boolean noTimeCompare, JarFile jar,
			JarEntry file, File f) throws IOException, FileNotFoundException {
		print("\t\t" + file.getName() + ":  ");
		if (isCached(file, f)) {
			println(" skipped.");
		} else {
			noTimeCompare = writeFile(noTimeCompare, jar, file, f);
		}
		return noTimeCompare;
	}

	/**
	 * Extract to temp.
	 * 
	 * @param jarFile the jar file
	 * 
	 * @return the file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
				noTimeCompare = extractFile(noTimeCompare, jar, file, f);
			}
		}
		println("\n");

		if (noTimeCompare) {
			System.out
					.println("Can't alter timestamps: this will cause unpack every run.");
		}

		return temp;
	}

	/**
	 * Checks if is cached.
	 * 
	 * @param file the file
	 * @param f the f
	 * 
	 * @return true, if is cached
	 */
	private boolean isCached(JarEntry file, File f) {
		return f.exists() && f.lastModified() == file.getTime()
				&& f.length() == file.getSize();
	}

	/**
	 * Prints the.
	 * 
	 * @param msg the msg
	 */
	private void print(String msg) {
		if (!quiet) {
			System.out.print(msg);
		}
	}

	/**
	 * Println.
	 * 
	 * @param msg the msg
	 */
	private void println(String msg) {
		if (!quiet) {
			System.out.println(msg);
		}
	}

	/**
	 * Sets the jar.
	 * 
	 * @param jarFile the new jar
	 */
	public void setJar(File jarFile) {
		this.jarFile = jarFile;
	}

	/**
	 * Sets the quiet.
	 * 
	 * @param b the new quiet
	 */
	public void setQuiet(boolean b) {
		this.quiet = b;
	}

	/**
	 * Write file.
	 * 
	 * @param noTimeCompare the no time compare
	 * @param jar the jar
	 * @param file the file
	 * @param f the f
	 * 
	 * @return true, if successful
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException the file not found exception
	 */
	private boolean writeFile(boolean noTimeCompare, JarFile jar,
			JarEntry file, File f) throws IOException, FileNotFoundException {
		InputStream is = jar.getInputStream(file); // get the input stream
		FileOutputStream fos = new FileOutputStream(f);
		while (is.available() > 0) {
			fos.write(is.read());
		}
		fos.close();
		is.close();
		if (!f.setLastModified(file.getTime())) {
			noTimeCompare = true;
		}
		println(" extracted.");
		return noTimeCompare;
	}

}