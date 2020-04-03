package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;

import com.github.pseudoresonance.resonantbot.ResonantBot;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		EmbedBuilder build = new EmbedBuilder();
		long now = System.nanoTime();
		String heartBeat = (int) ResonantBot.getBot().getJDA().getAverageGatewayPing() + "ms";
		build.setColor(new Color(178, 17, 36));
		build.addField(LanguageManager.getLanguage(e).getMessage("utils.pong"), "⏱️ " + LanguageManager.getLanguage(e).getMessage("utils.pinging") + "\n💓 " + heartBeat, false);
		Message message = e.getChannel().sendMessage(build.build()).complete();
		long received = System.nanoTime();
		long diff = (received - now) / 1000000;
		String ping = diff + "ms";
		build = new EmbedBuilder();
		build.setColor(new Color(178, 17, 36));
		build.addField(LanguageManager.getLanguage(e).getMessage("utils.pong"), "⏱️ " + ping + "\n💓 " + heartBeat, false);
		message.editMessage(build.build()).queue();
	}

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.pingCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
