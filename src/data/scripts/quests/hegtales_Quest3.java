package data.scripts.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.missions.DelayedFleetEncounter;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.missions.RecoverAPlanetkiller;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;
import java.util.Map;


public class hegtales_Quest3 extends HubMissionWithSearch {

    public enum Stage {
        TALK_TO_CORBY,
        GO_TO_UMBRA,
        SET_THE_BEACON,
        FIGHT_ISAIAS,
        DEAL_WITH_DEFECTUM,
        FIND_RETALIATION,
        RETURN_TO_EVENTIDE_WITH_RETALIATION,
        COMPLETED,
    }

    protected PersonAPI australis;
    protected PersonAPI borealis;
    protected PersonAPI isaias;
    protected PersonAPI defectum;
    protected PersonAPI frusca;
    private PersonAPI corby;
    protected MarketAPI jangala;
    protected MarketAPI nortia;
    protected MarketAPI a4;
    protected SectorEntityToken retaliationentity;
    protected int isaiasbribeCost;

    protected boolean create(MarketAPI createdAt, boolean barEvent) {

       setName("Operation: Heritage");

        // if this mission was already accepted by the player, abort
        if (!setGlobalReference("$hegtales_Quest3_ref", "$hegtales_Quest3_inProgress")) {
            return false;
        }



        requireEntityMemoryFlags("$retaliation");
        // hamatsu could be null if player salvaged it after dipping into alpha site then backing out

        setCreditReward(CreditReward.VERY_HIGH);
        setRepRewardPerson(CoreReputationPlugin.RepRewards.EXTREME);
        setRepRewardFaction(CoreReputationPlugin.RepRewards.HIGH);

        australis = getImportantPerson("hegtales_australis");
        borealis = getImportantPerson("hegtales_borealis");
        isaias = getImportantPerson("hegtales_isaias");
        defectum = getImportantPerson("hegtales_defectum");
        corby = getImportantPerson("hegtales_corby");
        frusca = getImportantPerson("hegtales_frusca");

        if (australis == null || borealis == null || isaias == null || defectum == null || corby == null || frusca == null) return false;



        // Add ships/fleets
        // addIsaiasFleet();
        // addCluePatherAmbushFleet();

        isaiasbribeCost = genRoundNumber(100000, 200000); // payment

        // if the player destroyed Aztlan Relay before this point, uhh. Need to do something, probably.
        retaliationentity = Global.getSector().getEntityById("hegtalesretribution");
        if (retaliationentity == null) return false;

        a4 = Global.getSector().getEconomy().getMarket("umbra");
        if (a4 == null) return false;
        nortia = Global.getSector().getEconomy().getMarket("nortia");
        if (nortia == null) return false;
        jangala = Global.getSector().getEconomy().getMarket("jangala");
        if (jangala == null) return false;

        //MISSION START!
        setStoryMission();

        setStartingStage(hegtales_Quest3.Stage.TALK_TO_CORBY);
        makeImportant(corby, "$hegtales_Quest3_talk_to_corby", Stage.TALK_TO_CORBY); //IMPORTANT

        setStageOnGlobalFlag(Stage.GO_TO_UMBRA, "$hegtales_Quest3_GO_TO_UMBRA");
        makeImportant(frusca, "$hegtales_Quest3_GO_TO_UMBRA", Stage.GO_TO_UMBRA); //IMPORTANT

        setStageOnGlobalFlag(Stage.SET_THE_BEACON, "$hegtales_Quest3_SET_THE_BEACON");
        makeImportant(a4, "$hegtales_Quest3_SET_THE_BEACON", Stage.SET_THE_BEACON); //IMPORTANT

        setStageOnGlobalFlag(Stage.FIGHT_ISAIAS, "$hegtales_Quest3_FIGHT_ISAIAS");
        makeImportant(isaias, "$hegtales_Quest3_FIGHT_ISAIAS", Stage.FIGHT_ISAIAS);

        setStageOnGlobalFlag(Stage.DEAL_WITH_DEFECTUM, "$hegtales_Quest3_DEAL_WITH_DEFECTUM");
        makeImportant(defectum, "$hegtales_Quest3_DEAL_WITH_DEFECTUM", Stage.DEAL_WITH_DEFECTUM);

        setStageOnGlobalFlag(Stage.FIND_RETALIATION, "$hegtales_Quest3_FIND_RETALIATION");
        makeImportant(retaliationentity, "$hegtales_Quest3_FIND_RETALIATION", Stage.FIND_RETALIATION);

        setStageOnGlobalFlag(Stage.RETURN_TO_EVENTIDE_WITH_RETALIATION, "$hegtales_Quest3_RETURN_EVENTIDE");
        makeImportant(borealis, "$hegtales_Quest3_RETURN_EVENTIDE", Stage.RETURN_TO_EVENTIDE_WITH_RETALIATION);

        setStageOnGlobalFlag(Stage.COMPLETED, "$hegtales_Quest3_missionCompleted");

        addSuccessStages(Stage.COMPLETED);

        //end of global flags

        //setSystemWasUsedForStory(Stage.FIND_CLUES, hegclueSystem);

        beginStageTrigger(hegtales_Quest3.Stage.TALK_TO_CORBY);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_talk_to_corby", true);
        endTrigger();

        beginStageTrigger(Stage.GO_TO_UMBRA);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_GO_TO_UMBRA", true);
        endTrigger();

        beginStageTrigger(Stage.SET_THE_BEACON);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_SET_THE_BEACON", true);
        endTrigger();

        beginStageTrigger(Stage.FIGHT_ISAIAS);
            triggerSetGlobalMemoryValue("$hegtales_Quest3_FIGHT_ISAIAS", true);
            //beginGlobalFlagTrigger("$hegtales_Quest3_triggerIsaias", Stage.FIGHT_ISAIAS);
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.HIGHER, Factions.DIKTAT, FleetTypes.PATROL_MEDIUM, a4.getStarSystem());
            triggerSetFleetOfficers( OfficerNum.DEFAULT, OfficerQuality.LOWER);
            triggerMakeHostileAndAggressive();
            //triggerAutoAdjustFleetStrengthMajor();
        //Makes Isaias fleet normal-ish to yours.
            triggerAutoAdjustFleetStrengthModerate();
        //triggerFleetSetNoFactionInName();
            triggerSetFleetAlwaysPursue();
            triggerOrderFleetInterceptPlayer();
            triggerMakeFleetIgnoredByOtherFleets();
            triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
            //triggerFleetSetName("Sindrian Patrol Fleet");
            triggerFleetSetCommander(isaias);
            triggerFleetSetFlagship("eagle_LG_Assault");
            triggerPickLocationAroundEntity(a4.getPlanetEntity(), 800f);
            triggerSpawnFleetAtPickedLocation("$hegtales_Quest3_isaias_spawn", null);
            triggerSetFleetMissionRef("$hegtales_Quest3_ref"); // so they can be made unimportant
            triggerFleetMakeImportant(null, Stage.FIGHT_ISAIAS);
            triggerFleetAddDefeatTrigger("hegtales_Quest3_isaiaskilled");
        endTrigger();

