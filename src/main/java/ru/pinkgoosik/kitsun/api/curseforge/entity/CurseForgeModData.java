package ru.pinkgoosik.kitsun.api.curseforge.entity;

@SuppressWarnings("unused")
public class CurseForgeModData {
	public int id = 0;
	public String name = "";
	public String slug = "";

	public CurseForgeModLinks links = new CurseForgeModLinks();
	public String summary = "";
	public int status = 0;
	public int downloadCount = 0;

	public String getStringId() {
		return Integer.toString(id);
	}
}
