package data.scripts.quests;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;

import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.util.Misc;


public class hegtales_Quest1 extends HubMissionWithSearch {

    private PersonAPI corby;
    protected int servumPayment;
    protected StarSystemAPI corvus;

    public enum Stage {
        KILL_SERVUM,
        BACK_TO_CORBY,
        COMPLETED
    }

    protected void updateInteractionDataImpl() {
        if (getCurrentStage() != null) {
            set("$hegtales_servum_stage", ((Enum)getCurrentStage()).name());
        }

        set("$hegtales_servum_reward", Misc.getWithDGS(getCreditsReward()));
        set("$hegtales_servum_Payment", Misc.getWithDGS(servumPayment));
    }
    PersonAPI servum = Global.getFactory().createPerson();

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // if this mission was already accepted by the player, abort
        if (!setGlobalReference("$hegtales_Quest1_ref")) {
            return false;
        }

        servum = Global.getSector().getFaction(Factions.PIRATES).createRandomPerson();
        servum.setRankId(Ranks.SPACE_COMMANDER);
        servum.setPostId(Ranks.POST_FLEET_COMMANDER);
        servum.getName().setFirst("Servum");
        servum.getName().setLast("Servo");
        servum.getMemoryWithoutUpdate().set("$servum_warlord", true);
        servum.setId("hegtales_servum_warlord");
        servum.setGender(FullName.Gender.MALE);
        servum.setPortraitSprite("graphics/hegtales/portraits/hegtales_servum.png");
        Global.getSector().getImportantPeople().addPerson(servum);

        setCreditReward(CreditReward.AVERAGE);

        corby = getImportantPerson("hegtales_corby");
        if (corby == null) return false;

        MarketAPI eventide = Global.getSector().getEconomy().getMarket("eventide");
        if(eventide == null) return false;

        setStartingStage(Stage.KILL_SERVUM);
        addSuccessStages(Stage.COMPLETED);

        servumPayment = genRoundNumber(20000, 45000);

        makeImportant(corby, "$hegtales_Quest1_contact", Stage.BACK_TO_CORBY);
        makeImportant(corby.getMarket(), null, Stage.BACK_TO_CORBY);
        setStoryMission();

        beginStageTrigger(Stage.KILL_SERVUM);
        StarSystemAPI corvus = corby.getMarket().getStarSystem();
        SectorEntityToken asharu = corvus.getEntityById("asharu");
        triggerCreateFleet(FleetSize.MEDIUM, FleetQuality.LOWER, Factions.PIRATES, FleetTypes.PERSON_BOUNTY_FLEET, corvus);
        triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        triggerMakeFleetIgnoredByOtherFleets();
        triggerMakeFleetGoAwayAfterDefeat();
        triggerAutoAdjustFleetStrengthMajor();
        triggerSetStandardAggroPirateFlags();
        triggerFleetSetCommander(servum);
        triggerMakeNonHostileToFaction(Factions.HEGEMONY);
        triggerFleetSetName("Servum Servos");
        triggerFleetSetFlagship("venture_p_Pirate");
        triggerPickLocationAroundEntity(asharu, 50.0f);
        triggerMakeAllFleetFlagsPermanent();
        triggerSpawnFleetAtPickedLocation("$hegtales_Quest1_pirate", null);
        triggerMakeFleetIgnoreOtherFleets(); // don't go chasing pirates, please.
        //triggerSetPatrol();
        triggerFleetSetPatrolLeashRange(50.0f);
        triggerFleetSetPatrolActionText("looking for easy prey.");
        triggerOrderFleetPatrol(true, asharu);
        triggerFleetMakeImportant(null, Stage.KILL_SERVUM);
        triggerFleetAddDefeatTrigger("hegtales_Quest1PirateDefeated");
        final String fleetTag = "hegtales_Quest1fleet";
        triggerFleetAddTags(fleetTag);
        endTrigger();

        beginStageTrigger(Stage.COMPLETED);
        triggerSetGlobalMemoryValue("$hegtales_Quest1_missionCompleted", true);
        endTrigger();

        //This should complete the mission by talking instead of fighting
        setStageOnGlobalFlag(Stage.BACK_TO_CORBY, "$hegtales_Quest1_suspicious_cargo");
        setStageOnGlobalFlag(Stage.COMPLETED, "$hegtales_Quest1GotReward");


        return true;
    }


    @Override
    public void addDescriptionForCurrentStage(TooltipMakerAPI info, float width, float height) {
        if(currentStage == Stage.KILL_SERVUM) {
            info.addPara("Defeat the pirate fleet that usually hangs around Asharu.", 10.0f);
        }
        if(currentStage == Stage.BACK_TO_CORBY) {
            info.addPara("Get back to Eventide talk to "+ corby.getNameString()+ " to receive your reward", 10.0f);
            //this shouldnt go here, right?
            addStandardMarketDesc("Corby is located "+ corby.getMarket().getOnOrAt(), corby.getMarket(), info, 10.0f);
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        if(currentStage == Stage.KILL_SERVUM) {
            info.addPara("Find and deal the pirate fleet around Asharu.", tc, pad);
        }
        else if(currentStage == Stage.BACK_TO_CORBY) {
            info.addPara(getGoTalkToPersonText(corby), tc, pad);
        }
        return false;
    }

    @Override
    public String getBaseName() {
        return "Deal with the Servos";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }
}
