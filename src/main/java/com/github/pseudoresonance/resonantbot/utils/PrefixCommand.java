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
		if (e.getChannelType() == ChannelType.PRIVATE) {
			e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.privatePrefix", Config.getPrefix())).queue();
			return;
		}
		if (args.length == 0) {
			e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.prefix", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild()))).queue();
		} else if (args.length >= 1) {
			if (PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.ADMINISTRATOR) || e.getAuthor().getIdLong() == Config.getOwner()) {
				if (!args[0].equals("")) {
					String prefix = "";
					for (String s : args) {
						prefix += s + " ";
					}
					prefix = prefix.substring(0, prefix.length() - 1);
					if (Language.isValidPrefix(prefix)) {
						MessageListener.setPrefix(e.getGuild().getIdLong(), prefix);
						e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "utils.prefixSet", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild()))).queue();
					} else
						e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "utils.invalidPrefix")).queue();
				} else {
					e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "utils.addPrefix")).queue();
				}
			} else {
				e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "main.prefix", e.getGuild().getName(), MessageListener.getPrefix(e.getGuild()))).queue();
			}
		}
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.prefixCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
