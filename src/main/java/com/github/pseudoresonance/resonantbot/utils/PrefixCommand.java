package com.github.pseudoresonance.resonantbot.utils;

import java.util.HashSet;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.data.Data;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PrefixCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		if (args.length == 0) {
			if (e.getChannelType() == ChannelType.PRIVATE) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e.getPrivateChannel().getIdLong()).getMessage("main.privatePrefix", Data.getGuildPrefix(e.getPrivateChannel().getIdLong()))).queue();
			} else {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e.getGuild().getIdLong()).getMessage("main.prefix", e.getGuild().getName(), Data.getGuildPrefix(e.getGuild().getIdLong()))).queue();
			}
		} else if (args.length >= 1) {
			if (e.getChannelType() == ChannelType.PRIVATE || userPermissions.contains(PermissionGroup.ADMIN)) {
				if (args[0].equalsIgnoreCase("reset")) {
					if (e.getChannelType() == ChannelType.PRIVATE) {
						Data.setGuildPrefix(e.getPrivateChannel().getIdLong(), null);
						e.getChannel().sendMessage(LanguageManager.getLanguage(e.getPrivateChannel().getIdLong()).getMessage("utils.privatePrefixReset", Data.getGuildPrefix(e.getPrivateChannel().getIdLong()))).queue();
					} else {
						Data.setGuildPrefix(e.getGuild().getIdLong(), null);
						e.getChannel().sendMessage(LanguageManager.getLanguage(e.getGuild().getIdLong()).getMessage("utils.prefixReset", e.getGuild().getName(), Data.getGuildPrefix(e.getGuild().getIdLong()))).queue();
					}
				} else if (!args[0].equals("")) {
					String prefix = "";
					for (String s : args) {
						prefix += s + " ";
					}
					prefix = prefix.length() > 33 ? prefix.substring(0, 32) : prefix.substring(0, prefix.length() - 1);
					if (LanguageManager.isValidPrefix(prefix)) {
						if (e.getChannelType() == ChannelType.PRIVATE) {
							Data.setGuildPrefix(e.getPrivateChannel().getIdLong(), prefix);
							e.getChannel().sendMessage(LanguageManager.getLanguage(e.getPrivateChannel().getIdLong()).getMessage("utils.privatePrefixSet", Data.getGuildPrefix(e.getPrivateChannel().getIdLong()))).queue();
						} else {
							Data.setGuildPrefix(e.getGuild().getIdLong(), prefix);
							e.getChannel().sendMessage(LanguageManager.getLanguage(e.getGuild().getIdLong()).getMessage("utils.prefixSet", e.getGuild().getName(), Data.getGuildPrefix(e.getGuild().getIdLong()))).queue();
						}
					} else
						e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.invalidPrefix")).queue();
				} else {
					e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.addPrefix")).queue();
				}
			} else {
				if (e.getChannelType() == ChannelType.PRIVATE) {
					e.getChannel().sendMessage(LanguageManager.getLanguage(e.getPrivateChannel().getIdLong()).getMessage("main.privatePrefix", Data.getGuildPrefix(e.getPrivateChannel().getIdLong()))).queue();
				} else {
					e.getChannel().sendMessage(LanguageManager.getLanguage(e.getGuild().getIdLong()).getMessage("main.prefix", e.getGuild().getName(), Data.getGuildPrefix(e.getGuild().getIdLong()))).queue();
				}
			}
		}
	}

}
