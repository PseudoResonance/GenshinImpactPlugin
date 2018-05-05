package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.util.HashMap;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.PluginManager;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.listeners.MessageListener;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		try {
			if (PluginManager.getPluginNames().size() > 0 && CommandManager.getCommands().size() > 0) {
				String prefix = MessageListener.getPrefix(e.getGuild());
				EmbedBuilder build = new EmbedBuilder();
				build.setColor(new Color(200, 0, 190));
				build.setDescription("List of Commands for " + Config.getName());
				build.setTitle(Config.getName() + " Help");
				for (String pl : PluginManager.getPluginNames()) {
					HashMap<String, Command> commands = CommandManager.getPluginCommandMap(PluginManager.getPlugin(pl));
					int commandsFound = 0;
					if (commands.keySet().size() > 0) {
						String commandSt = "";
						for (String c : commands.keySet()) {
							if (!commands.get(c).isHidden()) {
								commandSt += "`" + prefix + c + "`  |  " + commands.get(c).getDesc() + "\n";
								commandsFound++;
							} else {
								if (e.getAuthor().getIdLong() == Config.getOwner()) {
									commandSt += "`" + prefix + c + "`  |  " + commands.get(c).getDesc() + "\n";
									commandsFound++;
								}
							}
						}
						if (commandsFound > 0) {
							commandSt.substring(0, commandSt.length() - 1);
							build.addField(pl, commandSt, false);
						}
					}
				}
				e.getChannel().sendMessage(build.build()).queue();
			} else
				e.getChannel().sendMessage("No commands loaded!").queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getDesc() {
		return "Displays list of available commands";
	}

	public boolean isHidden() {
		return false;
	}

}
