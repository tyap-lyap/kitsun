package ru.pinkgoosik.kitsun.api.modrinth.entity;

@SuppressWarnings("unused")
public class ProjectFile {
	public Hashes hashes = new Hashes();
	public String url = "";
	public String filename = "";
	public boolean primary = false;
	public int size = 0;

	public static class Hashes {
		public String sha1 = "";
		public String sha512 = "";
	}
}
