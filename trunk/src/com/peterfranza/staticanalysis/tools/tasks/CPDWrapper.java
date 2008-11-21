package com.peterfranza.staticanalysis.tools.tasks;

import java.io.File;
import java.io.FileWriter;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;


/**
 * The Class CPDWrapper.
 * @author Peter.Franza
 */
public class CPDWrapper {
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * 
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		//Usage: <output file> <minimum tokens> ... <Files> 
		final FileWriter fos = new FileWriter(new File(args[0]));
		final Renderer renderer = CPD.getRendererFromString("xml", System.getProperty("file.encoding"));
		final CPD cpd = new CPD(Integer.valueOf(args[1]), (new LanguageFactory()).createLanguage("java"));
		cpd.skipDuplicates();
		cpd.setEncoding(System.getProperty("file.encoding"));
		
		for(int i = 2; i < args.length; i++) {
				cpd.addRecursively(args[i]);
		}

		cpd.go();
		fos.write(renderer.render(cpd.getMatches()));
		fos.close();
	}

}
