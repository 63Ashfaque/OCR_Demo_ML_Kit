package com.example.ocrdemomlkit;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

public class BasicFunction {

	public void setImage(ImageView imageView, Bitmap bitmap)
	{
		imageView.setImageBitmap(bitmap);
	}

	public void setText(TextView textView, String text)
	{
		textView.setText(text);
	}
}
