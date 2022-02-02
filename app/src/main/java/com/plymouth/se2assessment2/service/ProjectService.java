package com.plymouth.se2assessment2.service;

import com.plymouth.se2assessment2.model.Project;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProjectService {
	@GET("students/{projectId}")
	Call<Project> getById(@Path("projectId") int projectId);

	@GET("students")
	Call<List<Project>> getAll();

	@POST("students")
	Call<Void> create(@Body Project project);

	@PUT("students/{projectId}")
	Call<Void> update(@Path("projectId") int projectId, @Body Project project);

	@DELETE("students/{projectId}")
	Call<Void> delete(@Path("projectId") int projectId);

	@Multipart
	@POST("students/{projectId}/image")
	Call<ResponseBody> uploadPhoto(@Path("projectId") int projectId, @Part("photo") RequestBody photo, @Part MultipartBody.Part file);
}
