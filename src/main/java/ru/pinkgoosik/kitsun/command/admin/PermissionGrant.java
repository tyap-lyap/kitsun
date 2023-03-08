package ru.pinkgoosik.kitsun.command.admin;

public class PermissionGrant {

//	@Override
//	public String getName() {
//		return "permission grant";
//	}
//
//	@Override
//	public String getDescription() {
//		return "Grants a permission to the role.";
//	}
//
//	@Override
//	public String appendArgs() {
//		return " <role id> <permission>";
//	}
//
//	@Override
//	public void respond(CommandUseContext ctx) {
//		String roleId = ctx.args.get(0);
//		String permission = ctx.args.get(1);
//		if(disallowed(ctx, Permissions.PERMISSION_GRANT)) return;
//
//		if(!Permissions.LIST.contains(permission)) {
//			ctx.channel.sendMessageEmbeds(Embeds.error("Such permission doesn't exist.")).queue();
//			return;
//		}
//		ctx.accessManager.grant(roleId, permission);
//		String text = "`" + roleId + "` successfully granted with the `" + permission + "` permission.";
//		ctx.channel.sendMessageEmbeds(Embeds.success("Permission Grating", text)).queue();
//	}
}
