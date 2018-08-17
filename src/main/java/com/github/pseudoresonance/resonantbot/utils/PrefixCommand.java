package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.listeners.MessageListener;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PrefixCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		if (args.length == 0) {
			if (e.getChannelType() == ChannelType.PRIVATE) {
				e.getChannel().sendMessage(Language.getMessage(e.getPrivateChannel().getIdLong(), "main.privatePrefix", MessageListener.getPrefix(e.getPrivateChannel().getIdLong()))).queue();
			} else {
				e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.prefix", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild().getIdLong()))).queue();
			}
		} else if (args.length >= 1) {
			if (e.getChannelType() == ChannelType.PRIVATE || PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.ADMINISTRATOR) || e.getAuthor().getIdLong() == Config.getOwner()) {
				if (!args[0].equals("")) {
					String prefix = "";
					for (String s : args) {
						prefix += s + " ";
					}
					prefix = prefix.substring(0, prefix.length() - 1);
					if (Language.isValidPrefix(prefix)) {
						if (e.getChannelType() == ChannelType.PRIVATE) {
							MessageListener.setPrefix(e.getPrivateChannel().getIdLong(), prefix);
							e.getChannel().sendMessage(Language.getMessage(e.getPrivateChannel().getIdLong(), "utils.privatePrefixSet", MessageListener.getPrefix(e.getPrivateChannel().getIdLong()))).queue();
						} else {
							MessageListener.setPrefix(e.getGuild().getIdLong(), prefix);
							e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "utils.prefixSet", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild().getIdLong()))).queue();
						}
					} else
						e.getChannel().sendMessage(Language.getMessage(e, "utils.invalidPrefix")).queue();
				} else {
					e.getChannel().sendMessage(Language.getMessage(e, "utils.addPrefix")).queue();
				}
			} else {
				if (e.getChannelType() == ChannelType.PRIVATE) {
					e.getChannel().sendMessage(Language.getMessage(e.getPrivateChannel().getIdLong(), "main.privatePrefix", MessageListener.getPrefix(e.getPrivateChannel().getIdLong()))).queue();
				} else {
					e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.prefix", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild().getIdLong()))).queue();
				}
			}
		}
	}

	public String getDesc(long id) {
		return Language.getMessage(id, "utils.prefixCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
