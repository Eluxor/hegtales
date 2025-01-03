package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class hss_retaliation extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        BaseSalvageSpecial.clearExtraSalvage(dialog.getInteractionTarget());
        BaseSalvageSpecial.clearExtraSalvage(memoryMap);
        CargoAPI extraSalvage = Global.getFactory().createCargo(true);
        extraSalvage.addCommodity(Commodities.HAND_WEAPONS, 25f);
        extraSalvage.addFuel(55f);
        dialog.getVisualPanel().showLoot("Salvaged", extraSalvage, false, true, true, new CoreInteractionListener() {
            public void coreUIDismissed() {
            }
        });
        ShipVariantAPI variant = Global.getSettings().getVariant("heg_retaliation_damaged").clone();
        ShipRecoverySpecial.ShipRecoverySpecialData data = new ShipRecoverySpecial.ShipRecoverySpecialData(null);
        data.addShip(new ShipRecoverySpecial.PerShipData(variant, ShipCondition.AVERAGE, "HSS Retaliation", Factions.NEUTRAL, 0f));
        Misc.setSalvageSpecial(dialog.getInteractionTarget(), data);

        return true;
    }

}



