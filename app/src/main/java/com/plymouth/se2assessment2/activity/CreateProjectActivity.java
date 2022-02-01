package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.plymouth.se2assessment2.R;

public class CreateProjectActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_project);
	}

	public void save(View view)
	{
		finish();
	}

	public void cancel(View view)
	{
		finish();
	}

	public void selectPhoto(View view)
	{
		finish();
	}
}