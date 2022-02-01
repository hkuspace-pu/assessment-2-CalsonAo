package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.plymouth.se2assessment2.R;

public class HomeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	public void create(View view)
	{
		Intent intent = new Intent(this, CreateProjectActivity.class);
		startActivity(intent);
	}

	public void list(View view)
	{
		Intent intent = new Intent(this, ListProjectActivity.class);
		startActivity(intent);
	}
}