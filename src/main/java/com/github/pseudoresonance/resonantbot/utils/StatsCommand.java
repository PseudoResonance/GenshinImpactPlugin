package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.PluginManager;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class StatsCommand implements Command {
	
	private static SystemInfo info = new SystemInfo();
	
	private static final boolean useSi = false;

	private static final DecimalFormat df = new DecimalFormat("#.##");

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		Long id = 0L;
		if (e.getChannelType() == ChannelType.PRIVATE)
			id = e.getPrivateChannel().getIdLong();
		else
			id = e.getGuild().getIdLong();
		EmbedBuilder build = new EmbedBuilder();
		User own = e.getJDA().getUserById(Config.getOwner());
		build.setTitle(e.getJDA().getSelfUser().getName());
		build.setColor(new Color(77, 0, 153));
		build.addField(Language.getMessage(id, "utils.uptime"), getUptime(id), true);
		build.addField(Language.getMessage(id, "utils.owner"), Language.escape(own.getName()) + "#" + own.getDiscriminator(), true);
		build.addField(Language.getMessage(id, "utils.ramUsage"), getRam(), true);
		build.addField(Language.getMessage(id, "utils.cpuUsage"), getCpu(id), true);
		build.addField(Language.getMessage(id, "utils.pluginsLoaded"), String.valueOf(PluginManager.getPlugins().size()), true);
		build.addField(Language.getMessage(id, "utils.commandsLoaded"), String.valueOf(CommandManager.getCommands().size()), true);
		build.addField(Language.getMessage(id, "utils.javaVersion"), System.getProperty("java.version"), true);
		build.addField(Language.getMessage(id, "utils.javaVendor"), System.getProperty("java.vendor"), true);
		build.addField(Language.getMessage(id, "utils.servers"), String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField(Language.getMessage(id, "utils.helpfulLinks"), "[" + Language.getMessage(id, "utils.inviteMe") + "](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long id) {
		return Language.getMessage(id, "utils.statsCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

	private String getCpu(long id) {
		CentralProcessor cpu = info.getHardware().getProcessor();
		return cpu.getSystemCpuLoad() >= 0 ? Double.valueOf(df.format(cpu.getSystemCpuLoad() * 100.0)) + "%" : Language.getMessage(id, "utils.unknown");
	}

	private String getRam() {
		Runtime run = Runtime.getRuntime();
		long total = run.totalMemory();
		long mem = total - run.freeMemory();
		return bytesToHumanFormat(mem, useSi) + " / " + bytesToHumanFormat(total, useSi);
	}

	private static String bytesToHumanFormat(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
}

	private String getUptime(long id) {
		long start = ManagementFactory.getRuntimeMXBean().getStartTime();
		long now = System.currentTimeMillis();
		long uptime = now - start;
		long days = TimeUnit.MILLISECONDS.toDays(uptime);
		uptime -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(uptime);
		uptime -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
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
		if (days > 0) {
			if (days == 1)
				upString = Language.getMessage(id, "utils.uptimeFormatSingular", days, hours + ":" + min + ":" + sec);
			else
				upString = Language.getMessage(id, "utils.uptimeFormat", days, hours + ":" + min + ":" + sec);
		}
		return upString;
	}

}
