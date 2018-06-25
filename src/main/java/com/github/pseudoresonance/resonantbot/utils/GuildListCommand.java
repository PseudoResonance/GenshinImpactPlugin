package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.util.List;
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
			e.getChannel().sendMessage("There is no page " + (page + 1) + "!").queue();
			return;
		}
		for (int i = startI; i < endI; i++) {
			Guild g = guilds.get(i);
			guildString += "`" + g.getName() + "` (" + g.getId() + "): " + g.getOwner().getAsMention() + "\n";
		}
		guildString = guildString.substring(0, guildString.length() - 1);
		EmbedBuilder build = new EmbedBuilder();
		build.setColor(new Color(24, 226, 132));
		build.setTitle(e.getJDA().getSelfUser().getName() + "'s Guilds");
		build.addField("Guilds " + (startI + 1) + "-" + endI, guildString, true);
		build.setFooter("Requested By " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), null);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc() {
		return "List connected guilds";
	}

	public boolean isHidden() {
		return true;
	}

}
