package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

public class UserCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		String[] split = e.getMessage().getContentRaw().split(" ");
		long id = e.getAuthor().getIdLong();
		if (split.length > 1) {
			String idString = split[1];
			if (split[1].matches("<@(.[0-9]*)>"))
				idString = split[1].substring(2, split[1].length() - 1).replaceAll("!", "");
			try {
				id = Long.valueOf(idString);
			} catch (NumberFormatException ex) {
				e.getChannel().sendMessage(Language.getMessage(e, "utils.validMentionID")).queue();
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
			build.setTitle(Language.getMessage(e, "utils.userStatistics"));
			build.setThumbnail(user.getEffectiveAvatarUrl());
			build.setColor(new Color(51, 214, 195));
			String info = "";
			info  += Language.getMessage(e, "utils.userID", user.getId());
			if (e.getChannelType() != ChannelType.PRIVATE && e.getGuild().isMember(user)) {
				build.setColor(member.getColorRaw());
				String nick = member.getNickname();
				if (nick != null)
					info  += "\n" + Language.getMessage(e, "utils.nickname", Language.escape(nick));
			}
			if (user.isBot())
				info  += "\n" + Language.getMessage(e, "utils.accountTypeBot");
			else
				info  += "\n" + Language.getMessage(e, "utils.accountTypeUser");
			OffsetDateTime create = user.getCreationTime();
			Long guildID = 0L;
			if (e.getChannelType() == ChannelType.PRIVATE)
				guildID = e.getPrivateChannel().getIdLong();
			else
				guildID = e.getGuild().getIdLong();
			info += "\n" + Language.getMessage(e, "utils.joinedDiscord", create.format(DateTimeFormatter.ofPattern(Language.getDateTimeFormat(guildID))), ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault())));
			if (e.getChannelType() != ChannelType.PRIVATE) {
				if (e.getGuild().isMember(user)) {
					OffsetDateTime join = member.getJoinDate();
					info += "\n" + Language.getMessage(e, "utils.joinedGuild", join.format(DateTimeFormatter.ofPattern(Language.getDateTimeFormat(e.getGuild().getIdLong()))), ChronoUnit.DAYS.between(join, Instant.now().atZone(ZoneId.systemDefault())));
					String roles = "";
					List<Role> roleList = member.getRoles();
					for (int i = 0; i < roleList.size(); i++)
						if (i == 0)
							roles += Language.escape(roleList.get(i).getName());
						else
							roles += ", " + Language.escape(roleList.get(i).getName());
					if (!roles.equals(""))
						info += "\n" + Language.getMessage(e, "utils.roles", roleList.size(), roles);
					else
						info += "\n" + Language.getMessage(e, "utils.rolesNone");
				}
			}
			build.addField(Language.escape(user.getName()) + "#" + user.getDiscriminator(), info, false);
			e.getChannel().sendMessage(build.build()).queue();
			return;
		}
		e.getChannel().sendMessage(Language.getMessage(e, "utils.validMentionID")).queue();
	}

	public String getDesc(long id) {
		return Language.getMessage(id, "utils.userCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
