package com.github.pseudoresonance.resonantbot.utils;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InviteCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.inviteMeToGuild", "https://discordapp.com/oauth2/authorize?client_id=" + e.getJDA().getSelfUser().getId() + "&scope=bot&permissions=3505222")).queue();
	}

	public String getDesc(long id) {
		return LanguageManager.getLanguage(id).getMessage("utils.inviteCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
