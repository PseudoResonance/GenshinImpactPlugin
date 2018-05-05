package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.listeners.MessageListener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PrefixCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		if (e.getChannelType() == ChannelType.PRIVATE) {
			e.getChannel().sendMessage("The prefix for DMs is `" + Config.getPrefix() + "`").queue();
			return;
		}
		if (args.length == 0) {
			e.getChannel().sendMessage("The prefix for " + e.getGuild().getName() + " is `" + MessageListener.getPrefix(e.getGuild()) + "`").queue();
		} else if (args.length >= 1) {
			if (PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.ADMINISTRATOR)) {
				if (!args[0].equals("")) {
					String prefix = "";
					for (String s : args) {
						prefix += s + " ";
					}
					prefix = prefix.substring(0, prefix.length() - 1);
					MessageListener.setPrefix(e.getGuild().getIdLong(), prefix);
					e.getChannel().sendMessage("The prefix for " + e.getGuild().getName() + " has been set to `" + MessageListener.getPrefix(e.getGuild()) + "`").queue();
				} else {
					e.getChannel().sendMessage("Please add a prefix to set!").queue();
				}
			} else {
				e.getChannel().sendMessage("The prefix for " + e.getGuild().getName() + " is `" + MessageListener.getPrefix(e.getGuild()) + "`").queue();
			}
		}
	}

	public String getDesc() {
		return "Sets or gets the guild prefix";
	}

	public boolean isHidden() {
		return false;
	}

}
