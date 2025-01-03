package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.PerseanLeagueMembership;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.events.HALuddicPathDealFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.HegemonyHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.LuddicPathHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMission;
import com.fs.starfarer.api.loading.PersonMissionSpec;
import com.fs.starfarer.api.util.Misc.Token;
import data.scripts.quests.hegtales_Quest1;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.impl.campaign.intel.events.HegtaleshegemonyPeace;
import java.util.Random;

// enables us to show some intel before mission has been started
public class hegShowQuestIntel extends BaseCommandPlugin {

	protected SectorEntityToken other;
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;

		OptionPanelAPI options = dialog.getOptionPanel();
		TextPanelAPI text = dialog.getTextPanel();
		CampaignFleetAPI pf = Global.getSector().getPlayerFleet();
		other = dialog.getInteractionTarget();
		CargoAPI cargo = pf.getCargo();
		String action = params.get(0).getString(memoryMap);
		MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
		if (memory == null) return false;
		StarSystemAPI system = null;
		if (dialog.getInteractionTarget().getContainingLocation() instanceof StarSystemAPI) {
			system = (StarSystemAPI) dialog.getInteractionTarget().getContainingLocation();
		}
		if ("stopAIstuff".equals(action)) {
			HegemonyHostileActivityFactor.avertInspectionIfNotInProgress();
			HegemonyHostileActivityFactor.setPlayerDefeatedHegemony();
			HegemonyHostileActivityFactor.isPlayerDefeatedHegemony();

			HostileActivityEventIntel ha = HostileActivityEventIntel.get();
			if (ha != null) {
				int points = -1 * Global.getSettings().getInt("HEGTALES_aiuse");
				HegtaleshegemonyPeace factor = new HegtaleshegemonyPeace(points);
				ha.addFactor(factor, dialog);
			}
		}

		return false;
	}


}



