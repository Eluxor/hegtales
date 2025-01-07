package data.scripts.customstart;

import java.util.Map;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.CharacterCreationData;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.NGCAddStandardStartingScript;
import com.fs.starfarer.api.util.Misc;
import data.scripts.ids.hegtalesIDs;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.customstart.CustomStart;
import java.awt.Color;

import exerelin.campaign.ExerelinSetupData;

import exerelin.utilities.StringHelper;

public class hegtalescustomstart extends CustomStart {
private static final Color HIGHLIGHT_COLOR = Global.getSettings().getColor("buttonShortcut");

    @Override
    public void execute(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {

        CharacterCreationData data = (CharacterCreationData) memoryMap.get(MemKeys.LOCAL).get("$characterData");


        if (Global.getSettings().getMissionScore("retaliation") < 80) {
            dialog.getTextPanel().addParagraph("This start option is currently locked. Complete the " +
                    "\"Loyal to the End\" combat mission with a score of at least 80% to unlock it.", Misc.getNegativeHighlightColor());
            dialog.getTextPanel().highlightInLastPara(Misc.getHighlightColor(), "locked", "\"Loyal to the End\"");
            dialog.getOptionPanel().addOption(StringHelper.getString("back", true), "nex_NGCStartBack");
            return;
        }

        PlayerFactionStore.setPlayerFactionIdNGC(Factions.HEGEMONY);

        CampaignFleetAPI tempFleet = FleetFactoryV3.createEmptyFleet(
                PlayerFactionStore.getPlayerFactionIdNGC(), FleetTypes.PATROL_SMALL, null);

        addFleetMember("heg_retaliation_standard", dialog, data, tempFleet, "flagship");
        addFleetMember("enforcer_XIV_Elite", dialog, data, tempFleet, "enforcer");
        addFleetMember("lasher_CS", dialog, data, tempFleet, "rightjab");
        addFleetMember("lasher_CS", dialog, data, tempFleet, "leftjab");
        addFleetMember("buffalo_hegemony_Standard", dialog, data, tempFleet, "milk");
        addFleetMember("dram_Light", dialog, data, tempFleet, "fuel");

        if (Global.getSettings().getModManager().isModEnabled("timid_xiv"))   {
            dialog.getTextPanel().addParagraph("Gained skill: XIV stuff", Misc.getPositiveHighlightColor());
            dialog.getTextPanel().highlightInLastPara(Global.getSettings().getSkillSpec("eis_xiv").getGoverningAptitudeColor().brighter(), "Iron Heritage");
        }

        data.getStartingCargo().getCredits().add(150000);
        AddRemoveCommodity.addCreditsGainText(150000, dialog.getTextPanel());

        tempFleet.getFleetData().setSyncNeeded();
        tempFleet.getFleetData().syncIfNeeded();
        tempFleet.forceSync();

        int crew = 0;
        int fuel = 0;
        int supplies = 0;
        for (FleetMemberAPI member : tempFleet.getFleetData().getMembersListCopy()) {
            crew += member.getMinCrew() + (int) ((member.getMaxCrew() - member.getMinCrew()) * 0.3f);
            fuel += (int) member.getFuelCapacity() * 0.5f;
            supplies += (int) member.getBaseDeploymentCostSupplies() * 3;
        }
        data.getStartingCargo().addItems(CargoAPI.CargoItemType.RESOURCES, Commodities.CREW, crew);
        data.getStartingCargo().addItems(CargoAPI.CargoItemType.RESOURCES, Commodities.FUEL, fuel);
        data.getStartingCargo().addItems(CargoAPI.CargoItemType.RESOURCES, Commodities.SUPPLIES, supplies);
        data.getStartingCargo().addItems(CargoAPI.CargoItemType.RESOURCES, Commodities.HEAVY_MACHINERY, 40);

        AddRemoveCommodity.addCommodityGainText(Commodities.CREW, crew, dialog.getTextPanel());
        AddRemoveCommodity.addCommodityGainText(Commodities.FUEL, fuel, dialog.getTextPanel());
        AddRemoveCommodity.addCommodityGainText(Commodities.SUPPLIES, supplies, dialog.getTextPanel());
        AddRemoveCommodity.addCommodityGainText(Commodities.HEAVY_MACHINERY, 40, dialog.getTextPanel());

        PlayerFactionStore.setPlayerFactionIdNGC(Factions.HEGEMONY);
        ExerelinSetupData.getInstance().freeStart = true;


        data.addScript(new Script() {
            public void run() {
                CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();

                // SEQUENCE BREAK!
                Global.getSector().getMemoryWithoutUpdate().set(hegtalesIDs.HEGTALES_FINISHED_QUEST1, true);
                Global.getSector().getMemoryWithoutUpdate().set(hegtalesIDs.HEGTALES_FINISHED_QUEST2, true);
                Global.getSector().getMemoryWithoutUpdate().set(hegtalesIDs.HEGTALES_FINISHED_QUEST3, true);
                Global.getSector().getMemoryWithoutUpdate().set(hegtalesIDs.OF_HOUSE_OSSUM, true);
                Global.getSector().getFaction(Factions.HEGEMONY).setRelationship(Factions.PLAYER, RepLevel.COOPERATIVE);
                Global.getSector().getFaction(Factions.LUDDIC_CHURCH).setRelationship(Factions.PLAYER, RepLevel.FRIENDLY);
                Global.getSector().getFaction(Factions.LUDDIC_PATH).setRelationship(Factions.PLAYER, RepLevel.NEUTRAL);
                Global.getSector().getMemoryWithoutUpdate().set("$hegtales_debris_generated", true);

                NGCAddStandardStartingScript.adjustStartingHulls(fleet);

                fleet.getFleetData().ensureHasFlagship();

                for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
                    if (member.getVariant().hasHullMod(HullMods.DISTRIBUTED_FIRE_CONTROL)) {
                        // she doesn't actually exist yet (SotfPeople is run later than this code) so let's get her generated
                        member.setShipName("HSS Retribution");
                    }
                }

                for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
                    float max = member.getRepairTracker().getMaxCR();
                    member.getRepairTracker().setCR(max);
                }
                fleet.getFleetData().setSyncNeeded();
            }
        });


        dialog.getVisualPanel().showFleetInfo(StringHelper.getString("exerelin_ngc", "playerFleet", true),
                tempFleet, null, null);


        dialog.getTextPanel().addPara("You start with the Ancestral ship of the House Ossum of Eventide.\n\nFind and speak with your Uncle Borealis to get access to the rest of the family and their interactions!",
                HIGHLIGHT_COLOR, Misc.ucFirst("Uncle Borealis"));

        FireBest.fire(null, dialog, memoryMap, "ExerelinNGCStep4");
    }

    public void addFleetMember(String vid, InteractionDialogAPI dialog, CharacterCreationData data, CampaignFleetAPI fleet, String special) {
        data.addStartingFleetMember(vid, FleetMemberType.SHIP);
        FleetMemberAPI temp = Global.getFactory().createFleetMember(FleetMemberType.SHIP, vid);
        fleet.getFleetData().addFleetMember(temp);
        temp.getRepairTracker().setCR(0.7f);

        if (special.equals("flagship")) {
            fleet.getFleetData().setFlagship(temp);
            temp.setShipName("HSS Retribution");
            temp.setCaptain(data.getPerson());
        }
        else {
            AddRemoveCommodity.addFleetMemberGainText(temp.getVariant(), dialog.getTextPanel());
        }
    }

}