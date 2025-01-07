package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/**
 * hegtalesIsMarketFaction <faction id>
 * Totally not a copy of Ironshell - SWP's version.
 */

public class hegtalesIsMarketFaction extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = dialog.getInteractionTarget().getMarket();

        if (market == null) {
            return true;
        }
        else {
            String factionId = ((Misc.Token)params.get(0)).getString(memoryMap);
            return market.getFaction().getId().contentEquals(factionId);
        }
    }
}