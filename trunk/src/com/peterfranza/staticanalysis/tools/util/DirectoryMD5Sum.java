package com.peterfranza.staticanalysis.tools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class DirectoryMD5Sum {

	public static String getHashForDirectory(Project p, File dir, String pattern)
	throws Exception {

		FileSet fileSet = new FileSet();
		fileSet.setDir(dir);
		fileSet.setIncludes(pattern);

		fileSet.setProject(p);

		List<String> fileStringList = Arrays.asList(fileSet
				.getDirectoryScanner()
				.getIncludedFiles());
		Collections.sort(fileStringList);

		List<File> fileList = new ArrayList<File>();
		for (String s : fileStringList) {
			fileList.add(p.resolveFile(s));
		}

		return computeHashForFileSet(fileList);
	}

	private static String computeHashForFileSet(List<File> fileList)
	throws Exception {

		MessageDigest digest = java.security.MessageDigest.getInstance("MD5");

		StringBuffer buf = new StringBuffer();

		for (File s : fileList) {
			byte[] val = digest.digest(loadFile(s));
			buf.append(getHexString(val));
		}

		return getHexString(digest.digest(buf.toString().getBytes()));
	}

	private static byte[] loadFile(File file) throws Exception {

		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		is.close();
		return bytes;
	}

	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
		(byte) '2',
		(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
		(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
		(byte) 'd', (byte) 'e', (byte) 'f' };

	private static String getHexString(byte[] raw)
	throws UnsupportedEncodingException {
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}

	// public static void main(String[] args) throws Exception {
	//
	// Project p = new Project();
	// p.setBaseDir(new File("classes"));
	//
	// System.out.println(getHashForDirectory(p, new File("classes"),
	// "**/*.class"));
	// }

}
