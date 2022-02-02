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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.plymouth.se2assessment2.R;
import com.plymouth.se2assessment2.model.Project;

public class ListProjectActivity extends AppCompatActivity {

	private ListView lvProjectList;
	private Project[] projects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_project);

		Log.i("hw2", "create project list page");

		this.lvProjectList = (ListView) findViewById(R.id.listview);

		AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Log.i("hw2", "position: " + position);
				Project selectedProject = projects[position];
				Log.i("hw2", "[" + position + "]" + selectedProject.toString());

				Intent intent = new Intent(ListProjectActivity.this, EditProjectActivity.class);
				intent.putExtra("selectedProject", selectedProject);
				startActivity(intent);
			}
		};
		this.lvProjectList.setOnItemClickListener(listener);

		retrieveProjects();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("hw2", "resume project list page");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("hw2", "restart project list page");
	}

	private void retrieveProjects()
	{
		Project p1 = new Project();
		p1.setProjectID(8251);
		p1.setStudentID(10350220);
		p1.setFirst_Name("Amy");
		p1.setSecond_Name("Choi");
		p1.setTitle("Biohazard 3");
		p1.setDescription("Defeat all zombies!");
		p1.setYear(2008);


		Project p2 = new Project();
		p2.setProjectID(9631);
		p2.setStudentID(20109201);
		p2.setFirst_Name("Bob");
		p2.setSecond_Name("Li");
		p2.setTitle("Dino Crisis 2");
		p2.setDescription("They are come, run, run!!!");
		p2.setYear(2002);

		projects = new Project[] {p1, p2};

		ListAdapter adapter = new ArrayAdapter<Project>(this,
				android.R.layout.simple_list_item_2,
				android.R.id.text1,
				projects) {

			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

				View view = super.getView(position, convertView, parent);
				Project project = getItem(position);

				Log.i("hw2", "position: " + position);
				Log.i("hw2", "project: " + project.toString());

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
				return view;
			}
		};

		this.lvProjectList.setAdapter(adapter);
	}
}