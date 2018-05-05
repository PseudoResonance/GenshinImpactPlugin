package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InviteCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		e.getChannel().sendMessage("Invite me to your guild with: https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222").queue();
	}

	public String getDesc() {
		return "Shows bot invite link";
	}

	public boolean isHidden() {
		return false;
	}

}
