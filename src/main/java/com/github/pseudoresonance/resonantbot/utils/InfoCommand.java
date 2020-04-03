package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		Long id = 0L;
		if (e.getChannelType() == ChannelType.PRIVATE)
			id = e.getPrivateChannel().getIdLong();
		else
			id = e.getGuild().getIdLong();
		EmbedBuilder build = new EmbedBuilder();
		User own = e.getJDA().getUserById(Config.getOwner());
		build.setTitle(e.getJDA().getSelfUser().getName());
		build.setColor(new Color(242, 9, 230));
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.uptime"), getUptime(id), true);
		if (own != null)
			build.addField(LanguageManager.getLanguage(id).getMessage("utils.owner"), LanguageManager.escape(own.getName()) + "#" + own.getDiscriminator(), true);
		else
			build.addField(LanguageManager.getLanguage(id).getMessage("utils.owner"), LanguageManager.getLanguage(id).getMessage("main.none"), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.servers"), String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.helpfulLinks"), "[" + LanguageManager.getLanguage(id).getMessage("utils.inviteMe") + "](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.infoCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

	private String getUptime(long id) {
		long start = ManagementFactory.getRuntimeMXBean().getStartTime();
		long now = System.currentTimeMillis();
		long uptime = now - start;
		long up = uptime / 1000;
		long hours = Math.floorDiv(up, 3600);
		long upmin = up % 3600;
		long minutes = Math.floorDiv(upmin, 60);
		long seconds = upmin % 60;
		String min = "00";
		if (minutes != 0) {
			if (minutes < 10) {
				min = "0" + minutes;
			} else {
				min = String.valueOf(minutes);
			}
		}
		String sec = "00";
		if (seconds != 0) {
			if (seconds < 10) {
				sec = "0" + seconds;
			} else {
				sec = String.valueOf(seconds);
			}
		}
		String upString = hours + ":" + min + ":" + sec;
		if (hours >= 24) {
			long days = Math.floorDiv(hours, 24);
			long nHours = hours % 24;
			if (days == 1)
				upString = LanguageManager.getLanguage(id).getMessage("utils.uptimeFormatSingular", days, nHours + ":" + min + ":" + sec);
			else
				upString = LanguageManager.getLanguage(id).getMessage("utils.uptimeFormat", days, nHours + ":" + min + ":" + sec);
		}
		return upString;
	}

}
