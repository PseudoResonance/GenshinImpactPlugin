package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.Config;
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
		build.addField("Uptime", getUptime(), true);
		build.addField("Owner", own.getName() + "#" + own.getDiscriminator(), true);
		build.addField("RAM Usage", getRam(), true);
		build.addField("CPU Usage", getCpu(), true);
		build.addField("Plugins Loaded", String.valueOf(PluginManager.getPlugins().size()), true);
		build.addField("Commands Loaded", String.valueOf(CommandManager.getCommands().size()), true);
		build.addField("Java Version", System.getProperty("java.version"), true);
		build.addField("Java Vendor", System.getProperty("java.vendor"), true);
		build.addField("Servers", String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField("Helpful Links:", "[Invite Me](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc() {
		return "Show bot statistics";
	}

	public boolean isHidden() {
		return false;
	}

	private String getCpu() {
		double load = osb.getSystemLoadAverage();
		return load >= 0 ? load + "%" : "Unknown";
	}

	private String getRam() {
		Runtime run = Runtime.getRuntime();
		long total = run.totalMemory();
		long mem = total - run.freeMemory();
		BigDecimal memF = new BigDecimal(String.valueOf(mem / 1048576.0)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalF = new BigDecimal(String.valueOf(total / 1048576.0)).setScale(2, RoundingMode.HALF_UP);
		return memF + " MiB / " + totalF + " MiB";
	}

	private String getUptime() {
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
			if (days == 1) {
				upString = days + " day and " + nHours + ":" + min + ":" + sec;
			} else {
				upString = days + " days and " + nHours + ":" + min + ":" + sec;
			}
		}
		return upString;
	}

}
