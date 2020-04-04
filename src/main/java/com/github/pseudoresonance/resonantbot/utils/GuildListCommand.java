package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildListCommand extends Command {
	
	private static final int itemsPerPage = 10;

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
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
			e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("main.noPage", (page + 1))).queue();
			return;
		}
		for (int i = startI; i < endI; i++) {
			Guild g = guilds.get(i);
			String ownerStr = "";
			try {
				Member owner = g.retrieveOwner().complete();
				ownerStr = owner.getAsMention();
			} catch (Exception ex) {
				ownerStr = LanguageManager.getLanguage(e).getMessage("utils.unknown");
			}
			guildString += "`" + LanguageManager.escape(g.getName()) + "` (" + g.getId() + "): " + ownerStr + "\n";
		}
		guildString = guildString.substring(0, guildString.length() - 1);
		EmbedBuilder build = new EmbedBuilder();
		build.setColor(new Color(24, 226, 132));
		build.setTitle(LanguageManager.getLanguage(e).getMessage("utils.botsGuilds", e.getJDA().getSelfUser().getName()));
		build.addField(LanguageManager.getLanguage(e).getMessage("utils.guildsList", (startI + 1), endI), guildString, true);
		build.setFooter(LanguageManager.getLanguage(e).getMessage("main.requestedBy", e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator()), null);
		e.getChannel().sendMessage(build.build()).queue();
	}

}
