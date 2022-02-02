package com.plymouth.se2assessment2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plymouth.se2assessment2.Constant;
import com.plymouth.se2assessment2.R;
import com.plymouth.se2assessment2.model.Project;
import com.plymouth.se2assessment2.service.ApiClientManager;
import com.plymouth.se2assessment2.service.ProjectService;
import com.plymouth.se2assessment2.service.ThreadService;

import java.io.File;
import java.util.concurrent.ExecutorService;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProjectActivity extends AppCompatActivity {

	private static final String[] PERMISSION_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	private static final int PERMISSION_SELECT_IMAGE = 55;
	private static final int REQUEST_CODE_SELECT_IMAGE = 88;
	private static final String CHANNEL_ID = "edit_channel";

	private ExecutorService executorService;
	private ProjectService projectService;
	private Project project;
	private String selectedImagePath;
	private int notificationId;

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
	private Button btnUploadPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_project);

		this.executorService = ThreadService.getInstance().getService();
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

		this.btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);

		Intent intent = getIntent();
		this.project = (Project) intent.getSerializableExtra("selectedProject");

		fillData(this.project);
		initStoragePermission();

		this.notificationId = 1;
		this.createNotificationChannelForAndroid8OrAbove();
	}

	public void selectPhoto(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK);	// open a app for selecting a file
		intent.setType("image/*");	// only allow image files
		startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(Constant.TAG, "requestCode: " + requestCode);
		Log.i(Constant.TAG, "resultCode: " + resultCode);

		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
				Uri tmpUri = data.getData();

				if (tmpUri != null) {
					final Integer projectId = this.project.getProjectID();
					this.ivPhoto.setImageURI(tmpUri);
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(tmpUri, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					this.selectedImagePath = cursor.getString(columnIndex);
					Log.i(Constant.TAG, "selectedImagePath: " + selectedImagePath);
					cursor.close();

					executorService.submit(new Runnable() {
						@Override
						public void run() {
							File file = new File(selectedImagePath);
							if (!file.exists())
							{
								Log.e(Constant.TAG, "Image is NOT found!");
								Toast.makeText(getApplicationContext(), "Image is NOT found!", Toast.LENGTH_SHORT).show();
							}
							else
							{
								Log.i(Constant.TAG, "Image is uploading...");
								RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
								MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
								RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), "file");

								Call<ResponseBody> call = projectService.uploadPhoto(projectId, fullName, body);
								call.enqueue(new Callback<ResponseBody>() {
									@Override
									public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
										int respCode = response.code();
										Log.d(Constant.TAG, "upload photo status: " + respCode);
										if (respCode == 201)
										{
											Log.i(Constant.TAG, "image is uploaded successfully!");
											Toast.makeText(getApplicationContext(), "Image is uploaded successfully!", Toast.LENGTH_SHORT).show();
										}
										else
										{
											Log.e(Constant.TAG, "failed to upload photo!");
											Toast.makeText(getApplicationContext(), "Failed to upload this photo, please try again!", Toast.LENGTH_SHORT).show();
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
					});
				}
			}
		}
		else {
			Log.e(Constant.TAG, "resultCode is NOT OK!");
		}
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

						// send notification
						Notification notification = new NotificationCompat.Builder(EditProjectActivity.this, CHANNEL_ID)
								.setSmallIcon(R.drawable.app_icon)
								.setContentTitle("Operation Result")
								.setContentText("Your project is updated successfully!")
								.setPriority(NotificationCompat.PRIORITY_HIGH)          // how important this notification is (this param is ignored if API level > 26, the channel importance is used instead)
								.setAutoCancel(true)                                    // remove the notification automatically after clicking it
								.setCategory(NotificationCompat.CATEGORY_MESSAGE)       // if user turn off notification, do NOT disturb him!
								.build();
						NotificationManagerCompat nmc = NotificationManagerCompat.from(EditProjectActivity.this);
						nmc.notify(notificationId, notification);
						notificationId++;


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

					// send notification
					Notification notification = new NotificationCompat.Builder(EditProjectActivity.this, CHANNEL_ID)
							.setSmallIcon(R.drawable.app_icon)
							.setContentTitle("Operation Result")
							.setContentText("The project is deleted successfully!")
							.setPriority(NotificationCompat.PRIORITY_HIGH)          // how important this notification is (this param is ignored if API level > 26, the channel importance is used instead)
							.setAutoCancel(true)                                    // remove the notification automatically after clicking it
							.setCategory(NotificationCompat.CATEGORY_MESSAGE)       // if user turn off notification, do NOT disturb him!
							.build();
					NotificationManagerCompat nmc = NotificationManagerCompat.from(EditProjectActivity.this);
					nmc.notify(notificationId, notification);
					notificationId++;

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

			// if photo has already been uploaded, hide the select-photo button and re-uploaded is NOT allowed
			this.btnUploadPhoto.setVisibility(View.INVISIBLE);
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

	private void initStoragePermission() {
		Log.i(Constant.TAG, "Current API Level: " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT >= 23) {
			// for API level 23 or above
			Log.i(Constant.TAG, "API level is 23 or above, need to ask storage permission from user!");

			int storagePermission = ActivityCompat.checkSelfPermission(EditProjectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (storagePermission != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(EditProjectActivity.this, PERMISSION_STORAGE, PERMISSION_SELECT_IMAGE);
			}
		}
		else {
			Log.i(Constant.TAG, "API level is below 23! Storage permission is granted automatically!");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_SELECT_IMAGE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.i(Constant.TAG, "Permission Granted by user! Local storage can be accessed!");
			}
			else {
				Log.e(Constant.TAG, "Permission Denied by user! Local storage is NOT allowed to access!");
			}
		}
	}

	private void createNotificationChannelForAndroid8OrAbove() {
		// create a NotificationChannel if API level > 26, otherwise the noti
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Edit Channel", NotificationManager.IMPORTANCE_HIGH);
			channel.setDescription("Student's notification channel");
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel);
		}
	}
}