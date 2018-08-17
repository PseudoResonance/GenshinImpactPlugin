package com.github.pseudoresonance.resonantbot.utils;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

public class SystemCommand implements Command {
	
	private static SystemInfo info = new SystemInfo();
	
	private static final boolean useSi = false;

	private static final DecimalFormat df = new DecimalFormat("#.##");

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		ArrayList<String> output = new ArrayList<String>();
		ComputerSystem system = info.getHardware().getComputerSystem();
		CentralProcessor cpu = info.getHardware().getProcessor();
		GlobalMemory ram = info.getHardware().getMemory();
		HWDiskStore[] disks = info.getHardware().getDiskStores();
		PowerSource[] power = info.getHardware().getPowerSources();
		Sensors sensors = info.getHardware().getSensors();
		OperatingSystem os = info.getOperatingSystem();
		OSFileStore[] fileStores = os.getFileSystem().getFileStores();
		Runtime runtime = Runtime.getRuntime();
		StringBuilder sb = new StringBuilder();
		sb.append("=== " + Language.getMessage(e.getGuild().getIdLong(), "utils.systemStats") + " ===");
		// UPTIME
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.uptime") + " :: " + getUptime(e.getGuild().getIdLong(), (System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime())));
		output.add(sb.toString());
		// CPU
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.cpu") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.model") + " :: " + cpu.getName());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.cores") + " :: " + cpu.getPhysicalProcessorCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.sockets") + " :: " + cpu.getPhysicalPackageCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.coresPerSocket") + " :: " + ((Integer) (cpu.getPhysicalProcessorCount() / cpu.getPhysicalPackageCount())));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.threads") + " :: " + cpu.getLogicalProcessorCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.frequency") + " :: " + (cpu.getVendorFreq() >= 0 ? Double.valueOf(df.format(cpu.getVendorFreq() / 1000000000.0)) + "GHz" : Language.getMessage(e.getGuild().getIdLong(), "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.cpuUsage") + " :: " + (cpu.getSystemCpuLoad() >= 0 ? Double.valueOf(df.format(cpu.getSystemCpuLoad() * 100.0)) + "%" : Language.getMessage(e.getGuild().getIdLong(), "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.temperature") + " :: " + (sensors.getCpuTemperature() > 0 ? sensors.getCpuTemperature() + "°C" : Language.getMessage(e.getGuild().getIdLong(), "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.voltage") + " :: " + (sensors.getCpuVoltage() > 0 ? sensors.getCpuVoltage() + "V" : Language.getMessage(e.getGuild().getIdLong(), "utils.unknown")));
		output.add(sb.toString());
		// RAM
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.ram") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.used") + " :: " + bytesToHumanFormat(runtime.totalMemory() - runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.free") + " :: " + bytesToHumanFormat(runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.allocated") + " :: " + bytesToHumanFormat(runtime.totalMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.maxAllocated") + " :: " + (runtime.maxMemory() == Long.MAX_VALUE ? Language.getMessage(e.getGuild().getIdLong(), "utils.unlimited") : bytesToHumanFormat(Runtime.getRuntime().maxMemory(), useSi)));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.available") + " :: " + bytesToHumanFormat(ram.getAvailable(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.total") + " :: " + bytesToHumanFormat(ram.getTotal(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.swapUsed") + " :: " + bytesToHumanFormat(ram.getSwapUsed(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.swapTotal") + " :: " + bytesToHumanFormat(ram.getSwapTotal(), useSi));
		output.add(sb.toString());
		// JAVA
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.java") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.javaVersion") + " :: " + System.getProperty("java.version"));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.javaVendor") + " :: " + System.getProperty("java.vendor"));
		output.add(sb.toString());
		// OS
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.os") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.developer") + " :: " + os.getManufacturer());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.os") + " :: " + os.getFamily() + " " + os.getVersion());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.architecture") + " :: " + ManagementFactory.getOperatingSystemMXBean().getArch() + " " + os.getBitness() + "-bit");
		output.add(sb.toString());
		// MACHINE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.hardware") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.systemUptime") + " :: " + getUptime(e.getGuild().getIdLong(), cpu.getSystemUptime()));
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.systemTime") + " :: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(Language.getDateTimeFormat(e.getGuild().getIdLong()))));
		Baseboard baseboard = system.getBaseboard();
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.motherboard") + " :: " + baseboard.getManufacturer() + " " + baseboard.getModel() + " " + baseboard.getVersion());
		Firmware firmware = system.getFirmware();
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.bios") + " :: " + firmware.getManufacturer() + " " + firmware.getName() + " " + firmware.getVersion() + " " + firmware.getDescription() + " (" + firmware.getReleaseDate() + ")");
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.system") + " :: " + system.getManufacturer() + " " + system.getModel());
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.fanSpeeds") + " :: ");
		for (int i = 0; i < sensors.getFanSpeeds().length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(sensors.getFanSpeeds()[i]);
		}
		output.add(sb.toString());
		// POWER
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.powerSupply") + " =");
		for (int i = 0; i < power.length; i++) {
			PowerSource p = power[i];
			sb.append(System.lineSeparator()).append((i + 1) + ": " + p.getName() + " :: " + Language.getMessage(e.getGuild().getIdLong(), "utils.capacity") + ": " + Double.valueOf(df.format(p.getRemainingCapacity() * 100.0)) + "% " + getTimeRemaining(e.getGuild().getIdLong(), p.getTimeRemaining()));
		}
		output.add(sb.toString());
		// STORAGE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(e.getGuild().getIdLong(), "utils.storage") + " =");
		long total = 0;
		long free = 0;
		for (OSFileStore fs : fileStores) {
			total += fs.getTotalSpace();
			free = fs.getUsableSpace();
		}
		long used = total - free;
		sb.append(System.lineSeparator()).append(Language.getMessage(e.getGuild().getIdLong(), "utils.used") + ": " + bytesToHumanFormat(used, useSi) + " " + Language.getMessage(e.getGuild().getIdLong(), "utils.free") + ": " + bytesToHumanFormat(free, useSi) + " " + Language.getMessage(e.getGuild().getIdLong(), "utils.total") + ": " + bytesToHumanFormat(total, useSi));
		int n = 0;
		for (int i = 0; i < disks.length; i++) {
			HWDiskStore ds = disks[i];
			if (ds.getSize() <= 0)
				continue;
			n++;
			sb.append(System.lineSeparator()).append(n + ": " + ds.getModel() + " :: " + bytesToHumanFormat(ds.getSize(), useSi));
		}
		output.add(sb.toString());
		sb = new StringBuilder();
		for (int i = 0; i < output.size(); i++) {
			String test = "";
			if (i > 0)
				test = sb.toString();
			sb.append(output.get(i));
			if (sb.length() > 1986) {
				e.getChannel().sendMessage("```asciidoc" + System.lineSeparator() + test + "```").queue();
				sb = new StringBuilder().append(output.get(i));
			}
		}
		e.getChannel().sendMessage("```asciidoc" + System.lineSeparator() + sb.toString() + "```").queue();
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.systemCommandDescription");
	}

	public boolean isHidden() {
		return true;
	}
	
	private static String getTimeRemaining(long guildID, double time) {
		if (time < -1d) {
			return Language.getMessage(guildID, "utils.charging");
		} else if (time < 0d) {
			return Language.getMessage(guildID, "utils.calculating");
		}
		long calcTime = (long) time;
		long days = TimeUnit.SECONDS.toDays(calcTime);
		calcTime -= TimeUnit.DAYS.toSeconds(days);
		long hours = TimeUnit.SECONDS.toHours(calcTime);
		calcTime -= TimeUnit.HOURS.toSeconds(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(calcTime);
		calcTime -= TimeUnit.MINUTES.toSeconds(minutes);
		long seconds = TimeUnit.SECONDS.toSeconds(calcTime);
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
				upString = Language.getMessage(guildID, "utils.uptimeFormatSingular", days, hours + ":" + min + ":" + sec);
			else
				upString = Language.getMessage(guildID, "utils.uptimeFormat", days, hours + ":" + min + ":" + sec);
		}
		return upString;
	}

	private static String bytesToHumanFormat(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
}

	private String getUptime(long guildID, long uptime) {
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
				upString = Language.getMessage(guildID, "utils.uptimeFormatSingular", days, hours + ":" + min + ":" + sec);
			else
				upString = Language.getMessage(guildID, "utils.uptimeFormat", days, hours + ":" + min + ":" + sec);
		}
		return upString;
	}

}
