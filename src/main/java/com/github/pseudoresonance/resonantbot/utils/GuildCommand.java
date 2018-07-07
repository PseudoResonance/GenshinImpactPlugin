package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.Language;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.listeners.MessageListener;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GuildCommand implements Command {

	public void onCommand(MessageReceivedEvent e, String command, String[] args) {
		Guild guild = e.getGuild();
		if (args.length > 0) {
			try {
				long id = Long.valueOf(args[0]);
				guild = e.getJDA().getGuildById(id);
				if (guild == null)
					guild = e.getGuild();
			} catch (NumberFormatException ex) {}
		}
		EmbedBuilder build = new EmbedBuilder();
		build.setTitle(Language.getMessage(e.getGuild().getIdLong(), "utils.guildStatistics"));
		String url = guild.getIconUrl();
		if (url != null)
			build.setThumbnail(url);
		build.setColor(new Color(24, 226, 132));
		String info = "";
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.guildStatistics", guild.getId()) + "\n";
		User owner = guild.getOwner().getUser();
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.ownerID", Language.escape(owner.getName()) + "#" + owner.getDiscriminator(), owner.getId()) + "\n";
		List<Member> members = guild.getMembers();
		int bots = 0;
		int membersSize = members.size();
		for (Member member : members) {
			if (member.getUser().isBot())
				bots++;
		}
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.members", membersSize, bots, new BigDecimal(String.valueOf(bots / Double.valueOf(membersSize) * 100.0)).setScale(2, RoundingMode.HALF_UP)) + "\n";
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.region", guild.getRegion().getName()) + "\n";
		String verification = guild.getVerificationLevel().toString();
		String[] verificationSplit = verification.split("_");
		String veriRet = "";
		for (String part : verificationSplit) {
			veriRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		veriRet = veriRet.substring(0, veriRet.length() - 1);
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.verificationLevel", veriRet) + "\n";
		String explicit = guild.getExplicitContentLevel().toString();
		String[] explicitSplit = explicit.split("_");
		String explRet = "";
		for (String part : explicitSplit) {
			explRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		explRet = explRet.substring(0, explRet.length() - 1);
		info  += Language.getMessage(e.getGuild().getIdLong(), "utils.contentLevel", explRet) + "\n";
		Channel afkC = guild.getAfkChannel();
		if (afkC == null)
			info  += Language.getMessage(e.getGuild().getIdLong(), "utils.afkNone") + "\n";
		else {
			info  += Language.getMessage(e.getGuild().getIdLong(), "utils.afkChannel", Language.escape(afkC.getName())) + "\n";
			info  += Language.getMessage(e.getGuild().getIdLong(), "utils.afkChannel", guild.getAfkTimeout().getSeconds()) + "\n";
		}
		if (e.getAuthor().getIdLong() == Config.getOwner()) {
			String prefix = MessageListener.getPrefix(guild);
			info  += Language.getMessage(e.getGuild().getIdLong(), "utils.prefix", prefix) + "\n";
		}
		OffsetDateTime create = guild.getCreationTime();
		info += Language.getMessage(e.getGuild().getIdLong(), "utils.created", create.format(DateTimeFormatter.ofPattern(Language.getDateTimeFormat(e.getGuild().getIdLong()))), ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault()))) +  "\n";
		String roles = "";
		List<Role> roleList = guild.getRoles();
		for (int i = 0; i < roleList.size(); i++)
			if (i == 0)
				roles += Language.escape(roleList.get(i).getName());
			else if (!roleList.get(i).isPublicRole())
				roles += ", " + Language.escape(roleList.get(i).getName());
		if (!roles.equals(""))
			info += Language.getMessage(e.getGuild().getIdLong(), "utils.roles", guild.getRoles().size(), roles);
		else
			info += Language.getMessage(e.getGuild().getIdLong(), "utils.rolesNone");
		build.addField(guild.getName(), info, false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc(long guildID) {
		return Language.getMessage(guildID, "utils.guildCommandDescription");
	}

	public boolean isHidden() {
		return false;
	}

}
