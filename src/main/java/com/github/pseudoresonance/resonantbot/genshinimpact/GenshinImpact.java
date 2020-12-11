package com.github.pseudoresonance.resonantbot.genshinimpact;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.api.Plugin;

public class GenshinImpact extends Plugin {
	
	private PromotionalCodes pc;

	public void onEnable() {
		pc = new PromotionalCodes(this);
		CommandManager.registerCommand(this, new GenshinImpactCommand(pc), "genshinimpact", "genshinImpact.genshinImpactCommandDescription", "genshin");
	}
	
	public void onDisable() {
		if (pc != null)
			pc.stop();
	}
	
}
