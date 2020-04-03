package com.github.pseudoresonance.resonantbot.utils;

import java.util.concurrent.ThreadLocalRandom;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RandomCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
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

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.randomCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
