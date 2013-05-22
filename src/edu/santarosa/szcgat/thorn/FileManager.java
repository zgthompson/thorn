package edu.santarosa.szcgat.thorn;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileManager {

	public static final String THORN_PATH = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
			+ File.separator + "thorn";
	public static final String TEMP_PATH = THORN_PATH + File.separator + "tmp";
	public static final String THUMBNAIL_PATH = THORN_PATH + File.separator
			+ "thumbnails";
	public static String FFMPEG = null;
	private static Context context = null;

	public static void load(Context curContext) {
		context = curContext;
		FFMPEG = context.getFilesDir().getAbsolutePath() + File.separator
				+ "ffmpeg";
	}

	public static List<String> getJpgPaths() {
		File folder = new File(TEMP_PATH);
		File[] listOfJpgs = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.getAbsolutePath().endsWith(".jpg")) {
					return true;
				}
				else {
					return false;
				}
			}
		});

		List<String> jpgPaths = new ArrayList<String>();

		for (File jpg : listOfJpgs) {
			jpgPaths.add(jpg.getAbsolutePath());
		}

		return jpgPaths;
	}

	public static boolean mediaMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static void ensureFileStructure() {
		File thornDir = new File(THORN_PATH);
		File thornTmpDir = new File(TEMP_PATH);
		File thornThmbDir = new File(THUMBNAIL_PATH);
		File nomedia = new File(THORN_PATH, ".nomedia");
		File ffmpeg = new File(FFMPEG);

		// Create the storage directory if it does not exist

		if (!thornDir.exists()) {
			createDir(thornDir);
		}

		if (!thornTmpDir.exists()) {
			createDir(thornTmpDir);
		}

		if (!thornThmbDir.exists()) {
			createDir(thornThmbDir);
		}

		if (!nomedia.exists()) {
			createFile(nomedia);
		}

		if (!ffmpeg.exists()) {
			copyFfmpeg();
		}

		if (!ffmpeg.canExecute()) {
			ffmpeg.setExecutable(true);
		}
	}

	private static void copyFfmpeg() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = context.getAssets().open("ffmpeg");
			out = new FileOutputStream(FFMPEG);

			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		}
		catch (IOException e) {
			Log.e("thorn", "Failed to copy ffmpeg", e);
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private static void createDir(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.d("thorn", "failed to create directory");
			}

		}
	}

	private static void createFile(File file) {
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					Log.d("thorn", "failed to create file");
				}
			}
			catch (IOException e) {
				Log.d("thorn", "createFile", e);
			}
		}
	}
}
