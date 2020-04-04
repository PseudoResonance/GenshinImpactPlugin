package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.util.HashSet;

import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		Long id = 0L;
		if (e.getChannelType() == ChannelType.PRIVATE)
			id = e.getPrivateChannel().getIdLong();
		else
			id = e.getGuild().getIdLong();
		EmbedBuilder build = new EmbedBuilder();
		User own = e.getJDA().getUserById(Config.getOwner());
		build.setTitle(e.getJDA().getSelfUser().getName());
		build.setColor(new Color(242, 9, 230));
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.uptime"), LanguageManager.getLanguage(id).formatTimeAgo(new Timestamp(ManagementFactory.getRuntimeMXBean().getStartTime()), false), true);
		if (own != null)
			build.addField(LanguageManager.getLanguage(id).getMessage("utils.owner"), LanguageManager.escape(own.getName()) + "#" + own.getDiscriminator(), true);
		else
			build.addField(LanguageManager.getLanguage(id).getMessage("utils.owner"), LanguageManager.getLanguage(id).getMessage("main.none"), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.servers"), String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.helpfulLinks"), "[" + LanguageManager.getLanguage(id).getMessage("utils.inviteMe") + "](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

}
