package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.util.List;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuildListCommand implements Command {
	
	private static final int itemsPerPage = 10;

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		List<Guild> guilds = e.getJDA().getGuilds();
		int page = 1;
		if (args.length > 0) {
			try {
				page = Integer.valueOf(args[0]);
			} catch (NumberFormatException ex) {}
		}
		page--;
		String guildString = "";
		int startI = itemsPerPage * page;
		int endI = itemsPerPage * (page + 1);
		if (endI >= guilds.size()) {
			endI = guilds.size();
		}
		if (startI >= guilds.size()) {
			e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.noPage", (page + 1))).queue();
			return;
		}
		for (int i = startI; i < endI; i++) {
			Guild g = guilds.get(i);
			guildString += "`" + Language.escape(g.getName()) + "` (" + g.getId() + "): " + g.getOwner().getAsMention() + "\n";
		}
		guildString = guildString.substring(0, guildString.length() - 1);
		EmbedBuilder build = new EmbedBuilder();
		build.setColor(new Color(24, 226, 132));
		build.setTitle(Language.getMessage(e.getGuild().getIdLong(), "utils.botsGuilds", e.getJDA().getSelfUser().getName()));
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.guildsList", (startI + 1), endI), guildString, true);
		build.setFooter(Language.getMessage(e.getGuild().getIdLong(), "main.requestedBy", e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator()), null);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.guildListCommandDescription");
	}

	public boolean isHidden() {
		return true;
	}

}
