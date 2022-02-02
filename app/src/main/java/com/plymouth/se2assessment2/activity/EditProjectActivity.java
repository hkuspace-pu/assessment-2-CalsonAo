package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.plymouth.se2assessment2.R;
import com.plymouth.se2assessment2.model.Project;

public class EditProjectActivity extends AppCompatActivity {

	private Project project;

	private TextView tbProjectId;
	private TextView tbStudentId;
	private TextView tbFirstName;
	private TextView tbLastName;
	private TextView tbTitle;
	private TextView tbDescription;
	private TextView tbYear;
	private TextView tbThumbnailUrl;
	private TextView tbPosterUrl;
	private ImageView ivPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_project);

		this.tbProjectId = (TextView) findViewById(R.id.tbProjectId);
		this.tbProjectId.setEnabled(false);
		this.tbStudentId = (TextView) findViewById(R.id.tbStudentId);
		this.tbStudentId.setEnabled(false);
		this.tbFirstName = (TextView) findViewById(R.id.tbFirstName);
		this.tbFirstName.setEnabled(false);
		this.tbLastName = (TextView) findViewById(R.id.tbLastName);
		this.tbLastName.setEnabled(false);
		this.tbTitle = (TextView) findViewById(R.id.tbTitle);
		this.tbDescription = (TextView) findViewById(R.id.tbDescription);
		this.tbYear = (TextView) findViewById(R.id.tbYear);
		this.tbThumbnailUrl = (TextView) findViewById(R.id.tbThumbnailUrl);
		this.tbPosterUrl = (TextView) findViewById(R.id.tbPosterUrl);
		this.ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

		Intent intent = getIntent();
		this.project = (Project) intent.getSerializableExtra("selectedProject");

		fillData(this.project);
	}

	public void save(View view)
	{
		finish();
	}

	public void delete(View view)
	{
		finish();
	}

	public void cancel(View view)
	{
		finish();
	}


	private void fillData(Project project)
	{
		String projectId = project.getProjectID().toString();
		this.tbProjectId.setText(projectId);

		String studentId = "";
		if (project.getStudentID() != null) {
			studentId = project.getStudentID().toString();
			this.tbStudentId.setText(studentId);
		}
		String firstName = "";
		if (!TextUtils.isEmpty(project.getFirst_Name())) {
			firstName = project.getFirst_Name();
			this.tbFirstName.setText(firstName);
		}
		String lastName = "";
		if (!TextUtils.isEmpty(project.getSecond_Name())) {
			lastName = project.getSecond_Name();
			this.tbLastName.setText(lastName);
		}
		String title = "";
		if (!TextUtils.isEmpty(project.getTitle())) {
			title = project.getTitle();
			this.tbTitle.setText(title);
		}
		String desc = "";
		if (!TextUtils.isEmpty(project.getDescription())) {
			desc = project.getDescription();
			this.tbDescription.setText(desc);
		}
		String year = "";
		if (project.getYear() != null) {
			year = project.getYear().toString();
			this.tbYear.setText(year);
		}

		String thumbnailURL = "";
		if (!TextUtils.isEmpty(project.getThumbnailURL())) {
			thumbnailURL = project.getThumbnailURL();
			this.tbThumbnailUrl.setText(thumbnailURL);
		}

		String posterURL = "";
		if (!TextUtils.isEmpty(project.getPosterURL())) {
			posterURL = project.getPosterURL();
			this.tbPosterUrl.setText(posterURL);
		}

		// special handling for image
		String photoBase64Str = project.getPhoto();
		if (!TextUtils.isEmpty(photoBase64Str)) {
			byte[] bytes = Base64.decode(photoBase64Str, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			this.ivPhoto.setImageBitmap(bitmap);
		}
	}
}