        beginStageTrigger(Stage.DEAL_WITH_DEFECTUM);
            triggerSetGlobalMemoryValue("$hegtales_Quest3_DEAL_WITH_DEFECTUM", true);
            //beginGlobalFlagTrigger("$hegtales_Quest3_triggerIsaias", Stage.FIGHT_ISAIAS);
            triggerCreateFleet(FleetSize.VERY_LARGE, FleetQuality.HIGHER, Factions.LIONS_GUARD, FleetTypes.PATROL_LARGE, nortia.getPlanetEntity());
            triggerSetFleetOfficers( OfficerNum.DEFAULT, OfficerQuality.LOWER);
            triggerAutoAdjustFleetStrengthMajor();
            triggerMakeFleetIgnoredByOtherFleets();
            triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
            triggerFleetSetCommander(defectum);
            triggerFleetSetFlagship("executor_Standard");
            triggerFleetSetPatrolActionText("exercising");
            triggerSetPatrol();
            triggerOrderFleetPatrol(nortia.getPlanetEntity());
            triggerPickLocationAroundEntity(nortia.getPlanetEntity(), 50f);
            triggerFleetSetPatrolLeashRange(100f);
            triggerSpawnFleetAtPickedLocation("$hegtales_Quest3_defectum_spawn", null);
            triggerSetFleetMissionRef("$hegtales_Quest3_ref"); // so they can be made unimportant
            triggerFleetMakeImportant(null, Stage.DEAL_WITH_DEFECTUM);
            triggerFleetAddDefeatTrigger("hegtales_Quest3_defectum_kill");
        //triggerUnhideCommListing(hannah);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_DEAL_WITH_DEFECTUM", true);
        endTrigger();

        beginStageTrigger(Stage.FIND_RETALIATION);
        //triggerUnhideCommListing(hannah);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_FIND_RETALIATION", true);
        endTrigger();

