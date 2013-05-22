/**
 * @author Zachary Thompson
 * @author Steve Avery
 */

package edu.santarosa.szcgat.thorn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class GifProcessor extends IntentService {

	Intent curIntent;
	String tempPath;
	String baseFilename;
	String gifPath;
	String thumbnailPath;
	String rotateParam;
	String jpgPath;

	public GifProcessor() {
		super("GifProcessor");
	}

	/*
	 * The intention of the processor service is to handle the native side when
	 * processing the received videos and to issue the filename URI when a new
	 * video is being created.
	 * 
	 * and probably more stuff as we realize what's needed.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		curIntent = intent;

		tempPath = intent.getData().getPath();
		baseFilename = intent.getData().getLastPathSegment()
				.replaceFirst(".mp4", "");
		gifPath = FileManager.THORN_PATH + File.separator + baseFilename
				+ ".gif";
		thumbnailPath = FileManager.THUMBNAIL_PATH + File.separator
				+ baseFilename + ".jpg";

		rotateParam = getRotateParam(tempPath);

		jpgPath = FileManager.TEMP_PATH + File.separator + "output%05d.jpg";

		createJpgFrames();

		encodeGif();

		createThumbnail();

		notifyGallery();

		cleanupTempFolder();

	}

	private void cleanupTempFolder() {
		File tempVideo = new File(tempPath);
		tempVideo.delete();

		for (String jpg : FileManager.getJpgPaths()) {
			File jpgFile = new File(jpg);
			jpgFile.delete();
		}
	}

	private void createThumbnail() {
		String createThumbnailCommand = FileManager.FFMPEG + " -i " + tempPath
				+ " -vcodec mjpeg -vframes 1 -an -f rawvideo -s 512x384 "
				+ rotateParam + thumbnailPath;
		execute(createThumbnailCommand);
	}

	private void execute(String command) {
		try {
			Runtime.getRuntime().exec(command).waitFor();
		}
		catch (InterruptedException e) {
			Log.e("thorn", "InterruptedException", e);
		}
		catch (IOException e) {
			Log.e("thorn", "IOException", e);
		}
	}

	private void createJpgFrames() {
		String createJpgsCommand = FileManager.FFMPEG + " -i " + tempPath
				+ " -r 10 -s 320x240 " + rotateParam + jpgPath;
		execute(createJpgsCommand);
	}

	private void notifyGallery() {
		Messenger messenger = (Messenger) curIntent.getExtras()
				.get("MESSENGER");
		Message msg = Message.obtain();
		msg.obj = baseFilename;

		try {
			messenger.send(msg);
		}
		catch (RemoteException e) {
			Log.e("thorn", "Exception sending message", e);
		}
	}

	private void encodeGif() {
		File gifFile = new File(gifPath);

		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.setRepeat(0);
		encoder.setDelay(100);
		try {
			encoder.start(new FileOutputStream(gifFile));

			for (String jpg : FileManager.getJpgPaths()) {
				encoder.addFrame(BitmapFactory.decodeFile(jpg));
			}
			encoder.finish();

		}
		catch (FileNotFoundException e) {
			Log.d("thorn", "file not found", e);
		}
	}

	private String getRotateParam(String videoPath) {
		int orientation = getOrientation(videoPath);

		switch (orientation) {
		case 90:
			return " -strict -2 -vf transpose=1 ";
		case 180:
			return " -strict -2 -vf hflip,vflip ";
		case 270:
			return " -strict -2 -vf transpose=2 ";
		default:
			return "";
		}
	}

	private int getOrientation(String videoPath) {
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(videoPath);
		String rotation = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
		return Integer.parseInt(rotation);
	}

}