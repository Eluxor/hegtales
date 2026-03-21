package data.scripts.quests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.missions.academy.GABaseMission;
import com.fs.starfarer.api.impl.campaign.missions.academy.GADeliverVIP;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI.PersonDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepRewards;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.missions.DelayedFleetEncounter;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class BDDeliverVIP extends GABaseMission {


    public static float MISSION_DAYS = 60f;
    public static float PROB_KANTA = 0.1f;
    public static float PROB_OSSUM = 0.1f;
    public static float PROB_MERC_KIDNAPPER = 0.5f;
    public static float PROB_AOSSUM_KIDNAPPER = 1f;
    public static float PROB_PIRATE_KIDNAPPER = 0.5f;

    public static float SPECIFIC_FACTION_SUBJECT_EVENT_LIKELIHOOD = 2f;

    PersonAPI occidens = Global.getFactory().createPerson();

    public static class FactionData {
        //public Pair<String, String>[] subjects;
        public String[] subjects;
        public String[] events;
    }

    public static FactionData ALL_FACTIONS;
    public static Map<String, FactionData> FACTION_DATA = new HashMap<String, FactionData>();
    public static List<String> ALLOWED_FACTIONS = new ArrayList<String>();
    public static List<String> MERC_FACTIONS = new ArrayList<String>();
    public static List<String> ANTIOSSUM_FACTIONS = new ArrayList<String>();
    static {
        ALLOWED_FACTIONS.add(Factions.INDEPENDENT);
        ALLOWED_FACTIONS.add(Factions.PIRATES);
        ALLOWED_FACTIONS.add(Factions.LUDDIC_PATH);

        ANTIOSSUM_FACTIONS.add(Factions.TRITACHYON);
        ANTIOSSUM_FACTIONS.add(Factions.DIKTAT);

        MERC_FACTIONS.add(Factions.TRITACHYON);
        MERC_FACTIONS.add(Factions.HEGEMONY);
        MERC_FACTIONS.add(Factions.DIKTAT);
        MERC_FACTIONS.add(Factions.PERSEAN);

        ALL_FACTIONS = new FactionData();
        ALL_FACTIONS.subjects = new String[] {
                "prisoner",
                "chem maker",
                "slaver",
                "dealer",
        };
        ALL_FACTIONS.events = new String [] {
                "a deal", "a meeting", "a reunion", "an operation", "a thing"
        };

        FactionData hegemony = new FactionData();
        FACTION_DATA.put(Factions.HEGEMONY, hegemony);
        hegemony.subjects = new String[] {
                "scion",
                "member",
        };
        hegemony.events = new String [] {
                "a Family Meeting" , "a ball"
        };
    }

    public String pickSubject(String factionId) {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(genRandom);
        for (String p : ALL_FACTIONS.subjects) {
            picker.add(p, 1f);
        }
        FactionData data = FACTION_DATA.get(factionId);
        if (data != null) {
            float w = Math.max(1f, data.subjects.length) / Math.max(1f, picker.getTotal());
            w *= SPECIFIC_FACTION_SUBJECT_EVENT_LIKELIHOOD; // faction-specific is more likely than generic, overall
            for (String p : data.subjects) {
                picker.add(p, w);
            }
        }
        return picker.pick();
    }

    public String pickEvent(String factionId) {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(genRandom);
        for (String event : ALL_FACTIONS.events) {
            picker.add(event, 1f);
        }
        FactionData data = FACTION_DATA.get(factionId);
        if (data != null) {
            float w = Math.max(1f, data.events.length) / Math.max(1f, picker.getTotal());
            w *= SPECIFIC_FACTION_SUBJECT_EVENT_LIKELIHOOD; // faction-specific is more likely than generic, overall
            for (String event : data.events) {
                picker.add(event, w);
            }
        }
        return picker.pick();
    }


    public static enum Stage {
        DELIVER_VIP,
        COMPLETED,
        FAILED,
        FAILED_DECIV,
    }

    public static enum Variation {
        BASIC,
        KANTA,
        OSSUM,
    }

    protected StarSystemAPI system;
    protected MarketAPI destination;
    protected Variation variation;
    protected FactionAPI faction;
    protected String theMercFaction;
    protected String mercFactionId;
    protected String theAOSSUMFaction;
    protected String AOSSUMFactionId;

    protected String subjectRelation;
    protected String event;
    protected PersonAPI target;
    protected String kantaRelationFirstName;
    protected String ossumRelationFirstName;

    protected int piratePayment;
    protected int mercPayment;

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // if this mission type was already accepted by the player, abort
        if (!setGlobalReference("$bdvip_ref")) {
            return false;
        }

        occidens = Global.getSector().getFaction(Factions.HEGEMONY).createRandomPerson();
        occidens.setRankId(Ranks.SPACE_COMMANDER);
        occidens.setPostId(Ranks.POST_FLEET_COMMANDER);
        occidens.getName().setLast("Ossum");
        occidens.getMemoryWithoutUpdate().set("$occidens", true);
        occidens.setId("hegtales_occidens");
        if (occidens.getGender() == FullName.Gender.MALE) {
            occidens.setPortraitSprite("graphics/portraits/portrait_hegemony01.png");
        } else {
            occidens.setPortraitSprite("graphics/portraits/portrait_hegemony04.png");
        }
        Global.getSector().getImportantPeople().addPerson(occidens);

        requireMarketFaction(ALLOWED_FACTIONS.toArray(new String [0]));
        requireMarketLocationNot("yma");
        requireMarketNotHidden();
        requireMarketNotInHyperspace();
        preferMarketInDirectionOfOtherMissions();

//		PROB_KANTA = 1f;
//		PROB_MERC_KIDNAPPER = 1f;

        destination = pickMarket();
        variation = Variation.BASIC;
        if (rollProbability(PROB_KANTA)) {
            MarketAPI kantasDen = Global.getSector().getEconomy().getMarket("kantas_den");
            if (kantasDen != null) {
                destination = kantasDen;
                variation = Variation.KANTA;
                kantaRelationFirstName = Global.getSector().getFaction(Factions.PIRATES).createRandomPerson(genRandom).getName().getFirst();
            }
        }
        if (rollProbability(PROB_OSSUM)) {
            MarketAPI eventide = Global.getSector().getEconomy().getMarket("eventide");
            if (eventide != null) {
                destination = eventide;
                variation = Variation.OSSUM;
                ossumRelationFirstName = Global.getSector().getFaction(Factions.HEGEMONY).createRandomPerson(genRandom).getName().getFirst();
            }
        }
        if (destination == null) return false;

        faction = destination.getFaction();
        subjectRelation = pickSubject(faction.getId());
        if (subjectRelation == null) return false;

        event = pickEvent(faction.getId());
        if (event == null) return false;

        if (variation == Variation.BASIC) {
            target = findOrCreatePerson(faction.getId(), destination, true, Ranks.CITIZEN,
                    Ranks.POST_ADMINISTRATOR, Ranks.POST_BASE_COMMANDER, Ranks.POST_STATION_COMMANDER,
                    Ranks.POST_OUTPOST_COMMANDER, Ranks.POST_PORTMASTER, Ranks.POST_FACTION_LEADER
                    );
//			target.addTag(Tags.CONTACT_TRADE);
//			setPersonIsPotentialContactOnSuccess(target);
        } else if (variation == Variation.KANTA) {
            // set the VIP to Kanta, for rep purposes, since the sub-boss that's the actual relation doesn't exist
            PersonDataAPI pd = Global.getSector().getImportantPeople().getData("kanta");
            if (pd != null) target = pd.getPerson();
        }
        else if (variation == Variation.OSSUM) {
            // set the VIP to Borealis, for rep purposes.
            PersonDataAPI pd = Global.getSector().getImportantPeople().getData("hegtales_borealis");
            if (pd != null) target = pd.getPerson();
        }
        if (target == null) return false;


        system = destination.getStarSystem();

        setStartingStage(Stage.DELIVER_VIP);
        addSuccessStages(Stage.COMPLETED);
        addFailureStages(Stage.FAILED);
        addNoPenaltyFailureStages(Stage.FAILED_DECIV);

        // used for generic pirate reaction
        if (variation == Variation.KANTA) {
            setGlobalFlag("$bdvip_workingForKanta", true, Stage.DELIVER_VIP);
        }
        if (variation == Variation.OSSUM) {
            setGlobalFlag("$bdvip_workingForOSSUM", true, Stage.DELIVER_VIP);
        }

        makeImportant(destination, "$bdvip_target", Stage.DELIVER_VIP);

        setStageOnGlobalFlag(Stage.COMPLETED, "$bdvip_delivered");
        setStageOnGlobalFlag(Stage.FAILED, "$bdvip_failed");
        connectWithMarketDecivilized(Stage.DELIVER_VIP, Stage.FAILED_DECIV, destination);

        setTimeLimit(Stage.FAILED, MISSION_DAYS, null);
        if (variation == Variation.BASIC) {
            //setCreditReward(30000, 40000);
            setCreditReward(CreditReward.AVERAGE);
            setRepPenaltyPerson(RepRewards.VERY_HIGH);
            setRepPenaltyFaction(RepRewards.HIGH);
        } else {
            //setCreditReward(50000, 70000);
            setCreditReward(CreditReward.HIGH);
            setRepPenaltyPerson(RepRewards.EXTREME);
            setRepPenaltyFaction(RepRewards.HIGH);
        }

        setDefaultGARepRewards();

//		beginStageTrigger(Stage.DELIVER_VIP);
//		LocData data = new LocData(EntityLocationType.HIDDEN_NOT_NEAR_STAR, null, system);
//		triggerSpawnShipGraveyard(Factions.REMNANTS, 10, 10, data);
//		endTrigger();

        piratePayment = genRoundNumber(40000, 60000);
        mercPayment = getCreditsReward() / 2;

        if (variation == Variation.BASIC && rollProbability(PROB_PIRATE_KIDNAPPER)) {
            beginWithinHyperspaceRangeTrigger(destination, 3f, false, Stage.DELIVER_VIP);
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.HIGHER, Factions.INDEPENDENT, FleetTypes.PATROL_MEDIUM, system);
            triggerSetFleetOfficers(OfficerNum.MORE, OfficerQuality.HIGHER);
            triggerAutoAdjustFleetStrengthMajor();
            triggerSetStandardAggroPirateFlags();
            triggerFleetAllowLongPursuit();
            triggerSetFleetAlwaysPursue();
            triggerMakeNoRepImpact();
            triggerPickLocationTowardsPlayer(system.getHyperspaceAnchor(), 90f, getUnits(1.5f));
            triggerSpawnFleetAtPickedLocation("$bdvip_pirate", null);
            triggerOrderFleetInterceptPlayer();
            triggerFleetMakeImportant(null, Stage.DELIVER_VIP);
            endTrigger();
        }

        if (variation == Variation.KANTA && rollProbability(PROB_MERC_KIDNAPPER)) {
            mercFactionId = pickOne(MERC_FACTIONS.toArray(new String[0]));
            FactionAPI mercFaction = Global.getSector().getFaction(mercFactionId);
            theMercFaction = mercFaction.getDisplayNameWithArticle();

            beginWithinHyperspaceRangeTrigger(destination, 3f, true, Stage.DELIVER_VIP);
            triggerCreateFleet(FleetSize.VERY_LARGE, FleetQuality.VERY_HIGH, Factions.MERCENARY, FleetTypes.PATROL_LARGE, system);
            triggerSetFleetFaction(Factions.INDEPENDENT);
            triggerSetFleetOfficers(OfficerNum.MORE, OfficerQuality.HIGHER);
            triggerAutoAdjustFleetStrengthMajor();
            triggerMakeHostileAndAggressive();
            triggerFleetMakeFaster(true, 2, true);
            //triggerMakeNoRepImpact(); // this happens in dialog instead
            triggerFleetAllowLongPursuit();
            triggerSetFleetAlwaysPursue();
            triggerPickLocationTowardsPlayer(system.getHyperspaceAnchor(), 90f, getUnits(1.5f));
            triggerSpawnFleetAtPickedLocation("$bdvip_merc", null);
            triggerOrderFleetInterceptPlayer();
            triggerFleetMakeImportant(null, Stage.DELIVER_VIP);
            endTrigger();
        }

        if (variation == Variation.OSSUM && rollProbability(PROB_AOSSUM_KIDNAPPER)) {
            AOSSUMFactionId = pickOne(ANTIOSSUM_FACTIONS.toArray(new String[0]));
            FactionAPI aossumFaction = Global.getSector().getFaction(AOSSUMFactionId);
            theAOSSUMFaction = aossumFaction.getDisplayNameWithArticle();

            beginWithinHyperspaceRangeTrigger(destination, 3f, true, Stage.DELIVER_VIP);
            triggerCreateFleet(FleetSize.VERY_LARGE, FleetQuality.VERY_HIGH, Factions.TRITACHYON, FleetTypes.PATROL_LARGE, system);
            triggerSetFleetFaction(Factions.INDEPENDENT);
            triggerSetFleetOfficers(OfficerNum.MORE, OfficerQuality.HIGHER);
            triggerAutoAdjustFleetStrengthMajor();
            triggerMakeHostileAndAggressive();
            triggerFleetMakeFaster(true, 3, true);
            //triggerMakeNoRepImpact(); // this happens in dialog instead
            triggerFleetAllowLongPursuit();
            triggerSetFleetAlwaysPursue();
            triggerPickLocationTowardsPlayer(system.getHyperspaceAnchor(), 90f, getUnits(1.5f));
            triggerSpawnFleetAtPickedLocation("$bdvip_assoum-merc", null);
            triggerOrderFleetInterceptPlayer();
            triggerFleetMakeImportant(null, Stage.DELIVER_VIP);
            endTrigger();
        }

        return true;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
                                 Map<String, MemoryAPI> memoryMap) {
        if (action.equals("betrayal")) {
            DelayedFleetEncounter e = new DelayedFleetEncounter(genRandom, getMissionId());
            e.setDelayMedium();
            e.setLocationInnerSector(false, Factions.INDEPENDENT);
            e.beginCreate();
            e.triggerCreateFleet(FleetSize.LARGE, FleetQuality.SMOD_3, Factions.MERCENARY, FleetTypes.PATROL_LARGE, new Vector2f());
            e.triggerSetFleetOfficers(OfficerNum.MORE, OfficerQuality.UNUSUALLY_HIGH);
            e.triggerFleetSetFaction(Factions.INDEPENDENT);
            e.triggerFleetMakeFaster(true, 2, true);
            e.triggerSetFleetFlag("$bdvip_kantaConsequences");
            e.triggerMakeNoRepImpact();
            e.triggerSetStandardAggroInterceptFlags();
            e.endCreate();
            return true;
        }
        if (action.equals("betrayal-ossum")) {
            DelayedFleetEncounter o = new DelayedFleetEncounter(genRandom, getMissionId());
            o.setDelayMedium();
            o.setLocationInnerSector(false, Factions.HEGEMONY);
            o.beginCreate();
            triggerFleetSetCommander(occidens); // Unique!
            triggerFleetSetName("Eventide Squadron");
            o.triggerCreateFleet(FleetSize.VERY_LARGE, FleetQuality.SMOD_3, Factions.HEGEMONY, FleetTypes.PATROL_LARGE, new Vector2f());
            o.triggerSetFleetOfficers(OfficerNum.MORE, OfficerQuality.UNUSUALLY_HIGH);
            o.triggerFleetSetFaction(Factions.HEGEMONY);
            o.triggerFleetMakeFaster(true, 2, true);
            o.triggerSetFleetFlag("$bdvip_ossum_Consequences");
            o.triggerFleetSetNoFactionInName();
            o.triggerMakeNoRepImpact();
            o.triggerSetStandardAggroInterceptFlags();
            o.endCreate();
            return true;
        }
        return false;
    }



    protected void updateInteractionDataImpl() {
        if (getCurrentStage() != null) {
            set("$bdvip_stage", ((Enum)getCurrentStage()).name());
        }
        set("$bdvip_starName", system.getNameWithNoType());
        set("$bdvip_marketName", destination.getName());
        set("$bdvip_systemName", system.getNameWithLowercaseTypeShort());

        set("$bdvip_subjectRelation", subjectRelation);
        set("$bdvip_kantaRelationFirstName", kantaRelationFirstName);
        set("$bdvip_VIP", target);
        set("$bdvip_VIP_faction", target.getFaction().getId());
        set("$bdvip_VIPName", target.getNameString());
        set("$bdvip_VIPhisOrHer", target.getHisOrHer());
        set("$bdvip_VIPPost", target.getPost().toLowerCase());
        set("$bdvip_event", event);

        set("$bdvip_reward", Misc.getWithDGS(getCreditsReward()));
        set("$bdvip_piratePayment", Misc.getWithDGS(piratePayment));
        set("$bdvip_mercPayment", Misc.getWithDGS(mercPayment));
        set("$bdvip_theMercFaction", theMercFaction);
        set("$bdvip_mercFactionId", mercFactionId);
        set("$bdvip_timeLimit", (int)MISSION_DAYS);
        set("$bdvip_variation", variation);
    }

    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.DELIVER_VIP) {
            if (variation == Variation.BASIC) {
                info.addPara("Deliver the " + subjectRelation + " of " + target.getNameString() + " to " +
                        destination.getName() + " in the " + system.getNameWithLowercaseTypeShort() + ", for " +
                        event + ". " +
                        target.getNameString() + " is the " + target.getPost().toLowerCase() + ".", opad);
            } else if (variation == Variation.KANTA) {
                info.addPara("Deliver the " + subjectRelation + " of " + kantaRelationFirstName + " Kanta to " +
                        destination.getName() + " in the " + system.getNameWithLowercaseTypeShort() + ", for " +
                        event + ". " +
                        kantaRelationFirstName + " is kin to Kanta herself.", opad);
            }
        }
    }

    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.DELIVER_VIP) {
            info.addPara("Deliver person to " + destination.getName() + " in the " + system.getNameWithLowercaseTypeShort(), tc, pad);
            return true;
        }
        return false;
    }


    @Override
    public String getBaseName() {
        return "Deliver VIP";
    }

}


