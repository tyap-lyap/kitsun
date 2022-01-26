package ru.pinkgoosik.kitsun.permission;

import java.util.ArrayList;

public record RolePermissions(String role, boolean registered, ArrayList<String> permissions){}
