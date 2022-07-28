package ru.pinkgoosik.kitsun.util;

import java.io.File;

public class FileUtils {

	public static void createDir(String path) {
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}
}
