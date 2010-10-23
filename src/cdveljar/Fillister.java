package cdveljar;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

public class Fillister {
	private static final String[] badfilenames = {};
	/*{"mp3","py","wma","mov","avi","mpg","mpeg","rar","zip","ini","nfo",
			"wav","mp4","wmv","cue","log","gif","mp2","pdf","odt","doc","docx",
			"png","jpeg","ods","xls","bin","iso","img","list","gz","tar","deb",
			"exe","sh","desktop","pps","ppt","bmp","psd","run","arm",
			"txt","m3u","jpg","ogg","flac","m4a","ogg","db","asf"}*/


	public static String[] folderlist(String fname) {
		FileFilter filter = new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		};
		File dir = new File(fname);
		File[] tmp = dir.listFiles(filter);

		String[] returner = new String[tmp.length];
		for (int i = 0; i < returner.length; i++) {
			returner[i] = tmp[i].toString();
		}

		return returner;
	}


	public static String[] dirlist(String fname, boolean medfiler) throws IOException{
		String[] chld;
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".")&checkFileName(name);
			}
			private boolean checkFileName(String filename) {
				String name = filename.toLowerCase();
				boolean bra = true;
				for (int i = 0; i < badfilenames.length; i++) {
					if (name.contains("." +badfilenames[i])) {
						bra = false;
					}
				}
				return bra;
			}			
		};
		File dir = new File(fname);
		if (!medfiler) {
			chld = dir.list(filter);
		}
		else {	
			FilenameFilter filfilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return !name.startsWith(".");
				}
			};
			chld = dir.list(filfilter);
		}
		if(chld == null) {
			throw new IOException();
			/*			JOptionPane.showMessageDialog(null, "Den valde mappa er ei dårleg mappe til formålet. Vel musikk-mappa di.");
			System.exit(0);*/
		}
		else {
			return chld;
		}
	}
}