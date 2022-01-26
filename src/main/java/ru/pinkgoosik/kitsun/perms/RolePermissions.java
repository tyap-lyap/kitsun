package ru.pinkgoosik.kitsun.perms;

import java.util.ArrayList;

public record RolePermissions(String role, boolean registered, ArrayList<String> permissions){}
