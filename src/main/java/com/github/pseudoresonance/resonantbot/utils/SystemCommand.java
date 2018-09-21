package com.github.pseudoresonance.resonantbot.utils;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.entities.ChannelType;
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
		Long id = 0L;
		if (e.getChannelType() == ChannelType.PRIVATE)
			id = e.getPrivateChannel().getIdLong();
		else
			id = e.getGuild().getIdLong();
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
		sb.append("=== " + Language.getMessage(id, "utils.systemStats") + " ===");
		// UPTIME
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.uptime") + " :: " + getUptime(id, (System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime())));
		output.add(sb.toString());
		// CPU
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.cpu") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.model") + " :: " + cpu.getName());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.cores") + " :: " + cpu.getPhysicalProcessorCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.sockets") + " :: " + cpu.getPhysicalPackageCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.coresPerSocket") + " :: " + ((Integer) (cpu.getPhysicalProcessorCount() / cpu.getPhysicalPackageCount())));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.threads") + " :: " + cpu.getLogicalProcessorCount());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.frequency") + " :: " + (cpu.getVendorFreq() >= 0 ? Double.valueOf(df.format(cpu.getVendorFreq() / 1000000000.0)) + "GHz" : Language.getMessage(id, "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.cpuUsage") + " :: " + (cpu.getSystemCpuLoad() >= 0 ? Double.valueOf(df.format(cpu.getSystemCpuLoad() * 100.0)) + "%" : Language.getMessage(id, "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.temperature") + " :: " + (sensors.getCpuTemperature() > 0 ? sensors.getCpuTemperature() + "°C" : Language.getMessage(id, "utils.unknown")));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.voltage") + " :: " + (sensors.getCpuVoltage() > 0 ? sensors.getCpuVoltage() + "V" : Language.getMessage(id, "utils.unknown")));
		output.add(sb.toString());
		// RAM
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.ram") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.used") + " :: " + bytesToHumanFormat(runtime.totalMemory() - runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.free") + " :: " + bytesToHumanFormat(runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.allocated") + " :: " + bytesToHumanFormat(runtime.totalMemory(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.maxAllocated") + " :: " + (runtime.maxMemory() == Long.MAX_VALUE ? Language.getMessage(id, "utils.unlimited") : bytesToHumanFormat(Runtime.getRuntime().maxMemory(), useSi)));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.available") + " :: " + bytesToHumanFormat(ram.getAvailable(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.total") + " :: " + bytesToHumanFormat(ram.getTotal(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.swapUsed") + " :: " + bytesToHumanFormat(ram.getSwapUsed(), useSi));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.swapTotal") + " :: " + bytesToHumanFormat(ram.getSwapTotal(), useSi));
		output.add(sb.toString());
		// JAVA
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.java") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.javaVersion") + " :: " + System.getProperty("java.version"));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.javaVendor") + " :: " + System.getProperty("java.vendor"));
		output.add(sb.toString());
		// OS
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.os") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.developer") + " :: " + os.getManufacturer());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.os") + " :: " + os.getFamily() + " " + os.getVersion());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.architecture") + " :: " + ManagementFactory.getOperatingSystemMXBean().getArch() + " " + os.getBitness() + "-bit");
		output.add(sb.toString());
		// MACHINE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.hardware") + " =");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.systemUptime") + " :: " + getUptimeSeconds(id, cpu.getSystemUptime()));
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.systemTime") + " :: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(Language.getDateTimeFormat(id))));
		Baseboard baseboard = system.getBaseboard();
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.motherboard") + " :: " + baseboard.getManufacturer() + " " + baseboard.getModel() + " " + baseboard.getVersion());
		Firmware firmware = system.getFirmware();
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.bios") + " :: " + firmware.getManufacturer() + " " + firmware.getName() + " " + firmware.getVersion() + " " + firmware.getDescription() + " (" + firmware.getReleaseDate() + ")");
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.system") + " :: " + system.getManufacturer() + " " + system.getModel());
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.fanSpeeds") + " :: ");
		StringBuilder sbFans = new StringBuilder();
		for (int i = 0; i < sensors.getFanSpeeds().length; i++) {
			if (i > 0) {
				sbFans.append(", ");
			}
			sbFans.append(sensors.getFanSpeeds()[i]);
		}
		String fans = sbFans.toString();
		if (fans.length() == 0)
			fans = Language.getMessage(id, "utils.unknown");
		sb.append(fans);
		output.add(sb.toString());
		// POWER
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.powerSupply") + " =");
		for (int i = 0; i < power.length; i++) {
			PowerSource p = power[i];
			sb.append(System.lineSeparator()).append((i + 1) + ": " + p.getName() + " :: " + Language.getMessage(id, "utils.capacity") + ": " + Double.valueOf(df.format(p.getRemainingCapacity() * 100.0)) + "% " + getTimeRemaining(id, p.getTimeRemaining()));
		}
		output.add(sb.toString());
		// STORAGE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + Language.getMessage(id, "utils.storage") + " =");
		long total = 0;
		long free = 0;
		for (OSFileStore fs : fileStores) {
			total += fs.getTotalSpace();
			free += fs.getUsableSpace();
		}
		long used = total - free;
		sb.append(System.lineSeparator()).append(Language.getMessage(id, "utils.used") + ": " + bytesToHumanFormat(used, useSi) + " " + Language.getMessage(id, "utils.free") + ": " + bytesToHumanFormat(free, useSi) + " " + Language.getMessage(id, "utils.total") + ": " + bytesToHumanFormat(total, useSi));
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

	public String getDesc(long id) {
		return Language.getMessage(id, "utils.systemCommandDescription");
	}

	public boolean isHidden() {
		return true;
	}
	
	private static String getTimeRemaining(long id, double time) {
		if (time < -1d) {
			return Language.getMessage(id, "utils.charging");
		} else if (time < 0d) {
			return Language.getMessage(id, "utils.calculating");
		}
		long calcTime = (long) time;
		long days = TimeUnit.SECONDS.toDays(calcTime);
		calcTime -= TimeUnit.DAYS.toSeconds(days);
		long hours = TimeUnit.SECONDS.toHours(calcTime);
		calcTime -= TimeUnit.HOURS.toSeconds(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(calcTime);
		calcTime -= TimeUnit.MINUTES.toSeconds(minutes);
		long seconds = calcTime;
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

	private static String bytesToHumanFormat(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	private String getUptimeSeconds(long id, long uptime) {
		long days = TimeUnit.SECONDS.toDays(uptime);
		uptime -= TimeUnit.DAYS.toSeconds(days);
		long hours = TimeUnit.SECONDS.toHours(uptime);
		uptime -= TimeUnit.HOURS.toSeconds(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(uptime);
		uptime -= TimeUnit.MINUTES.toSeconds(minutes);
		long seconds = uptime;
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

	private String getUptime(long id, long uptime) {
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
