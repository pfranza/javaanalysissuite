package com.peterfranza.staticanalysis.tools.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;

public class CPDWrapper {

	//Usage: <output file> <minimum tokens> ... <Files> 
	public static void main(String[] args) throws Exception {
		FileWriter fos = new FileWriter(new File(args[0]));
		CPD cpd = new CPD(Integer.valueOf(args[1]), (new LanguageFactory()).createLanguage("java"));
			cpd.skipDuplicates();
			cpd.setEncoding(System.getProperty("file.encoding"));
			Renderer renderer = CPD.getRendererFromString("xml", System.getProperty("file.encoding"));

			for(int i = 2; i < args.length; i++) {
				try {
					cpd.addRecursively(args[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			cpd.go();
			fos.write(renderer.render(cpd.getMatches()));
			fos.close();
	}
	
}
