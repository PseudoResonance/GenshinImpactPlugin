package com.github.pseudoresonance.resonantbot.genshinimpact;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;

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
		List<RegionalCodes> rcList = pc.getCodes();
		Language lang = LanguageManager.getLanguage(e);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(76, 166, 203));
		if (!rcList.isEmpty()) {
			int i = 1;
			if (args.length > 0) {
				try {
					i = Integer.valueOf(args[0]);
					if (i < 1)
						i = 1;
					else if (i > rcList.size())
						i = rcList.size();
				} catch (NumberFormatException ignore) {}
			}
			embed.setTitle(lang.getMessage("genshinImpact.latestCode", i));
			RegionalCodes rc = rcList.get(rcList.size() - i);
			embed.setDescription(PromotionalCodes.giftUrl + "\n" + rc.rewards);
			String codes = lang.getMessage("genshinImpact.eu", rc.eu);
			codes += "\n" + lang.getMessage("genshinImpact.na", rc.na);
			codes += "\n" + lang.getMessage("genshinImpact.sea", rc.sea);
			embed.addField(lang.getMessage("genshinImpact.codes"), codes, true);
		} else {
			embed.setTitle(lang.getMessage("genshinImpact.noCode"));
		}
		embed.setTimestamp(pc.getLastCheck());
		embed.setFooter(PromotionalCodes.url);
		e.getTextChannel().sendMessage(embed.build()).queue();
	}

}
