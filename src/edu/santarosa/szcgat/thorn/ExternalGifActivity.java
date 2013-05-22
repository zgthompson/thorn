package edu.santarosa.szcgat.thorn;

import java.io.File;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

public class ExternalGifActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_external_gif);

		GifViewer gifViewer = new GifViewer();
		File gifFile;

		if (getIntent().getData().toString().startsWith("file://")) {
			gifFile = new File(getIntent().getData().getPath());
		}
		else {
			gifFile = getFileFromUri(getIntent().getData());
		}

		Bundle bundle = new Bundle();
		bundle.putString("basePath", gifFile.getParent() + File.separator);
		bundle.putString("filename", gifFile.getName());

		gifViewer.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, gifViewer).commit();
	}

	private File getFileFromUri(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return new File(cursor.getString(column_index));
	}

}
