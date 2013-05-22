/**
 * @author Zachary Thompson
 * @author Steve Avery
 */

package edu.santarosa.szcgat.thorn;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class GalleryActivity extends FragmentActivity {

	private GalleryPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	private GifCreationHandler gifCreationHandler;

	// OBJECT METHODS

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gallery);

		Gif.loadDatabase(this);
		FileManager.load(this);

		mPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());

		gifCreationHandler = new GifCreationHandler(this);

		mViewPager = (ViewPager) findViewById(R.id.gallery_pager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new Camera.CameraListener(this));

		mViewPager.setCurrentItem(1);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mViewPager.setCurrentItem(1);
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		super.onActivityResult(reqCode, resCode, intent);

		if (reqCode == Camera.NEW_VIDEO) {
			if (resCode == RESULT_OK) {
				Intent process = new Intent(this, GifProcessor.class);
				Messenger messenger = new Messenger(gifCreationHandler);
				process.putExtra("MESSENGER", messenger);
				process.setData(intent.getData());
				process.putExtra("uripath", intent.getDataString());
				startService(process);
			}
			if (resCode == RESULT_CANCELED) {
				Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show();
			}

		}
	}

	public Gallery getGalleryFragment() {
		Gallery gallery = (Gallery) getSupportFragmentManager()
				.findFragmentByTag(getFragmentTag(1));
		return gallery;
	}

	// PRIVATE METHODS

	private String getFragmentTag(int pos) {
		return "android:switcher:" + R.id.gallery_pager + ":" + pos;
	}

	// HELPER CLASSES

	private static class GifCreationHandler extends Handler {
		WeakReference<GalleryActivity> activity;

		GifCreationHandler(GalleryActivity activity) {
			this.activity = new WeakReference<GalleryActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			GalleryActivity curActivity = activity.get();
			Gif.create(msg.obj.toString());
			curActivity.getGalleryFragment().update();
		}
	}

	public class GalleryPagerAdapter extends FragmentPagerAdapter {

		public GalleryPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			if (position == 0) {
				fragment = new Camera();
			}
			else {
				fragment = new Gallery();
			}

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

	}
}
