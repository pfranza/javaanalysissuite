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
package com.peterfranza.staticanalysis.tools.tasks;

import java.io.File;
import java.io.FileWriter;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;


/**
 * The Class CPDWrapper.
 * 
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
		//Usage: <output file> <minimum tokens> <FILE|DIRECTORY> <Files> <FILE|DIRECTORY> <Files> ...  
		final FileWriter fos = new FileWriter(new File(args[0]));
		final Renderer renderer = CPD.getRendererFromString("xml", System.getProperty("file.encoding"));
		final CPD cpd = new CPD(Integer.valueOf(args[1]), (new LanguageFactory()).createLanguage("java"));
		cpd.skipDuplicates();
		cpd.setEncoding(System.getProperty("file.encoding"));
		
		AddMode mode = null;
		for(int i = 2; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals(AddMode.FILE.toString()) || arg.equals(AddMode.DIRECTORY.toString())) {
				mode = AddMode.valueOf(arg);
				i++;
			}
			if (AddMode.FILE.equals(mode)) {
				cpd.add(new File(args[i]));
			} else if (AddMode.DIRECTORY.equals(mode)) {
				cpd.addRecursively(args[i]);	
			} else {
				throw new RuntimeException("no mode set");
			}
		}

		cpd.go();
		fos.write(renderer.render(cpd.getMatches()));
		fos.close();
	}
	
	/**
	 * Used to tell the wrapper if it should add files or directories to the
	 * CPD task.
	 */
	public enum AddMode {
		
		/** Add individual files. */
		FILE,
		
		/** Recursively add directories. */
		DIRECTORY
	}

}
