/**
 * @author Zachary Thompson
 * @author Steve Avery
 */

package edu.santarosa.szcgat.thorn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class Gallery extends Fragment {

	private static List<Gif> gifs = null;
	private static int gifCount = 0;

	private GalleryArrayAdapter adapter;
	private Map<Long, View> selectedGifs;

	// STATIC METHODS

	public static List<Gif> getGifs() {
		if (gifs == null) {
			updateGifsAndCount();
		}
		return gifs;
	}

	public static int getGifCount() {
		if (gifs == null) {
			updateGifsAndCount();
		}
		return gifCount;
	}

	// OBJECT METHODS

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		gifs = Gif.all();
		gifCount = gifs.size();
		adapter = new GalleryArrayAdapter(container.getContext(),
				R.layout.gallery_image, gifs);
		selectedGifs = new HashMap<Long, View>();

		GridView view = (GridView) inflater.inflate(R.layout.gallery_grid,
				container, false);

		view.setAdapter(adapter);

		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (selectedGifs.isEmpty()) {
					Intent intent = new Intent(getActivity(),
							ProfileActivity.class);
					intent.putExtra("gif_index", position);
					getActivity().startActivity(intent);
				}
				else {
					long gifId = gifs.get(position).getId();
					if (selectedGifs.containsKey(gifId)) {
						highlightGif(gifId, view, false);
					}
					else {
						highlightGif(gifId, view, true);
					}
				}
			}
		});

		view.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				long gifId = gifs.get(position).getId();

				if (selectedGifs.containsKey(gifId)) {
					highlightGif(gifId, view, false);
				}
				else {
					highlightGif(gifId, view, true);
				}
				return true;
			}
		});

		this.setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.gallery, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (selectedGifs.isEmpty()) {
			menu.setGroupEnabled(R.id.delete_menu, false);
			menu.setGroupVisible(R.id.delete_menu, false);
			menu.setGroupEnabled(R.id.default_menu, true);
			menu.setGroupVisible(R.id.default_menu, true);
		}

		else {
			menu.setGroupEnabled(R.id.default_menu, false);
			menu.setGroupVisible(R.id.default_menu, false);
			menu.setGroupEnabled(R.id.delete_menu, true);
			menu.setGroupVisible(R.id.delete_menu, true);
			menu.findItem(R.id.total_selected).setTitle(
					selectedGifs.size() + " selected");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.delete):
			for (long gifId : selectedGifs.keySet()) {
				Gif.destroy(gifId);
			}
			update();
			resetDelete();
			break;
		case (R.id.new_gif):
			Camera.openCamera(getActivity());
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (gifCount == 0) {
			Toast.makeText(getActivity(), "Swipe left to create a gif",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		resetDelete();
	}

	public void update() {
		updateGifsAndCount();
		adapter.clear();
		adapter.addAll(gifs);
	}

	// PRIVATE METHODS

	private void resetDelete() {
		for (View view : selectedGifs.values()) {
			view.setBackgroundResource(R.drawable.black_border);
		}
		selectedGifs = new HashMap<Long, View>();
		getActivity().invalidateOptionsMenu();
	}

	private static void updateGifsAndCount() {
		gifs = Gif.all();
		gifCount = gifs.size();
	}

	private void highlightGif(long gifId, View view, boolean highlight) {
		if (highlight) {
			selectedGifs.put(gifId, view);
			view.setBackgroundResource(R.drawable.blue_border);
		}
		else {
			selectedGifs.remove(gifId).setBackgroundResource(
					R.drawable.black_border);
		}

		getActivity().invalidateOptionsMenu();
	}

	// HELPER CLASSES

	public static class GalleryArrayAdapter extends ArrayAdapter<Gif> {

		public GalleryArrayAdapter(Context context, int textViewResourceId,
				List<Gif> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.gallery_image, null);
			}

			ImageView child = (ImageView) view.findViewById(R.id.gallery_image);
			// child.setImageURI(Uri.fromFile(new File(getItem(position)
			// .getThumbnail())));
			child.setImageURI(getItem(position).getThumbnailUri());

			return view;
		}
	}
}
