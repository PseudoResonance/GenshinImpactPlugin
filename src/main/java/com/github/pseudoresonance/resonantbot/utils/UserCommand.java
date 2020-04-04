package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

public class UserCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		String[] split = e.getMessage().getContentRaw().split(" ");
		long id = e.getAuthor().getIdLong();
		if (split.length > 1) {
			String idString = split[1];
			if (split[1].matches("<@(.[0-9]*)>"))
				idString = split[1].substring(2, split[1].length() - 1).replaceAll("!", "");
			try {
				id = Long.valueOf(idString);
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.validMentionID")).queue();
				return;
			}
		}
		RestAction<User> userRA = e.getJDA().retrieveUserById(id);
		User user = userRA.complete();
		if (user != null) {
			EmbedBuilder build = new EmbedBuilder();
			Member member = null;
			if (e.getChannelType() != ChannelType.PRIVATE) {
				member = e.getGuild().getMember(user);
			}
			build.setTitle(LanguageManager.getLanguage(e).getMessage("utils.userStatistics"));
			build.setThumbnail(user.getEffectiveAvatarUrl());
			build.setColor(new Color(51, 214, 195));
			String info = "";
			info  += LanguageManager.getLanguage(e).getMessage("utils.userID", user.getId());
			if (e.getChannelType() != ChannelType.PRIVATE && e.getGuild().isMember(user)) {
				build.setColor(member.getColorRaw());
				String nick = member.getNickname();
				if (nick != null)
					info  += "\n" + LanguageManager.getLanguage(e).getMessage("utils.nickname", LanguageManager.escape(nick));
			}
			if (user.isBot())
				info  += "\n" + LanguageManager.getLanguage(e).getMessage("utils.accountTypeBot");
			else
				info  += "\n" + LanguageManager.getLanguage(e).getMessage("utils.accountTypeUser");
			OffsetDateTime create = user.getTimeCreated();
			info += "\n" + LanguageManager.getLanguage(e).getMessage("utils.joinedDiscord", LanguageManager.getLanguage(e).formatDateTime(create.toLocalDateTime()), ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault())));
			if (e.getChannelType() != ChannelType.PRIVATE) {
				if (e.getGuild().isMember(user)) {
					OffsetDateTime join = member.getTimeJoined();
					info += "\n" + LanguageManager.getLanguage(e).getMessage("utils.joinedGuild", LanguageManager.getLanguage(e).formatDateTime(join.toLocalDateTime()), ChronoUnit.DAYS.between(join, Instant.now().atZone(ZoneId.systemDefault())));
					String roles = "";
					List<Role> roleList = member.getRoles();
					for (int i = 0; i < roleList.size(); i++)
						if (i == 0)
							roles += LanguageManager.escape(roleList.get(i).getName());
						else
							roles += ", " + LanguageManager.escape(roleList.get(i).getName());
					if (!roles.equals(""))
						info += "\n" + LanguageManager.getLanguage(e).getMessage("utils.roles", roleList.size(), roles);
					else
						info += "\n" + LanguageManager.getLanguage(e).getMessage("utils.rolesNone");
				}
			}
			build.addField(LanguageManager.escape(user.getName()) + "#" + user.getDiscriminator(), info, false);
			e.getChannel().sendMessage(build.build()).queue();
			return;
		}
		e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.validMentionID")).queue();
	}

}
