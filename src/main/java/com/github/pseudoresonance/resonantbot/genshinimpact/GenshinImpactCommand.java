package com.github.pseudoresonance.resonantbot.genshinimpact;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.HashSet;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.genshinimpact.PromotionalCodes.RegionalCodes;
import com.github.pseudoresonance.resonantbot.language.Language;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GenshinImpactCommand extends Command {

	private PromotionalCodes pc;
	
	public GenshinImpactCommand(PromotionalCodes pc) {
		super();
		this.pc = pc;
	}

	@Override
	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		RegionalCodes rc = pc.getLatestCodes();
		Language lang = LanguageManager.getLanguage(e);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(76, 166, 203));
		embed.setTitle(lang.getMessage("genshinImpact.latestCode"));
		if (rc != null) {
			embed.setDescription(rc.rewards);
			String codes = lang.getMessage("genshinImpact.eu", rc.eu);
			codes += "\n" + lang.getMessage("genshinImpact.na", rc.na);
			codes += "\n" + lang.getMessage("genshinImpact.sea", rc.sea);
			embed.addField(lang.getMessage("genshinImpact.codes"), codes, true);
		} else {
			String codes = lang.getMessage("genshinImpact.noCode");
			embed.addField(lang.getMessage("genshinImpact.codes"), codes, true);
		}
		embed.setTimestamp(LocalDateTime.now());
		embed.setFooter("https://www.gensh.in/events/promotion-codes");
		e.getTextChannel().sendMessage(embed.build()).queue();
	}

}
