package data.scripts.quests;
import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.missions.academy.GAFindingCoureuse;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.impl.campaign.missions.luddic.LuddicKnightErrant;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class hegtales_Quest2 extends HubMissionWithSearch {

    public static enum Stage {
        GO_TO_GILEAD,
        GO_TO_HESPERUS,
        FIND_CLUE_IN_HESPERUS,
        FIND_CLUES,
        GO_TO_BEHOLDER,
        RETURN_TO_EVENTIDE,
        COMPLETED,
    }

    protected PersonAPI australis;
    protected PersonAPI hannah;
    protected PersonAPI saltus;
    protected PersonAPI eridanus;
    protected PersonAPI narc;
    protected PersonAPI pather;
    protected PersonAPI leo;
    protected SectorEntityToken beholder_station;
    protected StarSystemAPI hegclueSystem;
    protected StarSystemAPI kumarikandam;
    protected int cluebribeCost;
    protected int cluesellOutPrice;
    protected int patherbribeCost;
    protected MarketAPI eos3;
    protected MarketAPI eos2;
    protected MarketAPI gilead;
    protected MarketAPI chalcedon;
    public static float RAID_DIFFICULTY = 75f;

    protected boolean create(MarketAPI createdAt, boolean barEvent) {

       setName("Find Hannah Stahl-Ossum");

        // if this mission was already accepted by the player, abort
        if (!setGlobalReference("$hegtales_Quest2_ref", "$hegtales_Quest2_inProgress")) {
            return false;
        }

        StarSystemAPI kumarikandam =  Global.getSector().getStarSystem("kumari kandam");
        for (SectorEntityToken curr : kumarikandam.getEntitiesWithTag(Tags.LUDDIC_SHRINE)) {
            beholder_station = curr;
            break;
        }
        if (beholder_station == null) return false;

        eos3 = Global.getSector().getEconomy().getMarket("hesperus");
        if (eos3 == null) return false;

        eos2 = Global.getSector().getEconomy().getMarket("tartessus");
        if (eos2 == null) return false;

        gilead = Global.getSector().getEconomy().getMarket("gilead");
        if (gilead == null) return false;

        chalcedon = Global.getSector().getEconomy().getMarket("chalcedon");
        if (chalcedon == null) return false;

        setCreditReward(CreditReward.VERY_HIGH);

        australis = getImportantPerson("hegtales_australis");
        hannah = getImportantPerson("hegtales_hannah");
        saltus = getImportantPerson("hegtales_saltus");

        // Kind of a lot of effort for a minor character, but ... -eluxor
        eridanus = Global.getSector().getFaction(Factions.LUDDIC_CHURCH).createRandomPerson(genRandom);
        eridanus.setRankId(Ranks.FATHER);
        eridanus.setPostId(Ranks.POST_ARCHCURATE);
        eridanus.setImportance(PersonImportance.MEDIUM);
        eridanus.setGender(FullName.Gender.MALE);
        eridanus.setId("hegtales_eridanus_archcurate");
        eridanus.getName().setFirst("Eridanus");
        eridanus.getName().setLast("Tio");
        eridanus.setPortraitSprite("graphics/hegtales/portraits/hegtales_eridanus.png");
        gilead.getCommDirectory().addPerson(eridanus);
        gilead.addPerson(eridanus);

        narc = Global.getSector().getFaction(Factions.TRITACHYON).createRandomPerson(genRandom);
        narc.setGender(FullName.Gender.ANY);
        narc.setId("hegtales_narc");
        narc.getName().setFirst("Narciso");
        narc.getName().setLast("Boeotia");
        narc.setPortraitSprite("graphics/hegtales/portraits/hegtales_narcissus.png");

        pather = Global.getSector().getFaction(Factions.LUDDIC_PATH).createRandomPerson(genRandom);
        pather.setGender(FullName.Gender.FEMALE);
        pather.setId("hegtales_pather");
        pather.getName().setFirst("Sidney");
        pather.setPortraitSprite("graphics/hegtales/portraits/hegtales_sidney.png");
        eos2.addPerson(pather);
        Global.getSector().getImportantPeople().addPerson(pather);

        leo = Global.getSector().getFaction(Factions.LUDDIC_PATH).createRandomPerson(genRandom);
        leo.setGender(FullName.Gender.MALE);
        leo.setId("hegtales_leo");
        leo.getName().setFirst("Leo");
        leo.setPortraitSprite("graphics/hegtales/portraits/hegtales_leo.png");
        eos3.addPerson(leo);
        Global.getSector().getImportantPeople().addPerson(leo);

        if (australis == null || hannah == null || saltus == null || eridanus == null || narc == null || pather == null || leo == null ) return false;



        // Find a system to hide some clues in.
        resetSearch();
        requireSystemTags(ReqMode.ANY, Tags.THEME_MISC, Tags.THEME_MISC_SKIP, Tags.THEME_RUINS);
        requireSystemTags(ReqMode.NOT_ANY, Tags.THEME_UNSAFE, Tags.THEME_CORE, Tags.SYSTEM_ALREADY_USED_FOR_STORY);
        requireSystemNotAlreadyUsedForStory();
        requireSystemNotHasPulsar(); // pls no
        preferSystemOnFringeOfSector();
        preferSystemUnexplored();
        preferSystemInDirectionOfOtherMissions();
        hegclueSystem = pickSystem();
        if (hegclueSystem == null) return false;

        // literally stolen from the courese mission
        // Set up 3? probes in the system to discover.
        // I sure hope they don't end up in the same place. -dgb
        SectorEntityToken hegclue1 = spawnEntity(Entities.SUPPLY_CACHE_SMALL, new LocData(EntityLocationType.UNCOMMON, null, hegclueSystem));
        SectorEntityToken hegclue2 = spawnEntity(Entities.SUPPLY_CACHE_SMALL, new LocData(EntityLocationType.ORBITING_PLANET, null, hegclueSystem));
        SectorEntityToken hegclue3 = spawnEntity(Entities.SUPPLY_CACHE_SMALL, new LocData(EntityLocationType.UNCOMMON, null, hegclueSystem));
        if (hegclue1 == null || hegclue2 == null || hegclue3 == null ) return false;

        hegclue1.setCustomDescriptionId("hegtales_hannahclue");
        hegclue2.setCustomDescriptionId("hegtales_hannahclue");
        hegclue3.setCustomDescriptionId("hegtales_hannahclue");

        // set a random probe as looted.
        WeightedRandomPicker<SectorEntityToken> picker = new WeightedRandomPicker<SectorEntityToken>(genRandom);
        picker.add(hegclue1, 1f);
        picker.add(hegclue2, 1f);
        picker.add(hegclue3, 1f);
        //picker.add(hegclue3, 1f);
        picker.pick().addTag("empty");

        // Add the church defender
        addchurccluedefenderFleet();
        // And the ambush fleet.
        addCluePatherAmbushFleet();

        cluebribeCost = genRoundNumber(25000, 55000); // bribe scavenger
        cluesellOutPrice = genRoundNumber(50000, 70000); // payment
        patherbribeCost = genRoundNumber(25000, 45000); // bribe pather

        gilead = Global.getSector().getEconomy().getMarket("gilead");
        if (gilead == null) return false;

        eos3 = Global.getSector().getEconomy().getMarket("hesperus");
        if (eos3 == null) return false;


        //MISSION START!
        setStoryMission();

        setStartingStage(hegtales_Quest2.Stage.GO_TO_GILEAD);
        makeImportant(gilead, "$hegtales_Quest2_gilead", Stage.GO_TO_GILEAD); //IMPORTANT

        setStageOnGlobalFlag(Stage.GO_TO_HESPERUS, "$hegtales_Quest2_GO_TO_HESPERUS");
        makeImportant(eos3, "$hegtales_Quest2_GO_TO_HESPERUS", Stage.GO_TO_HESPERUS); //IMPORTANT

        setStageOnGlobalFlag(Stage.FIND_CLUE_IN_HESPERUS, "$hegtales_Quest2_FIND_CLUE_IN_HESPERUS");
        makeImportant(eos3, "$hegtales_Quest2_FIND_CLUE_IN_HESPERUS", Stage.FIND_CLUE_IN_HESPERUS); //IMPORTANT

        setStageOnGlobalFlag(Stage.FIND_CLUES, "$hegtales_Quest2_FIND_CLUES");
        makeImportant(hegclue1, "$hegtales_Quest2_FIND_CLUES", Stage.FIND_CLUES);
        makeImportant(hegclue2, "$hegtales_Quest2_FIND_CLUES", Stage.FIND_CLUES);
        makeImportant(hegclue3, "$hegtales_Quest2_FIND_CLUES", Stage.FIND_CLUES);

        setStageOnGlobalFlag(Stage.GO_TO_BEHOLDER, "$hegtales_Quest2_goto_Beholder");
        makeImportant(beholder_station, "$hegtales_Quest2_goto_Beholder", Stage.GO_TO_BEHOLDER);

        setStageOnGlobalFlag(Stage.RETURN_TO_EVENTIDE, "$hegtales_Quest2_return_eventide");
        makeImportant(australis, "$hegtales_Quest2_australis_eventide_return", Stage.RETURN_TO_EVENTIDE);

        setStageOnGlobalFlag(Stage.COMPLETED, "$hegtales_Quest2_missionCompleted");

        addSuccessStages(Stage.COMPLETED);

        //end of global flags

        setSystemWasUsedForStory(Stage.FIND_CLUES, hegclueSystem);

        beginStageTrigger(hegtales_Quest2.Stage.GO_TO_GILEAD);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_goto_GILEAD", true);
        endTrigger();

        beginStageTrigger(Stage.GO_TO_HESPERUS);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_GO_TO_HESPERUS", true);
        endTrigger();

        beginStageTrigger(Stage.FIND_CLUE_IN_HESPERUS);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_FIND_CLUE_IN_HESPERUS", true);
        //triggerUnhideCommListing(saltus); done after talking with Sidney
        endTrigger();

        beginStageTrigger(Stage.FIND_CLUES);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_FIND_CLUES", true);
        triggerHideCommListing(saltus);
        endTrigger();

        beginStageTrigger(Stage.GO_TO_BEHOLDER);
        //triggerUnhideCommListing(saltus);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_goto_Beholder", true);
        StarSystemAPI KumariKandam = Global.getSector().getStarSystem("kumari kandam");
        SectorEntityToken beholder_station = KumariKandam.getEntityById("beholder_station");
        triggerCreateFleet(FleetSize.MEDIUM, FleetQuality.HIGHER, Factions.TRITACHYON, FleetTypes.MERC_PRIVATEER, KumariKandam);
        triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        triggerMakeFleetIgnoredByOtherFleets();
        triggerMakeFleetGoAwayAfterDefeat();
        triggerMakeLowRepImpact();
        triggerAutoAdjustFleetStrengthMajor();
        triggerMakeHostileAndAggressive();
        triggerFleetSetCommander(narc);
        triggerFleetSetName("Bounty Hunter");
        triggerPickLocationAroundEntity(beholder_station, 50.0f);
        triggerSpawnFleetAtPickedLocation("$hegtales_Quest2_tritach", null);
        triggerMakeFleetIgnoreOtherFleets(); // don't go chasing pirates, please.
        //triggerSetPatrol();
        triggerFleetSetPatrolLeashRange(50.0f);
        triggerFleetSetPatrolActionText("looking for easy prey.");
        triggerOrderFleetPatrol(true, beholder_station);
        triggerFleetMakeImportant(null, Stage.GO_TO_BEHOLDER);
        endTrigger();

        beginStageTrigger(Stage.RETURN_TO_EVENTIDE);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_return_eventide", true);
        endTrigger();

        beginStageTrigger(Stage.COMPLETED);
        triggerUnhideCommListing(hannah);
        //triggerMovePersonToMarket(saltus, chalcedon, false);
        triggerSetGlobalMemoryValue("$hegtales_Quest2_missionCompleted", true);
        endTrigger();

        return true;
    }


    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        if(currentStage == Stage.GO_TO_GILEAD) {
            info.addPara("Travel to Gilead in the Canaan system and find " + hannah.getNameString(), 10.0f);
            addStandardMarketDesc("Go to " + gilead.getOnOrAt(), gilead, info, opad);
        }
        else if(currentStage == Stage.GO_TO_HESPERUS) {
            info.addPara("Travel to the Hesperus and find clues about "+ hannah.getNameString(), 10.0f);
            addStandardMarketDesc("Go to " + eos3.getOnOrAt(), eos3, info, opad);
            //info.addPara(getGoToSystemTextShort(eos) + " and find out where Hannah is.",opad);
        }
        else if(currentStage == Stage.FIND_CLUE_IN_HESPERUS) {
            info.addPara("Search the Eos Exodus system for a clue of Hannah's location. Talk to people who might know anything about her.", opad);
        }
        else if(currentStage == Stage.FIND_CLUES) {
            // info.addPara("Travel to the Hesperus and find "+ hannah.getNameString(), 10.0f);
            info.addPara(getGoToSystemTextShort(hegclueSystem) + " and search for the clues left behind by Hannah.",opad);
        }
        else if(currentStage == Stage.GO_TO_BEHOLDER) {
            info.addPara("Find Hannah! "+ "She's supposed to be located in Beholder Station, according to the information.", opad);
        }
        else if(currentStage == Stage.RETURN_TO_EVENTIDE) {
            info.addPara("Travel to Eventide in the Samarra system and bring back Hannah to " + australis.getNameString(), 10.0f);
        }
    }

    @Override
    //THE  big triggers go here.
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        float opad = 10f;
        if(currentStage == Stage.GO_TO_GILEAD) {
            info.addPara(getGoToMarketText(eridanus.getMarket()) + " and find Hannah", tc, pad);
            return true;
        }
        else if (currentStage == Stage.GO_TO_HESPERUS) {
            info.addPara(getGoToMarketText(saltus.getMarket()) + " and find clues about Hannah.", tc, pad);
            return true;
        }
        else if (currentStage == Stage.FIND_CLUE_IN_HESPERUS) {
            info.addPara("Search the Eos Exodus system for a clue of Hannah's location. Talk to people who might know anything about her.", opad);
            return true;
        }
        else if (currentStage == Stage.FIND_CLUES) {
            info.addPara(getGoToSystemTextShort(hegclueSystem) + " and search for the clues left behind by Hannah.", tc, pad);
            return true;
        }
        else if(currentStage == Stage.GO_TO_BEHOLDER) {
            info.addPara(getGoToSystemTextShort(beholder_station.getStarSystem()) + "and find Hannah, she's supposed to be located in Beholder Station, according to the logs.", opad);
            return true;
        }
        else if(currentStage == Stage.RETURN_TO_EVENTIDE) {
            info.addPara("Get back to Eventide with Hannah talk to "+ australis.getNameString()+ " to receive your reward.", 10.0f);
            return true;
        }
        return false;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
                                 Map<String, MemoryAPI> memoryMap) {

        if ("drophegclue".equals(action)) {
            //SectorEntityToken probe = system.addCustomEntity(null,
            //"Probe name or null if it's in custom_entities",
            //"<entity type id from custom entities>, Factions.NEUTRAL);
            //probe.setLocation(churccluedefender.getLocation().x, churccluedefender.getLocation().y); // with some extra offset if needed etc

            SectorEntityToken churccluedefender = getEntityFromGlobal("$hegtales_Quest2_churccluedefender");
            //LocationAPI dropLocation = churccluedefender.getContainingLocation();
            SectorEntityToken hegclue2 = hegclueSystem.addCustomEntity(null, "Ejected Luddic Cache", Entities.CARGO_POD_SPECIAL, Factions.NEUTRAL);
            //does this need Misc.genUID() as first parameter?
            hegclue2.setLocation(churccluedefender.getLocation().x, churccluedefender.getLocation().y); // redundant?
            // spawnEntity(Entities.GENERIC_PROBE, churccluedefender.getLocation());
            hegclue2.addTag("hegtales_Quest2_lootedClue");
            Misc.makeImportant(hegclue2, getMissionId());
            //Misc.makeUnimportant(churccluedefender, getMissionId());
            // it was getting re-flagged "important" when updateData etc was called since
            // it was still noted down that it should be important during the current stage
            // this method call cleans that out
            makeUnimportant(churccluedefender);

            return true;
        }
        if ("foundEmptyClue".equals(action)) {
            // found empty probe, so now player suspects the churccluedefender of taking it -dgb
            SectorEntityToken churccluedefender = getEntityFromGlobal("$hegtales_Quest2_churccluedefender");
            Misc.makeImportant(churccluedefender, getMissionId());

            return true;
        }
        return false;
    }

    protected void updateInteractionDataImpl() {
        set("$hegtales_Quest2_stage", getCurrentStage());
        set("$hegtales_Quest2_starName", hegclueSystem.getNameWithNoType());
        set("$hegtales_Quest2_bribeCost", Misc.getWithDGS(cluebribeCost));
        set("$hegtales_Quest2_sellOutPrice", Misc.getWithDGS(cluesellOutPrice));
        set("$hegtales_Quest2_patherbribeCost", Misc.getWithDGS(patherbribeCost));
        set("$hegtales_Quest2_raidDifficulty", RAID_DIFFICULTY);
    }

    @Override
    public String getBaseName() {
        return "Find Hannah Stahl-Ossum";
    }

    @Override
    public String getPostfixForState() {
        if (startingStage != null) {
            return "";
        }
        return super.getPostfixForState();
    }

    protected void addchurccluedefenderFleet()
    {
        // Near the star? Okay, hope this works.
        //SectorEntityToken fleetLocation = probeSystem.getStar();

        // no reason for the scavenger fleet to exist unless the player is nearby
        beginWithinHyperspaceRangeTrigger(hegclueSystem, 3f, false, Stage.FIND_CLUES);
        triggerCreateFleet(FleetSize.SMALL, FleetQuality.LOWER, Factions.LUDDIC_CHURCH, FleetTypes.SCAVENGER_MEDIUM, hegclueSystem);
        triggerSetFleetOfficers(OfficerNum.FEWER, OfficerQuality.DEFAULT);
        triggerSetFleetFaction(Factions.LUDDIC_CHURCH);
        triggerMakeLowRepImpact();
        triggerMakeFleetIgnoredByOtherFleets();
        //triggerMakeFleetIgnoreOtherFleetsExceptPlayer();
        triggerPickLocationAtInSystemJumpPoint(hegclueSystem); // so it's not always the one closest to the star...
        triggerSetEntityToPickedJumpPoint();
        triggerFleetSetName("Luddic Scavengers");
        triggerPickLocationAroundEntity(1500);
        triggerSpawnFleetAtPickedLocation("$hegtales_Quest2_Probe_scavengerPermanentFlag", null);
        triggerFleetSetTravelActionText("exploring system");
        triggerFleetSetPatrolActionText("wandering around");
        triggerOrderFleetPatrolEntity(false);
        triggerFleetAddDefeatTrigger("hegtales_Quest2_churccluedefenderDefeated");
        triggerSaveGlobalFleetRef("$hegtales_Quest2_churccluedefender");
        // only becomes "important" when player finds empty probe
        //triggerFleetMakeImportant(null, Stage.SEARCH_ISIRAH);
        endTrigger();
    }

    protected void addCluePatherAmbushFleet()
    {
        //SectorEntityToken location = probeSystem.getStar();
        beginGlobalFlagTrigger("$hegtales_Quest2_triggerCluePatherAmbush", Stage.FIND_CLUES);
        if (Global.getSettings().getModManager().isModEnabled("knights_of_ludd")) {
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.DEFAULT, Factions.LUDDIC_PATH, FleetTypes.PATROL_LARGE, hegclueSystem);
            triggerFleetSetFlagship("kol_lunaria_Knightmaster");
        }
        else if (Global.getSettings().getModManager().isModEnabled("PAGSM"))  {
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.DEFAULT, Factions.LUDDIC_PATH, FleetTypes.PATROL_LARGE, hegclueSystem);
            triggerFleetSetFlagship("sfcpatherepimetheus_Barrage");
        }
        else {
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.DEFAULT, Factions.LUDDIC_PATH, FleetTypes.PATROL_LARGE, hegclueSystem);
            triggerFleetSetFlagship("prometheus2_Standard");
        }
        triggerSetFleetOfficers( OfficerNum.DEFAULT, OfficerQuality.DEFAULT);
        //triggerMakeNonHostile(); // should it be hostile?
        triggerMakeHostileAndAggressive();
        triggerSetFleetFaction(Factions.LUDDIC_PATH);
        triggerMakeLowRepImpact();
        triggerFleetPatherNoDefaultTithe();
        triggerPickLocationAtClosestToPlayerJumpPoint(hegclueSystem);
        triggerSetEntityToPickedJumpPoint();
        triggerAutoAdjustFleetStrengthMajor();
        triggerFleetSetNoFactionInName();
        triggerSetFleetAlwaysPursue();
        triggerFleetSetName("Pather Knights");
        triggerFleetSetCommander(saltus);
        triggerFleetSetPatrolActionText("waiting");
        triggerPickLocationTowardsEntity(null, 10f, getUnits(1.0f)); // towards the jump-point we just picked
        triggerSpawnFleetAtPickedLocation("$hegtales_Quest2_CluePatherAmbush", null);
        triggerSetFleetMissionRef("$hegtales_Quest2_ref"); // so they can be made unimportant
        triggerFleetMakeImportant(null, Stage.FIND_CLUES);
        triggerOrderFleetInterceptPlayer();
        triggerFleetAddDefeatTrigger("hegtales_Quest2_Saltuskilled");
        endTrigger();
    }


}
