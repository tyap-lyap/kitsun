package ru.pinkgoosik.kitsun.command.member;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.cosmetics.CosmeticsData;
import ru.pinkgoosik.kitsun.cosmetics.FtpConnection;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class RegisterCommands {

	public static CommandNext reg() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "reg";
			}

			@Override
			public String getDescription() {
				return "Links your Discord account and Minecraft nickname.";
			}

			@Override
			public boolean isTLExclusive() {
				return true;
			}

			@Override
			public SlashCommandData build() {
				var data = Commands.slash(getName(), getDescription());
				data.addOption(OptionType.STRING, "nickname", "Your Minecraft nickname.", true);
				return data;
			}

			@Override
			public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
				String name = Objects.requireNonNull(ctx.getOption("nickname")).getAsString().replaceAll("[^a-zA-Z0-9_]", "");
				ctx.deferReply().queue();
				proceed(name, helper);
			}

			public void proceed(String name, CommandHelper helper) {
				var member = helper.event.getMember();

				if(member != null) {
					if(CosmeticsData.getEntry(member.getId()).isPresent()) {
						helper.followup(Embeds.error("You already registered!"));
						return;
					}
					if(CosmeticsData.getEntryByName(name).isPresent()) {
						helper.followup(Embeds.error("Player `" + name + "` is already registered!"));
						return;
					}
					if(MojangAPI.getUuid(name).isPresent()) {
						CosmeticsData.register(member.getId(), name, MojangAPI.getUuid(name).get());
						FtpConnection.updateData();
						helper.followup(Embeds.success("Player Registration", "You've been successfully registered. Use `/unreg` command if you'd like to unregister or change your nickname."));
					}
					else {
						helper.followup(Embeds.error("Player `" + name + "` is not found. Write down your Minecraft username."));
					}
				}
			}
		};
	}

	public static CommandNext unreg() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "unreg";
			}

			@Override
			public String getDescription() {
				return "Unlinks your Discord account and Minecraft nickname.";
			}

			@Override
			public boolean isTLExclusive() {
				return true;
			}

			@Override
			public SlashCommandData build() {
				return Commands.slash(getName(), getDescription());
			}

			@Override
			public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
				ctx.deferReply().queue();
				proceed(helper);
			}

			public void proceed(CommandHelper helper) {
				var member = helper.event.getMember();
				if(member != null) {
					var entry = CosmeticsData.getEntry(member.getId());
					if(entry.isPresent()) {
						CosmeticsData.unregister(member.getId());
						FtpConnection.updateData();
						String text = "Player " + entry.get().user.name + " is successfully unregistered. \nHope to see you soon later!";
						helper.followup(Embeds.success("Player Unregistering", text));
					}
					else {
						helper.followup(Embeds.error("You have not registered yet!"));
					}
				}
			}
		};
	}
}
