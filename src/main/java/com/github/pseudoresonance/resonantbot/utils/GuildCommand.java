package com.github.pseudoresonance.resonantbot.utils;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.github.pseudoresonance.resonantbot.Config;
import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.data.Data;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GuildCommand extends Command {

	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		Guild guild = null;
		if (e.getChannelType() == ChannelType.PRIVATE) {
			if (args.length == 0) {
				e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.validGuild")).queue();
				return;
			} else {
				try {
					long id = Long.valueOf(args[0]);
					guild = e.getJDA().getGuildById(id);
					if (guild == null) {
						e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.validGuild")).queue();
						return;
					}
				} catch (NumberFormatException ex) {
					e.getChannel().sendMessage(LanguageManager.getLanguage(e).getMessage("utils.validGuild")).queue();
					return;
				}
			}
		} else {
			guild = e.getGuild();
			if (args.length > 0) {
				try {
					long id = Long.valueOf(args[0]);
					guild = e.getJDA().getGuildById(id);
					if (guild == null)
						guild = e.getGuild();
				} catch (NumberFormatException ex) {}
			}
		}
		EmbedBuilder build = new EmbedBuilder();
		build.setTitle(LanguageManager.getLanguage(e).getMessage("utils.guildStatistics"));
		String url = guild.getIconUrl();
		if (url != null)
			build.setThumbnail(url);
		build.setColor(new Color(24, 226, 132));
		String info = "";
		info  += LanguageManager.getLanguage(e).getMessage("utils.guildStatistics", guild.getId()) + "\n";
		User owner = guild.getOwner().getUser();
		info  += LanguageManager.getLanguage(e).getMessage("utils.ownerID", LanguageManager.escape(owner.getName()) + "#" + owner.getDiscriminator(), owner.getId()) + "\n";
		try {
			guild.retrieveMembers().get();
		} catch (InterruptedException | ExecutionException e1) {
		}
		info  += LanguageManager.getLanguage(e).getMessage("utils.members", guild.getMemberCount()) + "\n";
		info  += LanguageManager.getLanguage(e).getMessage("utils.region", guild.getRegion().getName()) + "\n";
		String verification = guild.getVerificationLevel().toString();
		String[] verificationSplit = verification.split("_");
		String veriRet = "";
		for (String part : verificationSplit) {
			veriRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		veriRet = veriRet.substring(0, veriRet.length() - 1);
		info  += LanguageManager.getLanguage(e).getMessage("utils.verificationLevel", veriRet) + "\n";
		String explicit = guild.getExplicitContentLevel().toString();
		String[] explicitSplit = explicit.split("_");
		String explRet = "";
		for (String part : explicitSplit) {
			explRet += part.substring(0, 1).toUpperCase() + part.substring(1, part.length()).toLowerCase() + " ";
		}
		explRet = explRet.substring(0, explRet.length() - 1);
		info  += LanguageManager.getLanguage(e).getMessage("utils.contentLevel", explRet) + "\n";
		VoiceChannel afkC = guild.getAfkChannel();
		if (afkC == null)
			info  += LanguageManager.getLanguage(e).getMessage("utils.afkNone") + "\n";
		else {
			info  += LanguageManager.getLanguage(e).getMessage("utils.afkChannel", LanguageManager.escape(afkC.getName())) + "\n";
			info  += LanguageManager.getLanguage(e).getMessage("utils.afkChannel", guild.getAfkTimeout().getSeconds()) + "\n";
		}
		if (e.getAuthor().getIdLong() == Config.getOwner()) {
			String prefix = Data.getGuildPrefix(guild.getIdLong());
			info  += LanguageManager.getLanguage(e).getMessage("utils.prefix", prefix) + "\n";
		}
		OffsetDateTime create = guild.getTimeCreated();
		info += LanguageManager.getLanguage(e).getMessage("utils.created", LanguageManager.getLanguage(e).formatDateTime(create.toLocalDateTime()), ChronoUnit.DAYS.between(create, Instant.now().atZone(ZoneId.systemDefault()))) +  "\n";
		String roles = "";
		List<Role> roleList = guild.getRoles();
		for (int i = 0; i < roleList.size(); i++)
			if (i == 0)
				roles += LanguageManager.escape(roleList.get(i).getName());
			else if (!roleList.get(i).isPublicRole())
				roles += ", " + LanguageManager.escape(roleList.get(i).getName());
		if (!roles.equals(""))
			info += LanguageManager.getLanguage(e).getMessage("utils.roles", guild.getRoles().size(), roles);
		else
			info += LanguageManager.getLanguage(e).getMessage("utils.rolesNone");
		build.addField(guild.getName(), info, false);
		e.getChannel().sendMessage(build.build()).queue();
	}
	
}
