package data.scripts.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;

public class BoneDancer_Quest1 extends HubMissionWithSearch {

    public enum Stage {
        GO_TO_HANAN,
        COMPLETED,
        FAILED,
    }

    protected PersonAPI granger2;
    protected PersonAPI cranium1;
    protected MarketAPI salamanca;

    public static float MISSION_DAYS = 30f;

    protected int payment;
    protected int paymentHigh;

    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // if already accepted by the player, abort
        if (!setGlobalReference("$BDhegtales_Quest1_ref", "$BDhegtales_Quest1_inProgress")) {
            return false;
        }

        cranium1 = getImportantPerson("hegtales_cranium");
        granger2 = getImportantPerson("hegtales_granger");
        if (cranium1 == null || granger2 == null ) return false;

        SectorEntityToken hanan_pacha = Global.getSector().getEntityById("hanan_pacha");
        if (hanan_pacha == null) return false;

        salamanca = Global.getSector().getEconomy().getMarket("salamanca");
        if (salamanca == null) return false;
        
        setStartingStage(Stage.GO_TO_HANAN);
        addSuccessStages(Stage.COMPLETED);
        addFailureStages(Stage.FAILED);

        setTimeLimit(Stage.FAILED, MISSION_DAYS, null);

        setStoryMission();


        payment = 15000;
        paymentHigh = 20000;

        makeImportant(hanan_pacha, "$BDhegtales_Quest1_tookTheJob", Stage.GO_TO_HANAN);
        setStageOnGlobalFlag(Stage.COMPLETED, "$BDhegtales_Quest1_completed");
        setStageOnGlobalFlag(BDDeliverVIP.Stage.FAILED, "$BDhegtales_Quest1_failed");

        setRepFactionChangesNone();
        setRepPersonChangesNone();

        // spawn Persean Patrol fleet to intercept the player
        beginStageTrigger(Stage.GO_TO_HANAN);
        StarSystemAPI Yma = Global.getSector().getStarSystem("Yma");
        SectorEntityToken hanan_pacha_planet = Yma.getEntityById("hanan_pacha");
        triggerCreateFleet(FleetSize.SMALL, FleetQuality.DEFAULT, Factions.PERSEAN, FleetTypes.PATROL_SMALL, Yma);

        triggerMakeFleetIgnoreOtherFleets(); // don't go chasing pirates, please.
        triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        triggerMakeFleetIgnoredByOtherFleets();
        triggerMakeFleetGoAwayAfterDefeat();
        triggerMakeLowRepImpact();
        triggerAutoAdjustFleetStrengthMajor();
        triggerMakeHostileAndAggressive();

        triggerPickLocationAroundEntity(hanan_pacha_planet, 50.0f);
        triggerSpawnFleetAtPickedLocation("$BDhegtales_Quest1_perseanPatrol", null);
        triggerSetFleetMissionRef("$BDhegtales_Quest1_ref");
        triggerOrderFleetInterceptPlayer();
        triggerFleetMakeImportant(null, Stage.GO_TO_HANAN);
        triggerFleetSetPatrolActionText("looking for a fleet.");
        triggerOrderFleetPatrol(true, hanan_pacha_planet);
        endTrigger();

        beginStageTrigger(Stage.COMPLETED);
        triggerSetGlobalMemoryValue("$BDhegtales_Quest1_missionCompleted", true);
        endTrigger();

        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$BDhegtales_Quest1_stage", getCurrentStage());
        set("$BDhegtales_Quest1_payment", Misc.getWithDGS(payment));
        set("$BDhegtales_Quest1_paymentHigh", Misc.getWithDGS(paymentHigh));
    }
    

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();

        info.addImage(cranium1.getPortraitSprite(), width, 128, opad);

        if (currentStage == Stage.GO_TO_HANAN) {
            info.addPara("Deliver the research team to Hanan Pacha in the Yma system.", opad);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.GO_TO_HANAN) {
            info.addPara("Go to Hanan Pacha in the Yma system", tc, pad);
            return true;
        }
        return false;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
                                 Map<String, MemoryAPI> memoryMap) {

        if (action.equals("cranium_contact")) {
            BaseMissionHub.set(cranium1, new BaseMissionHub(cranium1));
            cranium1.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
            ((RuleBasedDialog)dialog.getPlugin()).updateMemory();

            return true;
        }
        return false;
    }



    @Override
    public String getBaseName() {
        return "Passage To Hanan Pacha";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }
}