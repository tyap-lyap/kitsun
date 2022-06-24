package ru.pinkgoosik.kitsun.permission;

import java.util.ArrayList;

public class RolePermissions {
    public String role;
    public ArrayList<String> permissions;

    public RolePermissions(String role, ArrayList<String> permissions) {
        this.role = role;
        this.permissions = permissions;
    }
}
