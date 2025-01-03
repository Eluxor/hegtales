package com.fs.starfarer.api.impl.campaign.intel.events;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator;

public class HegtaleshegemonyPeace extends BaseOneTimeFactor{
    public HegtaleshegemonyPeace(int points) {
        super(points);
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Scion of House Ossum";
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Your connections with House Ossum have made the Hegemony turn a blind eye to your AI use.",
                        0f);
            }

        };
    }

}

