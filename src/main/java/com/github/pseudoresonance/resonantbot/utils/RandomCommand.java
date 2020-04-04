package com.github.pseudoresonance.resonantbot.utils;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RandomCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		long min = 0, max = 0;
		if (args.length == 0) {
			e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.minMaxPlease")).queue();
			return;
		} else if (args.length == 1) {
			try {
				max = Long.valueOf(args[0]);
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.integersPlease")).queue();
				return;
			}
		} else {
			try {
				min = Long.valueOf(args[0]);
				max = Long.valueOf(args[1]);
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.integersPlease")).queue();
				return;
			}
		}
		if (min == Long.MAX_VALUE || max == Long.MAX_VALUE) {
			e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.integersPlease")).queue();
		} else {
			if (max < min) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.randomNumber", ThreadLocalRandom.current().nextLong(max, min + 1))).queue();
			} else {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.randomNumber", ThreadLocalRandom.current().nextLong(min, max + 1))).queue();
			}
		}
	}

}
