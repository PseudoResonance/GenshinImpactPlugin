package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.util.HashSet;

import com.github.pseudoresonance.resonantbot.ResonantBot;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
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

}
