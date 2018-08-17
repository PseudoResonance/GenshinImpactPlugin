package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.ResonantBot;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		EmbedBuilder build = new EmbedBuilder();
		long now = System.nanoTime();
		String heartBeat = (int) ResonantBot.getClient().getAveragePing() + "ms";
		build.setColor(new Color(178, 17, 36));
		build.addField(Language.getMessage(e, "utils.pong"), "⏱️ " + Language.getMessage(e, "utils.pinging") + "\n💓 " + heartBeat, false);
		Message message = e.getChannel().sendMessage(build.build()).complete();
		long received = System.nanoTime();
		long diff = (received - now) / 1000000;
		String ping = diff + "ms";
		build = new EmbedBuilder();
		build.setColor(new Color(178, 17, 36));
		build.addField(Language.getMessage(e, "utils.pong"), "⏱️ " + ping + "\n💓 " + heartBeat, false);
		message.editMessage(build.build()).queue();
	}

	public String getDesc(long id) {
		return Language.getMessage(id, "utils.pingCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
