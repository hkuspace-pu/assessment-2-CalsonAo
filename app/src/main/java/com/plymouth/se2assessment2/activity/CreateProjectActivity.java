package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
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

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProjectActivity extends AppCompatActivity {

	private static final String CHANNEL_ID = "create_channel";

	private ProjectService projectService;
	private int notificationId;

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

		this.notificationId = 1;
		this.createNotificationChannelForAndroid8OrAbove();

//		initTestData();
	}

//	public void initTestData()
//	{
//		this.tbStudentId.setText("203345");
//		this.tbFirstName.setText("Alvin");
//		this.tbLastName.setText("Lin");
//		this.tbTitle.setText("Battle Cat");
//		this.tbDescription.setText("mobile phone");
//		this.tbYear.setText("2022");
//	}

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

						// send notification
						Notification notification = new NotificationCompat.Builder(CreateProjectActivity.this, CHANNEL_ID)
								.setSmallIcon(R.drawable.app_icon)
								.setContentTitle("Operation Result")
								.setContentText("Your project is created successfully!")
								.setPriority(NotificationCompat.PRIORITY_HIGH)          // how important this notification is (this param is ignored if API level > 26, the channel importance is used instead)
								.setAutoCancel(true)                                    // remove the notification automatically after clicking it
								.setCategory(NotificationCompat.CATEGORY_MESSAGE)       // if user turn off notification, do NOT disturb him!
								.build();
						NotificationManagerCompat nmc = NotificationManagerCompat.from(CreateProjectActivity.this);
						nmc.notify(notificationId, notification);
						notificationId++;

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

		if (project.getYear() == null) {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			project.setYear(currentYear);
			Toast.makeText(getApplicationContext(), "You haven't entered the project year yet, it is set to this year (" + currentYear + ") by default.", Toast.LENGTH_SHORT).show();
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

	private void createNotificationChannelForAndroid8OrAbove() {
		// NotificationChannel is required if API level > 26
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Create Channel", NotificationManager.IMPORTANCE_HIGH);
			channel.setDescription("Student's create channel");
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel);
		}
	}
}