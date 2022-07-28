package ru.pinkgoosik.kitsun.api.modrinth.entity;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class DependencyEntry {
	@SerializedName("version_id")
	public String versionId = "";
	@SerializedName("project_id")
	public String projectId = "";
	@SerializedName("dependency_type")
	public String dependencyType = "";
}
