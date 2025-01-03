package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

/*
*	HEG_OssumLastname <lastname>
*   Taken from PAGSM!!!
*/

public class HEG_Fuel extends BaseCommandPlugin {

	protected InteractionDialogAPI dialog;
	protected Map<String, MemoryAPI> memoryMap;


	@Override
	public boolean execute(String ruleId, final InteractionDialogAPI dialog, List<Misc.Token> params, final Map<String, MemoryAPI> memoryMap) {
		this.dialog = dialog;
		this.memoryMap = memoryMap;
		final MemoryAPI memory = getEntityMemory(memoryMap);
		final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
		String cmd = null;

		cmd = params.get(0).getString(memoryMap);
		String param = null;
		if (params.size() > 1) {
			param = params.get(1).getString(memoryMap);
		}

		final TextPanelAPI text = dialog.getTextPanel();

		PersonAPI isaias = Global.getSector().getImportantPeople().getPerson("hegtales_isaias");
		PersonAPI defectum = Global.getSector().getImportantPeople().getPerson("hegtales_defectum");

		switch (cmd) {
			case "defectumPortrait":
				defectum.setPortraitSprite(Global.getSettings().getSpriteName("portraits", param));
				return true;
			case "isaiasPortrait":
				isaias.setPortraitSprite(Global.getSettings().getSpriteName("portraits", param));
			default:
				return true;
		}
	}
}

