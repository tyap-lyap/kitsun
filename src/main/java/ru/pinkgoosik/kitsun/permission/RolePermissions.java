package ru.pinkgoosik.kitsun.permission;

import java.util.ArrayList;

public record RolePermissions(String role, ArrayList<String> permissions){}
