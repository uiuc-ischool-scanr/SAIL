/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.io.File;

import sentinets.Prediction.outDirIndex;

/**
 * This is a utility class for creating directories and copying folders.
 * @author Shubhanshu
 *
 */
public class Utils {
	public static enum OutDirIndex {FEATURES, ORIGINAL, LABELED, UPDATES, MODELS};
	public static final String[] outDirs = {"Features", "original", "labled", "updates", "models"};
	
	public static String getOutDir(OutDirIndex i){
		return outDirs[i.ordinal()];
	}
	public Utils() {
		// TODO Auto-generated constructor stub
	}
	
	public static void createFolder(String dirName){
		File theDir = new File(dirName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + dirName);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
				System.err.println("Security Exception while creating dir");
				System.err.println(se);
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
	}

}
