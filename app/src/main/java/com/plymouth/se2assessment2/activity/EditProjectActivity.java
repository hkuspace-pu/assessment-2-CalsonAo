package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plymouth.se2assessment2.Constant;
import com.plymouth.se2assessment2.R;
import com.plymouth.se2assessment2.model.Project;
import com.plymouth.se2assessment2.service.ApiClientManager;
import com.plymouth.se2assessment2.service.ProjectService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProjectActivity extends AppCompatActivity {

	private ProjectService projectService;
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

		this.projectService = ApiClientManager.getInstance().getProjectService();

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
		Integer projectId = this.project.getProjectID();
		Project projectToBeUpdated = getUserInput();
		boolean valid = validateProject(projectToBeUpdated);
		if (valid)
		{
			Call<Void> call = projectService.update(projectId, projectToBeUpdated);
			call.enqueue(new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					int respCode = response.code();
					Log.i(Constant.TAG, "update status: " + respCode);
					if (respCode == 204)
					{
						Log.i(Constant.TAG, "project " + projectId + " is updated successfully!");
						Toast.makeText(getApplicationContext(), "Project is updated successfully!", Toast.LENGTH_LONG).show();
						finish();
					}
					else
					{
						Log.e(Constant.TAG, "failed to update project " + projectId);
						Toast.makeText(getApplicationContext(), "Failed to update this project, please try again later!", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call call, Throwable t) {
					Log.e(Constant.TAG, "Error: " + t.toString());
					Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
				}
			});
		}


	}

	public void delete(View view)
	{
		Integer projectId = this.project.getProjectID();
		Call<Void> call = projectService.delete(projectId);
		call.enqueue(new Callback<Void>() {
			@Override
			public void onResponse(Call<Void> call, Response<Void> response) {
				int respCode = response.code();
				Log.i(Constant.TAG, "delete status: " + respCode);
				if (respCode == 204)
				{
					Log.i(Constant.TAG, "project " + projectId + " is deleted successfully!");
					Toast.makeText(getApplicationContext(), "Project " + projectId + " is deleted successfully!", Toast.LENGTH_LONG).show();
					finish();
				}
				else
				{
					Log.e(Constant.TAG, "failed to delete project " + projectId);
					Toast.makeText(getApplicationContext(), "Failed to delete this project, please try again later!", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Call call, Throwable t) {
				Log.e(Constant.TAG, "Error: " + t.toString());
				Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void cancel(View view)
	{
		// go back to previous page
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

	private boolean validateProject(Project project)
	{
		if (project.getStudentID() == null) {
			Toast.makeText(getApplicationContext(), "Please enter your student ID!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (project.getFirst_Name() == null) {
			Toast.makeText(getApplicationContext(), "Please enter your first name!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (project.getSecond_Name() == null) {
			Toast.makeText(getApplicationContext(), "Please enter your last name!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (project.getTitle() == null) {
			Toast.makeText(getApplicationContext(), "Please enter your project title!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (project.getDescription() == null) {
			Toast.makeText(getApplicationContext(), "Please enter the project description!", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (project.getYear() == null) {
			Toast.makeText(getApplicationContext(), "Please enter the project year!", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private Project getUserInput()
	{
		Project project = new Project();
		String projectIdStr = this.tbProjectId.getText().toString();
		Integer projectId = Integer.parseInt(projectIdStr);
		project.setProjectID(projectId);

		String studentIdStr = this.tbStudentId.getText().toString();
		if (!TextUtils.isEmpty(studentIdStr)) {
			Integer studentId = Integer.parseInt(studentIdStr);
			project.setStudentID(studentId);
		}

		String firstName = this.tbFirstName.getText().toString();
		if (!TextUtils.isEmpty(firstName)) {
			project.setFirst_Name(firstName);
		}

		String lastName = this.tbLastName.getText().toString();
		if (!TextUtils.isEmpty(lastName)) {
			project.setSecond_Name(lastName);
		}

		String title = this.tbTitle.getText().toString();
		if (!TextUtils.isEmpty(title)) {
			project.setTitle(title);
		}

		String description = this.tbDescription.getText().toString();
		if (!TextUtils.isEmpty(description)) {
			project.setDescription(description);
		}

		String yearStr = this.tbYear.getText().toString();
		if (!TextUtils.isEmpty(yearStr)) {
			Integer year = Integer.parseInt(yearStr);
			project.setYear(year);
		}

		String thumbnailUrl = this.tbThumbnailUrl.getText().toString();
		if (!TextUtils.isEmpty(thumbnailUrl)) {
			project.setThumbnailURL(thumbnailUrl);
		}

		String posterUrl = this.tbPosterUrl.getText().toString();
		if (!TextUtils.isEmpty(posterUrl)) {
			project.setPosterURL(posterUrl);
		}

		return project;
	}
}