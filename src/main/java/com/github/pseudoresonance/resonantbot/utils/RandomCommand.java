package com.github.pseudoresonance.resonantbot.utils;

import java.util.concurrent.ThreadLocalRandom;

import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RandomCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		if (args.length == 0) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Please add minimum and maximum values!").queue();
		} else if (args.length == 1) {
			try {
				int min = Integer.valueOf(args[0]);
				if (min == 2147483647) {
					e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Please only use integers for the minimum and maximum values!").queue();
				} else {
					if (min < 0) {
						e.getChannel().sendMessage(e.getAuthor().getAsMention() + " 🎲 Here's your random number: " + ThreadLocalRandom.current().nextInt(min, 1)).queue();
					} else {
						e.getChannel().sendMessage(e.getAuthor().getAsMention() + " 🎲 Here's your random number: " + ThreadLocalRandom.current().nextInt(0, min + 1)).queue();
					}
				}
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Please only use integers for the minimum and maximum values!").queue();
			}
		} else {
			try {
				int min = Integer.valueOf(args[0]);
				int max = Integer.valueOf(args[1]);
				if (min == 2147483647 || max == 2147483647) {
					e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Please only use integers for the minimum and maximum values!").queue();
				} else {
					if (max < min) {
						e.getChannel().sendMessage(e.getAuthor().getAsMention() + " 🎲 Here's your random number: " + ThreadLocalRandom.current().nextInt(max, min + 1)).queue();
					} else {
						e.getChannel().sendMessage(e.getAuthor().getAsMention() + " 🎲 Here's your random number: " + ThreadLocalRandom.current().nextInt(min, max + 1)).queue();
					}
				}
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Please only use integers for the minimum and maximum values!").queue();
			}
		}
	}

	public String getDesc() {
		return "Random number between x and y";
	}

	public boolean isHidden() {
		return false;
	}

}
