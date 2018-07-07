package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.PluginManager;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatsCommand implements Command {
	
	private OperatingSystemMXBean osb = ManagementFactory.getOperatingSystemMXBean();

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		EmbedBuilder build = new EmbedBuilder();
		User own = e.getJDA().getUserById(Config.getOwner());
		build.setTitle(e.getJDA().getSelfUser().getName());
		build.setColor(new Color(77, 0, 153));
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.uptime"), getUptime(e.getGuild().getIdLong()), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.owner"), Language.escape(own.getName()) + "#" + own.getDiscriminator(), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.ramUsage"), getRam(), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.cpuUsage"), getCpu(e.getGuild().getIdLong()), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.pluginsLoaded"), String.valueOf(PluginManager.getPlugins().size()), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.commandsLoaded"), String.valueOf(CommandManager.getCommands().size()), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.javaVersion"), System.getProperty("java.version"), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.javaVendor"), System.getProperty("java.vendor"), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.servers"), String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField(Language.getMessage(e.getGuild().getIdLong(), "utils.helpfulLinks"), "[" + Language.getMessage(e.getGuild().getIdLong(), "utils.inviteMe") + "](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.statsCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

	private String getCpu(long guildID) {
		double load = osb.getSystemLoadAverage();
		return load >= 0 ? load + "%" : Language.getMessage(guildID, "utils.unknown");
	}

	private String getRam() {
		Runtime run = Runtime.getRuntime();
		long total = run.totalMemory();
		long mem = total - run.freeMemory();
		BigDecimal memF = new BigDecimal(String.valueOf(mem / 1048576.0)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalF = new BigDecimal(String.valueOf(total / 1048576.0)).setScale(2, RoundingMode.HALF_UP);
		return memF + " MiB / " + totalF + " MiB";
	}

	private String getUptime(long guildID) {
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
				upString = Language.getMessage(guildID, "utils.uptimeFormatSingular", days, nHours + ":" + min + ":" + sec);
			else
				upString = Language.getMessage(guildID, "utils.uptimeFormat", days, nHours + ":" + min + ":" + sec);
		}
		return upString;
	}

}
