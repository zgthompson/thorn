/**
 * @author Zachary Thompson
 * @author Steve Avery
 */

package edu.santarosa.szcgat.thorn;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ProfileActivity extends FragmentActivity {

	ProfilePagerAdapter mPagerAdapter;
	ViewPager mViewPager;
	private static int curGif = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Create the adapter that will return a fragment the
		// primary sections of the app.
		mPagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.profile_pager);
		mViewPager.setAdapter(mPagerAdapter);

		curGif = getIntent().getExtras().getInt("gif_index");
		mViewPager.setCurrentItem(curGif);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mViewPager.setCurrentItem(curGif);
	}

	@Override
	protected void onPause() {
		super.onPause();
		curGif = mViewPager.getCurrentItem();
	}

	public static int getCurGif() {
		return curGif;
	}

	public class ProfilePagerAdapter extends FragmentPagerAdapter {

		public ProfilePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new GifViewer();
			Bundle bundle = new Bundle();
			bundle.putString("basePath", FileManager.THORN_PATH
					+ File.separator);
			bundle.putString("filename", Gallery.getGifs().get(position)
					.getFilename()
					+ ".gif");
			fragment.setArguments(bundle);

			return fragment;
		}

		@Override
		public int getCount() {
			return Gallery.getGifCount();
		}

	}

}
