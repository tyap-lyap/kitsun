package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ru.pinkgoosik.kitsun.cache.ServerData;

public class CommandHelper {
	public SlashCommandInteractionEvent event;
	public ServerData serverData;
	public Guild guild;
	public Member member;

	public CommandHelper(SlashCommandInteractionEvent event, ServerData serverData, Guild guild, Member member) {
		this.event = event;
		this.serverData = serverData;
		this.guild = guild;
		this.member = member;
	}

	public void reply(MessageEmbed embed) {
		event.reply(MessageCreateData.fromEmbeds(embed)).queue();
	}

	public void followup(MessageEmbed embed) {
		this.event.getInteraction().getHook().editOriginalEmbeds(embed).queue();
	}

	public void ephemeral(MessageEmbed embed) {
		event.getInteraction().getHook().setEphemeral(true).editOriginalEmbeds(embed).queue();
	}
}
