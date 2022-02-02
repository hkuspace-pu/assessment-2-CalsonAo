package com.plymouth.se2assessment2.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadService {

	private static final ThreadService instance = new ThreadService();

	private ExecutorService executorService;

	private ThreadService()
	{
		this.executorService = Executors.newSingleThreadExecutor();
	}

	public static ThreadService getInstance()
	{
		return instance;
	}

	public ExecutorService getService()
	{
		return this.executorService;
	}
}
