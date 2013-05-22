package edu.santarosa.szcgat.thorn;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;

public class Gif {

	public static final int CREATE = 0;
	public static final int DESTROY = 1;

	private long id;
	private String filename;
	private String path;
	private String thumbnail;
	private static ThornDatabase db = null;

	// STATIC METHODS

	public static void loadDatabase(Context context) {
		db = ThornDatabase.getInstance(context);
	}

	public static List<Gif> all() {
		return db.getAllGifs();
	}

	public static Gif create(String filename) {
		return db.addGif(filename);
	}

	public static Gif find(long id) {
		return db.getGif(id);
	}

	public static void destroy(long id) {
		Gif gif = Gif.find(id);
		File gifFile = gif.toFile();
		File thumbFile = gif.getThumbnailFile();
		gifFile.delete();
		thumbFile.delete();
		db.deleteGif(id);
	}

	// METHODS

	public Gif(long id, String filename) {
		this.id = id;
		this.filename = filename;
		this.path = FileManager.THORN_PATH + File.separator + filename + ".gif";
		this.thumbnail = FileManager.THUMBNAIL_PATH + File.separator + filename
				+ ".jpg";
	}

	public void destroy() {
		File gifFile = new File(path);
		File thumbFile = new File(thumbnail);
		gifFile.delete();
		thumbFile.delete();
		db.deleteGif(id);
	}

	public long getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public String getPath() {
		return path;
	}

	public String getThumbnailPath() {
		return thumbnail;
	}

	public File toFile() {
		return new File(path);
	}

	public File getThumbnailFile() {
		return new File(thumbnail);
	}

	public Uri toUri() {
		return Uri.fromFile(new File(path));
	}

	public Uri getThumbnailUri() {
		return Uri.fromFile(new File(thumbnail));
	}

	@Override
	public String toString() {
		return filename;
	}

}
