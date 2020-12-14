package com.github.pseudoresonance.resonantbot.genshinimpact;

import java.awt.Color;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.pseudoresonance.resonantbot.ResonantBot;
import com.github.pseudoresonance.resonantbot.api.Plugin;
import com.github.pseudoresonance.resonantbot.apiplugin.RequestTimeoutException;
import com.github.pseudoresonance.resonantbot.data.Column;
import com.github.pseudoresonance.resonantbot.data.Data;
import com.github.pseudoresonance.resonantbot.data.DynamicTable;
import com.github.pseudoresonance.resonantbot.language.Language;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class PromotionalCodes {

	public final static String url = "https://www.gensh.in/events/promotion-codes";
	public final static String giftUrl = "https://genshin.mihoyo.com/en/gift";

	private ScheduledExecutorService ses;
	private DynamicTable codeTable;

	private final List<RegionalCodes> codes = new ArrayList<RegionalCodes>();
	private final List<RegionalCodes> codesImmutable = Collections.unmodifiableList(codes);
	private int codeCount = 0;
	private ZonedDateTime lastCheck = ZonedDateTime.now();
	
	private long channelId = 762048993831550987L;
	private long guildId = 659641518512013313L;

	public PromotionalCodes(GenshinImpact plugin) {
		ses = Executors.newSingleThreadScheduledExecutor();
		Calendar time = Calendar.getInstance();
		int hour = (time.get(Calendar.HOUR_OF_DAY) / 2 + 1) * 2;
		time.set(Calendar.HOUR_OF_DAY, hour >= 24 ? 0 : hour);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		setupStorage(plugin);
		checkCodes();
		ses.scheduleAtFixedRate(this::checkCodes, time.getTimeInMillis() - System.currentTimeMillis(), 2 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		ses.shutdownNow();
	}
	
	private void setupStorage(Plugin plugin) {
		codeTable = new DynamicTable("genshin_impact_codes", "BIGINT UNSIGNED");
		codeTable.setup();
		Data.addTable(plugin, codeTable);
		codeTable.addColumn(new Column("rewards", "VARCHAR(128)", null));
		codeTable.addColumn(new Column("eu", "VARCHAR(32)", null));
		codeTable.addColumn(new Column("na", "VARCHAR(32)", null));
		codeTable.addColumn(new Column("sea", "VARCHAR(32)", null));
		codeTable.update();
		readCodes();
	}
	
	private void readCodes() {
		Object o = Data.getBotSetting("genshin_impact_code_count");
		if (o == null) {
			Data.setBotSetting("genshin_impact_code_count", 0);
			codeCount = 0;
			return;
		} else {
			int n = Integer.valueOf((String) o);
			codeCount = n;
			for (int i = 1; i <= n; i++) {
				HashMap<String, Object> data = codeTable.getSettings(String.valueOf(i));
				if (data != null) {
					RegionalCodes rc = new RegionalCodes((String) data.get("rewards"), (String) data.get("eu"), (String) data.get("na"), (String) data.get("sea"));
					codes.add(rc);
				}
			}
		}
	}
	
	protected RegionalCodes getLatestCodes() {
		if (codes.size() > 1)
			return codes.get(codes.size() - 1);
		return null;
	}
	
	protected List<RegionalCodes> getCodes() {
		return codesImmutable;
	}
	
	protected ZonedDateTime getLastCheck() {
		return lastCheck;
	}
	
	private void saveCode(RegionalCodes rc) {
		codeCount++;
		Data.setBotSetting("genshin_impact_code_count", codeCount);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("rewards", rc.rewards);
		data.put("eu", rc.eu);
		data.put("na", rc.na);
		data.put("sea", rc.sea);
		codeTable.setSettings(String.valueOf(codeCount), data);
	}
	
	private void newCodes(List<RegionalCodes> newCodes) {
		for (RegionalCodes rc : newCodes) {
			TextChannel ch = ResonantBot.getBot().getJDA().getGuildById(guildId).getTextChannelById(channelId);
			Language lang = LanguageManager.getLanguage(guildId);
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(new Color(76, 166, 203));
			embed.setDescription("https://genshin.mihoyo.com/en/gift\n" + rc.rewards);
			embed.setTitle(lang.getMessage("genshinImpact.newCode"));
			String codes = lang.getMessage("genshinImpact.eu", rc.eu);
			codes += "\n" + lang.getMessage("genshinImpact.na", rc.na);
			codes += "\n" + lang.getMessage("genshinImpact.sea", rc.sea);
			embed.addField(lang.getMessage("genshinImpact.codes"), codes, true);
			embed.setTimestamp(lastCheck);
			embed.setFooter("https://www.gensh.in/events/promotion-codes");
			ch.sendMessage(embed.build()).queue();
			saveCode(rc);
		}
	}

	private void checkCodes() {
		List<RegionalCodes> newCodes = new ArrayList<RegionalCodes>();
		try (WebClient client = new WebClient()) {
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			HtmlPage page = client.getPage(url);
			int rewardsI = 1;
			int euI = 3;
			int naI = 4;
			int seaI = 5;
			List<HtmlElement> headers = page.getByXPath("//section[@class='content']/div[@class='row']//table/thead/tr/th");
			for (int i = 0; i < headers.size(); i++) {
				switch (headers.get(i).asText().trim().toLowerCase()) {
				case "rewards":
					rewardsI = i + 1;
					break;
				case "eu":
					euI = i + 1;
					break;
				case "na":
					naI = i + 1;
					break;
				case "sea":
					seaI = i + 1;
					break;
				}
			}
			List<HtmlElement> codesRewards = page.getByXPath("//section[@class='content']/div[@class='row']//table/tbody/tr/td[" + rewardsI + "]");
			List<HtmlElement> codesEu = page.getByXPath("//section[@class='content']/div[@class='row']//table/tbody/tr/td[" + euI + "]");
			List<HtmlElement> codesNa = page.getByXPath("//section[@class='content']/div[@class='row']//table/tbody/tr/td[" + naI + "]");
			List<HtmlElement> codesSea = page.getByXPath("//section[@class='content']/div[@class='row']//table/tbody/tr/td[" + seaI + "]");
			for (int i = codesEu.size() - 1; i >= 0; i--) {
				String rewards = codesRewards.get(i).asText().trim();
				String eu = codesEu.get(i).asText().trim();
				String na = codesNa.get(i).asText().trim();
				String sea = codesSea.get(i).asText().trim();
				RegionalCodes rc = new RegionalCodes(rewards, eu, na, sea);
				if (!codes.contains(rc)) {
					codes.add(rc);
					newCodes.add(rc);
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			throw new RuntimeException(e);
		} catch (RequestTimeoutException e) {
			throw e;
		}
		lastCheck = ZonedDateTime.now();
		if (newCodes.size() > 0)
			newCodes(newCodes);
	}
	
	public class RegionalCodes {
		
		public final String rewards;
		public final String eu;
		public final String na;
		public final String sea;
		
		private RegionalCodes(String rewards, String eu, String na, String sea) {
			this.rewards = rewards;
			this.eu = eu;
			this.na = na;
			this.sea = sea;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((eu == null) ? 0 : eu.hashCode());
			result = prime * result + ((na == null) ? 0 : na.hashCode());
			result = prime * result + ((sea == null) ? 0 : sea.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RegionalCodes other = (RegionalCodes) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (eu == null) {
				if (other.eu != null)
					return false;
			} else if (!eu.equals(other.eu))
				return false;
			if (na == null) {
				if (other.na != null)
					return false;
			} else if (!na.equals(other.na))
				return false;
			if (sea == null) {
				if (other.sea != null)
					return false;
			} else if (!sea.equals(other.sea))
				return false;
			return true;
		}

		private PromotionalCodes getEnclosingInstance() {
			return PromotionalCodes.this;
		}
	}

}
