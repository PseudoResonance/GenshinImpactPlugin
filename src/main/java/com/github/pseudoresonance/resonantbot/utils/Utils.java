package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.api.Plugin;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

public class Utils extends Plugin {

	public void onEnable() {
		CommandManager.registerCommand(this, new HelpCommand(), "help", "utils.helpCommandDescription");
		CommandManager.registerCommand(this, new PingCommand(), "ping", "utils.pingCommandDescription");
		CommandManager.registerCommand(this, new PrefixCommand(), "prefix", "utils.prefixCommandDescription");
		CommandManager.registerCommand(this, new StatsCommand(), "stats", "utils.statsCommandDescription");
		CommandManager.registerCommand(this, new SystemCommand(), "system", "utils.systemCommandDescription");
		CommandManager.registerCommand(this, new RandomCommand(), "random", "utils.randomCommandDescription");
		CommandManager.registerCommand(this, new InviteCommand(), "invite", "utils.inviteCommandDescription");
		CommandManager.registerCommand(this, new InfoCommand(), "info", "utils.infoCommandDescription");
		CommandManager.registerCommand(this, new UserCommand(), "user", "utils.userCommandDescription");
		CommandManager.registerCommand(this, new GuildCommand(), "guild", "utils.guildCommandDescription");
		CommandManager.registerCommand(this, new GuildListCommand(), "listguilds", "utils.guildListCommandDescription", PermissionGroup.BOT_ADMIN, "guildlist");
	}
	
	public void onDisable() {
	}
	
}
