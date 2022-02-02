package com.plymouth.se2assessment2.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientManager {
	private static ApiClientManager instance = new ApiClientManager();

	private ProjectService projectService;

	private ApiClientManager() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://web.socem.plymouth.ac.uk/COMP2000/api/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		this.projectService = retrofit.create(ProjectService.class);
	}

	public static ApiClientManager getInstance() {
		return instance;
	}

	public ProjectService getProjectService() {
		return this.projectService;
	}
}
