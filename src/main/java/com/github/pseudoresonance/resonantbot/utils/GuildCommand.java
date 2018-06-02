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
import com.github.pseudoresonance.resonantbot.api.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
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
		build.setTitle("Guild Statistics");
		String url = guild.getIconUrl();
		if (url != null)
			build.setThumbnail(url);
		build.setColor(new Color(24, 226, 132));
		String info = "";
		info  += "Guild ID: `" + guild.getId() + "`" + "\n";
		User owner = guild.getOwner().getUser();
		info  += "Owner: `" + owner.getName() + "#" + owner.getDiscriminator() + " (" + owner.getId() + ")`" + "\n";
		List<Member> members = guild.getMembers();
		int bots = 0;
		int membersSize = members.size();
		for (Member member : members) {
			if (member.getUser().isBot())
				bots++;
		}
		info  += "Members: `" + membersSize + " (" + bots + " Bots | " + new BigDecimal(String.valueOf(bots / Double.valueOf(membersSize) * 100.0)).setScale(2, RoundingMode.HALF_UP) + "%)`" + "\n";
		info  += "Roles: `" + guild.getRoles().size() + "`" + "\n";
		info  += "Region: `" + guild.getRegion().getName() + "`" + "\n";
		String verification = guild.getVerificationLevel().toString();
		String[] verificationSplit = verification.split("_");
		String veriRet = "";
		for (String part : verificationSplit) {
			veriRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		veriRet = veriRet.substring(0, veriRet.length() - 1);
		info  += "Verification Level: `" + veriRet + "`" + "\n";
		String explicit = guild.getExplicitContentLevel().toString();
		String[] explicitSplit = explicit.split("_");
		String explRet = "";
		for (String part : explicitSplit) {
			explRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		explRet = explRet.substring(0, explRet.length() - 1);
		info  += "Content Level: `" + explRet + "`" + "\n";
		Channel afkC = guild.getAfkChannel();
		if (afkC == null)
			info  += "AFK: `None`" + "\n";
		else {
			info  += "AFK Channel: `" + afkC.getName() + "`" + "\n";
			info  += "AFK Timeout: `" + guild.getAfkTimeout().getSeconds() + " Seconds`" + "\n";
		}
		OffsetDateTime create = guild.getCreationTime();
		info += "Created: `" + create.format(DateTimeFormatter.ofPattern("uuuu/MM/d k:mm:ss")) + " (" + ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault())) + " days ago)`";
		build.addField(guild.getName(), info, false);
		e.getChannel().sendMessage(build.build()).queue();
	}

	public String getDesc() {
		return "Show Discord guild info";
	}

	public boolean isHidden() {
		return false;
	}

}
