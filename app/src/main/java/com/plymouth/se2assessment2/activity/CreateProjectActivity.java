package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class CreateProjectActivity extends AppCompatActivity {

	private ProjectService projectService;

	private TextView tbStudentId;
	private TextView tbFirstName;
	private TextView tbLastName;
	private TextView tbTitle;
	private TextView tbDescription;
	private TextView tbYear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_project);

		this.projectService = ApiClientManager.getInstance().getProjectService();

		this.tbStudentId = (TextView) findViewById(R.id.tbStudentId);
		this.tbFirstName = (TextView) findViewById(R.id.tbFirstName);
		this.tbLastName = (TextView) findViewById(R.id.tbLastName);
		this.tbTitle = (TextView) findViewById(R.id.tbTitle);
		this.tbDescription = (TextView) findViewById(R.id.tbDescription);
		this.tbYear = (TextView) findViewById(R.id.tbYear);

//		initTestData();
	}

	public void initTestData()
	{
		this.tbStudentId.setText("203345");
		this.tbFirstName.setText("Alvin");
		this.tbLastName.setText("Lin");
		this.tbTitle.setText("Battlecat");
		this.tbDescription.setText("mobile phone");
		this.tbYear.setText("2022");
	}

	public void save(View view)
	{
		Project project = getUserInput();
		Log.i(Constant.TAG, "new project: " + project.toString());
		boolean valid = validateProject(project);
		if (valid) {
			Call<Void> call = projectService.create(project);
			call.enqueue(new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					int respCode = response.code();
					Log.i(Constant.TAG, "create status: " + respCode);
					if (respCode == 201)
					{
						Log.i(Constant.TAG, "project is created successfully!");
						Toast.makeText(getApplicationContext(), "Congratulations! Your project is created successfully!", Toast.LENGTH_LONG).show();
						finish();
					}
					else
					{
						Log.e(Constant.TAG, "failed to create project!");
						Toast.makeText(getApplicationContext(), "Failed to create your project, please try again !", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call call, Throwable t) {
					Log.i(Constant.TAG, "Error: " + t.toString());
					Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public void backToList(View view)
	{
		finish();
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

		return project;
	}
}