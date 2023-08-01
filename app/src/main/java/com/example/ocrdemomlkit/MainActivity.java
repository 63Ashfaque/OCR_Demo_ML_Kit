package com.example.ocrdemomlkit;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

	TextView textView;
	TextRecognizer textRecognizer;
	private static final int REQUEST_CAMERA_CODE = 100;
	ImageView imageView;
	BasicFunction basicFunction=new BasicFunction();
	Button btnCopyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = findViewById(R.id.tv);
		imageView = findViewById(R.id.imageView);
		textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

		if (ContextCompat.checkSelfPermission(MainActivity.this,
				android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{
					Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);

		}

		findViewById(R.id.btnPhoto).setOnClickListener(v ->
		{
			if (ContextCompat.checkSelfPermission(MainActivity.this,
					android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{
						Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);

			} else {

//				https://github.com/Dhaval2404/ImagePicker
				ImagePicker.with(this)
						.crop()                    //Crop image(Optional), Check Customization for more option
						//.compress(1024)            //Final image size will be less than 1 MB(Optional)
						//.maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
						//.saveDir(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
						//.galleryOnly()	//User can only select image from Gallery
						//.cameraOnly()	//User can only capture image using Camera
						.start();
			}
		});

		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		btnCopyText =findViewById(R.id.btnCopyText);
		btnCopyText.setOnClickListener(v -> {

			String textToCopy = textView.getText().toString();
			if(textToCopy.isEmpty())
			{
				Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
				return;
			}
			ClipData clipData = ClipData.newPlainText("label", textToCopy);
			clipboardManager.setPrimaryClip(clipData);
			Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Uri imageUri = data.getData();
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					basicFunction.setImage(imageView,bitmap);
				} catch (IOException e) {
					Log.d("Ashu", "Error" + e.getMessage());
				}

				if (imageUri != null) {
					getTextFromImage(imageUri);
				}
			}
		} else {
			Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
		}
	}

	private void getTextFromImage(Uri imageUri) {
		try {
			InputImage inputImage = InputImage.fromFilePath(this, imageUri);

			textRecognizer.process(inputImage)
					.addOnSuccessListener(text -> {

						basicFunction.setText(textView,text.getText());
						StringBuilder stringBuilder = new StringBuilder();

						// Iterate through each recognized text block
						for (Text.TextBlock textBlock : text.getTextBlocks()) {
							// Iterate through each line within the text block
							for (Text.Line line : textBlock.getLines()) {
								stringBuilder.append(line.getText()); // Append line text
								stringBuilder.append("\n"); // Append line break after each line
								Log.d("Ashu", line.getText());
							}
						}
					})
					.addOnFailureListener(e ->
							Toast.makeText(MainActivity.this, "OnFailureListener " + e.getMessage(), Toast.LENGTH_SHORT).show());

		} catch (Exception e) {
			Log.d("Ashu", "Error " + e.getMessage());
		}


	}
}