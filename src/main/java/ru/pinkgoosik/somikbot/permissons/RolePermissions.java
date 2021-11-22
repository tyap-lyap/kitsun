package ru.pinkgoosik.somikbot.permissons;

import java.util.ArrayList;

public record RolePermissions(String role, boolean registered, ArrayList<String> permissions){}
