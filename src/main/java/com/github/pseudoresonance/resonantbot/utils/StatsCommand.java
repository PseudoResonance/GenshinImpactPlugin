package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.PluginManager;
import com.github.pseudoresonance.resonantbot.ResonantBot;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
		build.setTitle(e.getJDA().getSelfUser().getName() + " " + LanguageManager.getLanguage(id).getMessage("main.version", ResonantBot.getBot().getVersionInfo().getProperty("version")));
		build.setColor(new Color(77, 0, 153));
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.uptime"), LanguageManager.getLanguage(id).formatTimeAgo(new Timestamp(ManagementFactory.getRuntimeMXBean().getStartTime()), false), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.owner"), LanguageManager.escape(own.getName()) + "#" + own.getDiscriminator(), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.ramUsage"), getRam(), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.cpuUsage"), getCpu(id), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.pluginsLoaded"), String.valueOf(PluginManager.getPlugins().size()), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.commandsLoaded"), String.valueOf(CommandManager.getCommands().size()), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.javaVersion"), System.getProperty("java.version"), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.jdaVersion"), LanguageManager.getLanguage(id).getMessage("main.version", ResonantBot.getBot().getVersionInfo().getProperty("jda_version")), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.servers"), String.valueOf(e.getJDA().getGuilds().size()), true);
		build.addField(LanguageManager.getLanguage(id).getMessage("utils.helpfulLinks"), "[" + LanguageManager.getLanguage(id).getMessage("utils.inviteMe") + "](https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222), [GitHub](https://github.com/PseudoResonance/ResonantBot)", false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.statsCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

	private String getCpu(long id) {
		CentralProcessor cpu = info.getHardware().getProcessor();
		return cpu.getSystemLoadAverage(1)[0] >= 0 ? Double.valueOf(df.format(cpu.getSystemLoadAverage(1)[0])) + "%" : LanguageManager.getLanguage(id).getMessage("utils.unknown");
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

}
