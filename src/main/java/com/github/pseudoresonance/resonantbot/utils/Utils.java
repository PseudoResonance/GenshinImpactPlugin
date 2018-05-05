package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.api.Plugin;

public class Utils extends Plugin {

	public void onEnable() {
		CommandManager.registerCommand("help", new HelpCommand(), this);
		CommandManager.registerCommand("ping", new PingCommand(), this);
		CommandManager.registerCommand("prefix", new PrefixCommand(), this);
		CommandManager.registerCommand("stats", new StatsCommand(), this);
		CommandManager.registerCommand("random", new RandomCommand(), this);
		CommandManager.registerCommand("invite", new InviteCommand(), this);
	}
	
	public void onDisable() {
	}
	
}
