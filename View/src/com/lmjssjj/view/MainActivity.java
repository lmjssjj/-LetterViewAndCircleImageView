package com.lmjssjj.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private CircleImageView mCiv1;
	private CircleImageView mCiv2;
	private LetterTileDrawable mLtd;
	private ImageView mIv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mIv = (ImageView) findViewById(R.id.iv);
		initCircleImageView();
	}
	
	private void initCircleImageView(){
		mCiv1 = (CircleImageView) findViewById(R.id.civ1);
		mCiv2 = (CircleImageView) findViewById(R.id.civ2);
		mCiv1.setImageResource(R.drawable.ic_launcher);
		initLetterTileDrawable();
		mCiv2.setImageDrawable(mLtd);
	}
	
	private void initLetterTileDrawable(){
		mLtd = new LetterTileDrawable(getResources());
		mLtd.setContactDetails("aa", "aa");
		mLtd.setIsCircular(true);
		mIv.setImageDrawable(mLtd);
	}

}
