package edu.santarosa.szcgat.thorn;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ChooseGifActivity extends Activity {

	List<Gif> gifs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FileManager.load(this);
		Gif.loadDatabase(this);

		setContentView(R.layout.gallery_grid);

		gifs = Gif.all();
		GridView gridView = (GridView) findViewById(R.id.gallery_grid);
		gridView.setAdapter(new Gallery.GalleryArrayAdapter(this,
				R.layout.gallery_image, gifs));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				handleSendGif(gifs.get(position).toUri());
			}
		});
	}

	private void handleSendGif(Uri gifUri) {
		Intent result = new Intent("edu.santarosa.szcgat.thorn.RESULT_ACTION",
				gifUri);
		setResult(Activity.RESULT_OK, result);
		finish();

	}
}
