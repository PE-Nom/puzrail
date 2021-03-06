package com.pentech.puzrail.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.pentech.puzrail.R;

/**
 * Created by takashi on 2017/08/19.
 */

public class AboutFragment extends Fragment {
    String url = "file:///android_asset/default.html";

    public void setUrl(String url){
        this.url = url;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.page_about, container, false);
        WebView wv = (WebView) view.findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(url);
        return view;
    }
}
