package ru.pinkgoosik.kitsun.api.mojang;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class VersionManifest {
    public Latest latest = new Latest();
    public ArrayList<Version> versions = new ArrayList<>();

    public static class Latest {
        public String release = "";
        public String snapshot = "";
    }

    public static class Version {
        public String id = "";
        public String type = "";
        public String url = "";
        public String time = "";
        public String releaseTime = "";
        public String sha1 = "";
        public Integer complianceLevel = 0;
    }

}
