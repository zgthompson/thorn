/**
 * @author Zachary Thompson
 * @author Steve Avery
 */

package edu.santarosa.szcgat.thorn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class GifViewer extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile_gif, container, false);
		WebView webView = (WebView) view.findViewById(R.id.profile_gif);

		String filename = getArguments().getString("filename");
		String basePath = getArguments().getString("basePath");
		webView.loadDataWithBaseURL("file://" + basePath,
				"<html><center><img src=\"" + filename + "\"></html>",
				"text/html", "utf-8", "");

		return view;
	}

}
