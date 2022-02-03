package com.plymouth.se2assessment2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plymouth.se2assessment2.Constant;
import com.plymouth.se2assessment2.R;
import com.plymouth.se2assessment2.model.Project;
import com.plymouth.se2assessment2.service.ApiClientManager;
import com.plymouth.se2assessment2.service.ProjectService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProjectActivity extends AppCompatActivity {

	private ProjectService projectService;
	private Project[] projects;

	private ListView projectListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_project);

		Log.i(Constant.TAG, "init project list page");
		this.projects = new Project[0];
		this.projectService = ApiClientManager.getInstance().getProjectService();
		this.projectListView = (ListView) findViewById(R.id.listview);

		///////////////////////////////////////
		// set onclick listener for listview
		///////////////////////////////////////
		AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Project selectedProject = projects[position];
				Log.i(Constant.TAG, "[" + position + "]" + selectedProject.toString());

				Intent intent = new Intent(ListProjectActivity.this, EditProjectActivity.class);
				intent.putExtra("selectedProject", selectedProject);
				startActivity(intent);
			}
		};
		this.projectListView.setOnItemClickListener(listener);

		retrieveProjects();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(Constant.TAG, "resume project list page");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(Constant.TAG, "restart project list page");
		retrieveProjects();
	}

	public void backToHome(View view)
	{
		finish();
	}

	private void retrieveProjects()
	{
		Call<List<Project>> call = this.projectService.getAll();
		call.enqueue(new Callback<List<Project>>() {
			@Override
			public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
				List<Project> projectList = response.body();
				if (projectList == null)
				{
					projects = new Project[0];
					Log.i(Constant.TAG, "No projects found!");
					Toast.makeText(getApplicationContext(), "No projects found!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Log.i(Constant.TAG, "total num of projects: " + projectList.size());
					projects = new Project[projectList.size()];
					projectList.toArray(projects);
				}

				setListViewAdapter(projects);
			}

			@Override
			public void onFailure(Call call, Throwable t) {
				Log.d(Constant.TAG, "Error: " + t.toString());
				Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setListViewAdapter(Project[] projectArray) {
		///////////////////////////////////////
		// set adapter for listview
		///////////////////////////////////////
		ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this,
				android.R.layout.simple_list_item_2,
				android.R.id.text1,
				projectArray) {

			@Override
			public View getView(int position, View convertView, @NonNull ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				Project project = getItem(position);
				if (project != null)
				{
					Log.i(Constant.TAG, "[" + position + "] " + project.toString());

					if (!TextUtils.isEmpty(project.getTitle())) {
						String projectInfo = project.getTitle() + " (" + project.getProjectID() + ")";
						TextView tv1 = view.findViewById(android.R.id.text1);
						tv1.setText(projectInfo);
					}

					TextView tv2 = view.findViewById(android.R.id.text2);
					StringBuilder sb = new StringBuilder();
					if (!TextUtils.isEmpty(project.getFirst_Name())){
						sb.append(project.getFirst_Name());
					}
					if (!TextUtils.isEmpty(project.getSecond_Name())){
						sb.append(" " + project.getSecond_Name());
					}
					tv2.setText(sb.toString().trim());
				}

				return view;
			}
		};

		this.projectListView.setAdapter(adapter);
	}
}