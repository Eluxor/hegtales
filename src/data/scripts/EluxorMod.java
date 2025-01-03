package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;

import java.util.Random;



public class EluxorMod extends BaseModPlugin {

    //Characters and the Retribution Generation
    protected SectorEntityToken debris;
    public static String SPACE_REARADMIRAL = "rearadmiral";
    public static String POST_EXECUTIVE_COUNCILLOR = "councillor";

    //@Override
    public void onGameLoad(boolean newGame)  {
        if (Global.getSector().getMemoryWithoutUpdate().get("$hegtales_characters_generated") == null) {
            Global.getSector().getMemoryWithoutUpdate().set("$hegtales_characters_generated", true);
            {
                ImportantPeopleAPI ip = Global.getSector().getImportantPeople();

                MarketAPI market = Global.getSector().getEconomy().getMarket("eventide");
                if (market != null)
                {

                    //Borealis
                    PersonAPI person = Global.getFactory().createPerson();
                    person.setFaction("hegemony");
                    person.setGender(FullName.Gender.MALE);
                    person.setPostId(POST_EXECUTIVE_COUNCILLOR);
                    person.setRankId(Ranks.SPACE_ADMIRAL);
                    person.getName().setFirst("Borealis");
                    person.getName().setLast("Ossum");
                    person.setVoice(Voices.SOLDIER);
                    person.setImportance(PersonImportance.VERY_HIGH);
                    person.setPortraitSprite("graphics/hegtales/portraits/hegtales_borealis.png");
                    person.setId("hegtales_borealis");
                    person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
                    person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
                    person.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
                    person.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
                    person.getStats().setLevel(4);
                    market.getCommDirectory().addPerson(person, 0); // FIRST
                    market.getCommDirectory().getEntryForPerson(person).setHidden(false);
                    market.addPerson(person);

                    //Australis
                    PersonAPI person3 = Global.getFactory().createPerson();
                    person3.setId("hegtales_australis");
                    person3.setFaction(Factions.HEGEMONY);
                    person3.setGender(FullName.Gender.MALE);
                    person3.setPostId(Ranks.POST_BASE_COMMANDER);
                    person3.setRankId(SPACE_REARADMIRAL);
                    person3.setImportance(PersonImportance.VERY_HIGH);
                    person3.getName().setFirst("Australis");
                    person3.getName().setLast("Ossum");
                    person3.setPortraitSprite("graphics/hegtales/portraits/hegtales_australis.png");
                    person3.addTag(Tags.CONTACT_MILITARY);
                    person3.setVoice(Voices.OFFICIAL);
                    person3.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
                    person3.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
                    market.getCommDirectory().addPerson(person3, 1); // SECOND
                    market.getCommDirectory().getEntryForPerson(person3).setHidden(true);
                    market.addPerson(person3);

                    //Hannah person4
                    PersonAPI person4 = Global.getFactory().createPerson();
                    person4.setId("hegtales_hannah");
                    person4.setFaction(Factions.HEGEMONY);
                    person4.setGender(FullName.Gender.FEMALE);
                    person4.setRankId(Ranks.CITIZEN);
                    person4.setPostId(Ranks.POST_ARISTOCRAT);
                    person4.setImportance(PersonImportance.MEDIUM);
                    person4.getName().setFirst("Hannah");
                    person4.getName().setLast("Stahl-Ossum");
                    person4.setPortraitSprite("graphics/hegtales/portraits/hegtales_hannah.png");
                    person4.addTag(Tags.CONTACT_SCIENCE);
                    person4.setVoice(Voices.BUSINESS);
                    market.getCommDirectory().addPerson(person4, 2); // THIRD
                    market.getCommDirectory().getEntryForPerson(person4).setHidden(true);
                    market.addPerson(person4);

                    //Servum
                    PersonAPI person5 = Global.getFactory().createPerson();
                    person5.setId("hegtales_servum");
                    person5.setFaction(Factions.PIRATES);
                    person5.setGender(FullName.Gender.MALE);
                    person5.setRankId(Ranks.POST_CRIMINAL);
                    person5.setPostId(Ranks.POST_WARLORD);
                    person5.setImportance(PersonImportance.MEDIUM);
                    person5.getName().setFirst("Servum");
                    person5.getName().setLast("Servo");
                    person5.setPortraitSprite("graphics/hegtales/portraits/hegtales_servum.png");
                    person5.setVoice(Voices.SPACER);

                    //Isaias
                    PersonAPI person6 = Global.getFactory().createPerson();
                    person6.setId("hegtales_isaias");
                    person6.setFaction(Factions.DIKTAT);
                    person6.setGender(FullName.Gender.MALE);
                    person6.setRankId(Ranks.SPACE_COMMANDER);
                    person6.setPostId(Ranks.POST_PATROL_COMMANDER);
                    person6.getName().setFirst("Isaias");
                    person6.getName().setLast("Young");
                    person6.setPortraitSprite("graphics/hegtales/portraits/hegtales_isaias.png");
                    person6.setVoice(Voices.SOLDIER);
                    person6.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
                    person6.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 1);
                    person6.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
                    person6.getStats().setLevel(3);

                    //Defectum
                    PersonAPI person7 = Global.getFactory().createPerson();
                    person7.setId("hegtales_defectum");
                    person7.setFaction(Factions.LIONS_GUARD);
                    person7.setGender(FullName.Gender.MALE);
                    person7.setRankId(Ranks.SPACE_ADMIRAL);
                    person7.setPostId(Ranks.POST_FLEET_COMMANDER);
                    person7.getName().setFirst("Defectum");
                    person7.getName().setLast("Certum");
                    person7.setPortraitSprite("graphics/hegtales/portraits/hegtales_defectum.png");
                    person7.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
                    person7.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
                    person7.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
                    person7.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
                    person7.getStats().setLevel(4);

                    //Prima
                    PersonAPI person8 = Global.getFactory().createPerson();
                    person8.setId("hegtales_recruit_prima");
                    person8.setFaction(Factions.HEGEMONY);
                    person8.setGender(FullName.Gender.FEMALE);
                    person8.setRankId(Ranks.SPACE_LIEUTENANT);
                    person8.setPostId(Ranks.POST_OFFICER);
                    person8.getName().setFirst("Prima");
                    person8.getName().setLast("Ossum");
                    person8.setPortraitSprite("graphics/hegtales/portraits/hegtales_prima.png");
                    person8.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
                    person8.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
                    person8.setPersonality(Personalities.RECKLESS);
                    person8.getStats().setLevel(2);

                    Global.getSector().getImportantPeople().addPerson(person);
                    Global.getSector().getImportantPeople().addPerson(person3);
                    Global.getSector().getImportantPeople().addPerson(person4);
                    Global.getSector().getImportantPeople().addPerson(person5);
                    Global.getSector().getImportantPeople().addPerson(person6);
                    Global.getSector().getImportantPeople().addPerson(person7);
                    Global.getSector().getImportantPeople().addPerson(person8);
                    // so the person can be retrieved by id


                }
                //No one added in Chico... Yet.
                //market =  Global.getSector().getEconomy().getMarket("chicomoztoc");
                //if (market != null)
                //{

                //}


                market =  Global.getSector().getEconomy().getMarket("jangala");
                if (market != null)
                {
                    //Corby
                    PersonAPI person2 = Global.getFactory().createPerson();
                    person2.setId("hegtales_corby");
                    person2.setFaction(Factions.HEGEMONY);
                    person2.setGender(FullName.Gender.MALE);
                    person2.setRankId(Ranks.CITIZEN);
                    person2.setPostId(Ranks.POST_MERCHANT);
                    person2.setImportance(PersonImportance.MEDIUM);
                    person2.getName().setFirst("Corby");
                    person2.getName().setLast("Rufus");
                    person2.setPortraitSprite("graphics/hegtales/portraits/hegtales_corby.png");
                    person2.getStats().setSkillLevel(Skills.BULK_TRANSPORT, 1);
                    person2.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
                    person2.addTag(Tags.CONTACT_TRADE);
                    person2.addTag(Tags.CONTACT_UNDERWORLD);
                    person2.setVoice(Voices.BUSINESS);
                    market.getCommDirectory().addPerson(person2, 5); // LAST
                    market.addPerson(person2);
                    ip.addPerson(person2);
                    market.getCommDirectory().getEntryForPerson(person2).setHidden(true);

                    Global.getSector().getImportantPeople().addPerson(person2);
                }
                market =  Global.getSector().getEconomy().getMarket("hesperus");
                if (market != null) {
                    PersonAPI saltus = Global.getFactory().createPerson();
                    saltus.setId("hegtales_saltus");
                    saltus.setRankId(Ranks.KNIGHT_CAPTAIN);
                    saltus.setPostId(Ranks.POST_FLEET_COMMANDER);
                    saltus.setImportance(PersonImportance.VERY_HIGH);
                    saltus.addTag(Tags.CONTACT_MILITARY);
                    saltus.addTag(Tags.CONTACT_TRADE);
                    saltus.getName().setFirst("Greenearth");
                    saltus.getName().setLast("Salt");
                    saltus.setGender(FullName.Gender.MALE);
                    saltus.setPortraitSprite("graphics/hegtales/portraits/hegtales_saltus.png");
                    saltus.setFaction(Factions.LUDDIC_PATH);
                    saltus.setVoice(Voices.SPACER);
                    market.addPerson(saltus);
                    market.getCommDirectory().addPerson(saltus, 5); // LAST
                    market.addPerson(saltus);
                    ip.addPerson(saltus);
                    saltus.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
                    saltus.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
                    saltus.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
                    saltus.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
                    saltus.getStats().setLevel(4);
                    market.getCommDirectory().getEntryForPerson(saltus).setHidden(true);

                    Global.getSector().getImportantPeople().addPerson(saltus);
                }

                market =  Global.getSector().getEconomy().getMarket("umbra");
                if (market != null) {
                    PersonAPI frusca = Global.getFactory().createPerson();
                    frusca.setId("hegtales_frusca");
                    frusca.setRankId(Ranks.SPACE_COMMANDER);
                    frusca.setPostId(Ranks.POST_PORTMASTER);
                    frusca.setImportance(PersonImportance.HIGH);
                    frusca.addTag(Tags.CONTACT_TRADE);
                    frusca.addTag(Tags.CONTACT_UNDERWORLD);
                    frusca.getName().setFirst("Frusca");
                    frusca.getName().setLast("Canis");
                    frusca.setGender(FullName.Gender.MALE);
                    frusca.setPortraitSprite("graphics/hegtales/portraits/hegtales_frusca.png");
                    frusca.setFaction(Factions.PIRATES);
                    market.addPerson(frusca);
                    market.getCommDirectory().addPerson(frusca, 1); // LAST
                    market.addPerson(frusca);
                    ip.addPerson(frusca);
                    market.getCommDirectory().getEntryForPerson(frusca).setHidden(false);
                    People.assignPost(market, Ranks.POST_PORTMASTER , frusca);

                    Global.getSector().getImportantPeople().addPerson(frusca);
                }
            }
        }
        if (Global.getSector().getMemoryWithoutUpdate().get("$hegtales_debris_generated") == null) {
            Global.getSector().getMemoryWithoutUpdate().set("$hegtales_debris_generated", true);
            if (Global.getSector().getStarSystem("Duzahk") != null) {
                StarSystemAPI system = Global.getSector().getStarSystem("Duzahk");
                for(Object object : system.getEntities(CampaignTerrainAPI.class)) {
                    if (object instanceof SectorEntityToken) {
                        DerelictShipEntityPlugin.DerelictShipData paramrat = new DerelictShipEntityPlugin.DerelictShipData(new ShipRecoverySpecial.PerShipData(Global.getSettings().getVariant("heg_retaliation_damaged"), ShipCondition.BATTERED, "HSS Retaliation", "hegemony", 0.0F), false);
                        SectorEntityToken retaliationentity = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, paramrat);
                        retaliationentity.setCircularOrbitPointingDown(system.getEntityById("Duzahk"), 90.0F, 1150.0F, 50.0F);
                        retaliationentity.setDiscoveryXP(250.0F);
                        retaliationentity.setDiscoverable(true);
                        retaliationentity.setSalvageXP(250.0F);
                        retaliationentity.getMemoryWithoutUpdate().set("$hegtales_ret_eventRef", true);
                        //retaliationentity.setCustomDescriptionId("hss_retaliation_wreck_desc");
                        retaliationentity.setId("hegtalesretribution");
                        retaliationentity.addTag("unrecoverable");
                        retaliationentity.addTag("hss_retaliation_wreck_tag");

                        //This makes debris to appear next to the ship :3 Taken from Askonia/Kumari Debris
                        DebrisFieldParams r = new DebrisFieldParams(
                                250f, // field radius - should not go above 1000 for performance reasons
                                -1f, // density, visual - affects number of debris pieces
                                10000000f, // duration in days
                                0f); // days the field will keep generating glowing pieces
                        r.source = DebrisFieldSource.MIXED;
                        r.baseSalvageXP = 250; // base XP for scavenging in field
                        debris = Misc.addDebrisField(system, r, StarSystemGenerator.random);
                        SalvageSpecialAssigner.assignSpecialForDebrisField(debris);
                        debris.setCircularOrbit(retaliationentity, 90, 50, 100);

                        break;
                    }

                    //debris fields around gate

                }
            }
        }
    }

    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.
}