        beginStageTrigger(Stage.RETURN_TO_EVENTIDE_WITH_RETALIATION);
        //triggerUnhideCommListing(hannah);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_RETURN_EVENTIDE", true);
        endTrigger();

        beginStageTrigger(Stage.COMPLETED);
        triggerSetGlobalMemoryValue("$hegtales_Quest3_missionCompleted", true);
        endTrigger();

        return true;
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        if(currentStage == Stage.TALK_TO_CORBY) {
            info.addPara("Travel to Jangala and speak with Corby.", opad);
            //info.addPara("Travel to Gilead in the Canaan system and find " + hannah.getNameString(), 10.0f);
            addStandardMarketDesc("Go to " + jangala.getOnOrAt(), jangala, info, opad);
            //info.addPara(getGoToSystemTextShort(retaliationentity.getStarSystem()) + " and find the " + retaliationentity.getName() + ".", opad);
        }
        else if(currentStage == Stage.GO_TO_UMBRA) {
            info.addPara("Travel to Umbra and speak with Frusca.", opad);
            //info.addPara(getGoToSystemTextShort(eos) + " and find out where Hannah is.",opad);
            addStandardMarketDesc("Go to " + a4.getOnOrAt(), a4, info, opad);
        }
        else if(currentStage == Stage.SET_THE_BEACON) {
            info.addPara("Set the Satellite in Orbit around Umbra.", opad);
            addStandardMarketDesc("In " + a4.getOnOrAt(), a4, info, opad);
        }
        else if(currentStage == Stage.FIGHT_ISAIAS) {
            info.addPara("Deal with Isaias's fleet and find out where Defectum is located.",opad);
        }
        else if(currentStage == Stage.DEAL_WITH_DEFECTUM) {
            //info.addPara("Destroy Defectum and find the location of the Retaliation.", opad);
            info.addPara("Destroy Defectum in Nortia's orbit and find the location of the Retaliation", opad);
            addStandardMarketDesc("Go to " + nortia.getOnOrAt(), nortia, info, opad);
        }
        else if (currentStage == Stage.FIND_RETALIATION) {
            info.addPara(getGoToSystemTextShort(retaliationentity.getStarSystem()) + " and find the " + retaliationentity.getName() + ".", opad);
            //info.addPara("Find the HSS Retaliation in the Duzahk system.", opad);
        }
        else if(currentStage == Stage.RETURN_TO_EVENTIDE_WITH_RETALIATION) {
            addStandardMarketDesc("Deliver the HSS Retaliation to " + borealis.getNameString()
                    + " " + borealis.getMarket().getOnOrAt() + ".", borealis.getMarket(), info, opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        float opad = 10f;
        if(currentStage == Stage.TALK_TO_CORBY) {
            info.addPara(getGoTalkToPersonText(corby), tc, pad);

            return true;
        }
        else if (currentStage == Stage.GO_TO_UMBRA) {
            info.addPara(getGoTalkToPersonText(frusca), tc, pad);
            return true;
        }
        else if (currentStage == Stage.SET_THE_BEACON) {
            info.addPara("Set the Satellite in Orbit around Umbra.", tc, pad);
            return true;
        }
        else if (currentStage == Stage.FIGHT_ISAIAS) {
            info.addPara("Deal with Isaias's fleet and find out where Defectum is located.",tc, pad);
            return true;
        }
        else if(currentStage == Stage.DEAL_WITH_DEFECTUM) {
            info.addPara("Travel to the Nortia destroy Defectum and find the location of the Retaliation", tc, pad);
            return true;
        }
        else if(currentStage == Stage.FIND_RETALIATION) {
            info.addPara("Travel to the Duzahk system and search for the HSS Retaliation in orbit around the star.", tc, pad);
            return true;
        }
        else if(currentStage == Stage.RETURN_TO_EVENTIDE_WITH_RETALIATION) {
            info.addPara("Deliver the HSS Retaliation to " + borealis.getNameString(), tc, pad);
            return true;
        }
        return false;
    }

    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
                                 Map<String, MemoryAPI> memoryMap) {

        if ("playMusicDefectum".equals(action))
        {
            Global.getSoundPlayer().playCustomMusic(1, 1, "music_diktat_market_hostile", true);
            return true;
        }
        return false;
    }



    protected void updateInteractionDataImpl() {
        set("$hegtales_Quest3_stage", getCurrentStage());
    }

    @Override
    public String getBaseName() {
        return "Operation: Heritage";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }


}
