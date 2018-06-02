package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
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
				e.getChannel().sendMessage("Please use a valid user mention or ID! " + idString).queue();
				return;
			}
		}
		RestAction<User> userRA = e.getJDA().retrieveUserById(id);
		User user = userRA.complete();
		if (user != null) {
			EmbedBuilder build = new EmbedBuilder();
			Member member = e.getGuild().getMember(user);
			build.setTitle("User Statistics");
			build.setThumbnail(user.getEffectiveAvatarUrl());
			build.setColor(new Color(51, 214, 195));
			String info = "";
			info  += "User ID: `" + user.getId() + "`";
			if (e.getGuild().isMember(user)) {
				build.setColor(member.getColorRaw());
				String nick = member.getNickname();
				if (nick != null)
					info  += "\n" + "Nickname: `" + nick + "`";
				else
					info  += "\n" + "Nickname: `None`";
			}
			if (user.isBot())
				info  += "\n" + "Account Type: `Bot`";
			else if (user.isFake())
				info  += "\n" + "Account Type: `Fake`";
			else
				info  += "\n" + "Account Type: `User`";
			OffsetDateTime create = user.getCreationTime();
			info += "\n" + "Created: `" + create.format(DateTimeFormatter.ofPattern("uuuu/MM/d k:mm:ss")) + " (" + ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault())) + " days ago)`";
			if (e.getGuild().isMember(user)) {
				OffsetDateTime join = member.getJoinDate();
				info += "\n" + "Joined: `" + join.format(DateTimeFormatter.ofPattern("uuuu/MM/d k:mm:ss")) + " (" + ChronoUnit.DAYS.between(join, Instant.now().atZone(ZoneId.systemDefault())) + " days ago)`";
				String roles = "";
				List<Role> roleList = member.getRoles();
				for (int i = 0; i < roleList.size(); i++)
					if (i == 0)
						roles += roleList.get(i).getName();
					else
						roles += ", " + roleList.get(i).getName();
				if (!roles.equals(""))
					info += "\n" + "Roles: `" + roles + "`";
				else
					info += "\n" + "Roles: `None`";
			}
			build.addField(user.getName() + "#" + user.getDiscriminator(), info, false);
			e.getChannel().sendMessage(build.build()).queue();
			return;
		}
		e.getChannel().sendMessage("Please add a valid user mention or ID!").queue();
	}

	public String getDesc() {
		return "Show Discord user info";
	}

	public boolean isHidden() {
		return false;
	}

}
