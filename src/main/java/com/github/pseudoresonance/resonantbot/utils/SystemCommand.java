package com.github.pseudoresonance.resonantbot.utils;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
		sb.append("=== " + LanguageManager.getLanguage(id).getMessage("utils.systemStats") + " ===");
		// UPTIME
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.uptime") + " :: " + LanguageManager.getLanguage(id).formatTimeAgo(new Timestamp(ManagementFactory.getRuntimeMXBean().getStartTime()), false));
		output.add(sb.toString());
		// CPU
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.cpu") + " =");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.model") + " :: " + cpu.getName());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.cores") + " :: " + cpu.getPhysicalProcessorCount());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.sockets") + " :: " + cpu.getPhysicalPackageCount());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.coresPerSocket") + " :: " + ((Integer) (cpu.getPhysicalProcessorCount() / cpu.getPhysicalPackageCount())));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.threads") + " :: " + cpu.getLogicalProcessorCount());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.frequency") + " :: " + (cpu.getVendorFreq() >= 0 ? Double.valueOf(df.format(cpu.getVendorFreq() / 1000000000.0)) + "GHz" : LanguageManager.getLanguage(id).getMessage("utils.unknown")));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.cpuUsage") + " :: " + (cpu.getSystemLoadAverage(1)[0] >= 0 ? Double.valueOf(df.format(cpu.getSystemLoadAverage(1)[0])) + "%" : LanguageManager.getLanguage(id).getMessage("utils.unknown")));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.temperature") + " :: " + (sensors.getCpuTemperature() > 0 ? sensors.getCpuTemperature() + "°C" : LanguageManager.getLanguage(id).getMessage("utils.unknown")));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.voltage") + " :: " + (sensors.getCpuVoltage() > 0 ? sensors.getCpuVoltage() + "V" : LanguageManager.getLanguage(id).getMessage("utils.unknown")));
		output.add(sb.toString());
		// RAM
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.ram") + " =");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.used") + " :: " + bytesToHumanFormat(runtime.totalMemory() - runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.free") + " :: " + bytesToHumanFormat(runtime.freeMemory(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.allocated") + " :: " + bytesToHumanFormat(runtime.totalMemory(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.maxAllocated") + " :: " + (runtime.maxMemory() == Long.MAX_VALUE ? LanguageManager.getLanguage(id).getMessage("utils.unlimited") : bytesToHumanFormat(Runtime.getRuntime().maxMemory(), useSi)));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.available") + " :: " + bytesToHumanFormat(ram.getAvailable(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.total") + " :: " + bytesToHumanFormat(ram.getTotal(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.swapUsed") + " :: " + bytesToHumanFormat(ram.getSwapUsed(), useSi));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.swapTotal") + " :: " + bytesToHumanFormat(ram.getSwapTotal(), useSi));
		output.add(sb.toString());
		// JAVA
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.java") + " =");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.javaVersion") + " :: " + System.getProperty("java.version"));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.javaVendor") + " :: " + System.getProperty("java.vendor"));
		output.add(sb.toString());
		// OS
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.os") + " =");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.developer") + " :: " + os.getManufacturer());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.os") + " :: " + os.getFamily() + " " + os.getVersion());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.architecture") + " :: " + ManagementFactory.getOperatingSystemMXBean().getArch() + " " + os.getBitness() + "-bit");
		output.add(sb.toString());
		// MACHINE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.hardware") + " =");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.systemUptime") + " :: " + LanguageManager.getLanguage(id).formatTimeAgo(new Timestamp(System.currentTimeMillis() - cpu.getSystemUptime() * 1000), false));
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.systemTime") + " :: " + LanguageManager.getLanguage(id).formatDateTime(LocalDateTime.now()));
		Baseboard baseboard = system.getBaseboard();
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.motherboard") + " :: " + baseboard.getManufacturer() + " " + baseboard.getModel() + " " + baseboard.getVersion());
		Firmware firmware = system.getFirmware();
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.bios") + " :: " + firmware.getManufacturer() + " " + firmware.getName() + " " + firmware.getVersion() + " " + firmware.getDescription() + " (" + firmware.getReleaseDate() + ")");
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.system") + " :: " + system.getManufacturer() + " " + system.getModel());
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.fanSpeeds") + " :: ");
		StringBuilder sbFans = new StringBuilder();
		for (int i = 0; i < sensors.getFanSpeeds().length; i++) {
			if (i > 0) {
				sbFans.append(", ");
			}
			sbFans.append(sensors.getFanSpeeds()[i]);
		}
		String fans = sbFans.toString();
		if (fans.length() == 0)
			fans = LanguageManager.getLanguage(id).getMessage("utils.unknown");
		sb.append(fans);
		output.add(sb.toString());
		// POWER
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.powerSupply") + " =");
		for (int i = 0; i < power.length; i++) {
			PowerSource p = power[i];
			sb.append(System.lineSeparator()).append((i + 1) + ": " + p.getName() + " :: " + LanguageManager.getLanguage(id).getMessage("utils.capacity") + ": " + Double.valueOf(df.format(p.getRemainingCapacity() * 100.0)) + "% " + getBatteryTime(id, p.getTimeRemaining()));
		}
		output.add(sb.toString());
		// STORAGE
		sb = new StringBuilder();
		sb.append(System.lineSeparator()).append("= " + LanguageManager.getLanguage(id).getMessage("utils.storage") + " =");
		long total = 0;
		long free = 0;
		for (OSFileStore fs : fileStores) {
			total += fs.getTotalSpace();
			free += fs.getUsableSpace();
		}
		long used = total - free;
		sb.append(System.lineSeparator()).append(LanguageManager.getLanguage(id).getMessage("utils.used") + ": " + bytesToHumanFormat(used, useSi) + " " + LanguageManager.getLanguage(id).getMessage("utils.free") + ": " + bytesToHumanFormat(free, useSi) + " " + LanguageManager.getLanguage(id).getMessage("utils.total") + ": " + bytesToHumanFormat(total, useSi));
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
	
	public String getBatteryTime(long id, double time) {
		if (time < -1)
			return LanguageManager.getLanguage(id).getMessage("utils.charging");
		else if (time < 0)
			return LanguageManager.getLanguage(id).getMessage("utils.calculating");
		else
			return LanguageManager.getLanguage(id).formatTimeAgo(new Timestamp(System.currentTimeMillis() - (int) (time * 1000)), false);
	}

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.systemCommandDescription");
	}

	public boolean isHidden() {
		return true;
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
