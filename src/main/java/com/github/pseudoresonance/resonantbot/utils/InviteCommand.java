package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InviteCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		e.getChannel().sendMessage(Language.getMessage(e.getGuild().getIdLong(), "utils.inviteMeToGuild", "https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222")).queue();
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.inviteCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
