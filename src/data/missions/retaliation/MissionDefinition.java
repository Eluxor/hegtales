package data.missions.retaliation;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

import java.awt.*;


public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);

		api.setFleetTagline(FleetSide.PLAYER, "Admiral Ossum's Force");
		api.setFleetTagline(FleetSide.ENEMY, "Traitors of the Hegemony");

		api.addBriefingItem("Sink the HSS Executor and send Andrada to hell.");
		api.addBriefingItem("Destroy the HSS Oaxaca and get rid of traitorous Hyder.");
		api.addBriefingItem("You cannot win.");
		api.addBriefingItem("This mission is truly Impossible if you got the Ship/Weapon Pack mod.");

		FleetMemberAPI retaliation = api.addToFleet(FleetSide.PLAYER, "heg_retaliation_standard", FleetMemberType.SHIP, "HSS Retaliation", true);
		PersonAPI ossum = Global.getSector().getFaction("hegemony").createRandomPerson(FullName.Gender.MALE);
		ossum.setId("hegtales_christian");
		ossum.getName().setFirst("Christian");
		ossum.getName().setLast("Ossum");
		ossum.getName().setGender(FullName.Gender.MALE);
		ossum.setPersonality("aggressive");
		ossum.setPortraitSprite("graphics/hegtales/portraits/hegtales_christian.png");
		ossum.setFaction("hegemony");
		ossum.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
		ossum.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
		ossum.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
		ossum.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
		ossum.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
		ossum.getStats().setLevel(5);
		retaliation.setCaptain(ossum);

		FleetMemberAPI pride = api.addToFleet(FleetSide.PLAYER, "enforcer_XIV_Elite", FleetMemberType.SHIP, "HSS Pride of Eventide", false);
		FleetMemberAPI house = api.addToFleet(FleetSide.PLAYER, "enforcer_XIV_Elite", FleetMemberType.SHIP, "HSS House Ossum", false);
		api.addToFleet(FleetSide.PLAYER, "lasher_CS", FleetMemberType.SHIP,"HSS Right Hook",  false);
		api.addToFleet(FleetSide.PLAYER, "lasher_CS", FleetMemberType.SHIP,"HSS Left Hook",  false);
		api.defeatOnShipLoss("HSS Retaliation");

		PersonAPI eventide = Global.getSector().getFaction("hegemony").createRandomPerson(FullName.Gender.MALE);
		eventide.getName().setLast("Sang");
		eventide.getName().setGender(FullName.Gender.MALE);
		eventide.setPersonality("aggressive");
		eventide.setPortraitSprite("graphics/portraits/portrait_hegemony01.png");
		eventide.setFaction("hegemony");
		eventide.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
		eventide.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
		eventide.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
		eventide.getStats().setLevel(3);
		pride.setCaptain(eventide);

		PersonAPI amanda = Global.getSector().getFaction("hegemony").createRandomPerson(FullName.Gender.MALE);
		amanda.getName().setFirst("Phillip");
		amanda.getName().setLast("Ossum");
		amanda.getName().setGender(FullName.Gender.FEMALE);
		amanda.setPersonality("aggressive");
		amanda.setPortraitSprite("graphics/portraits/portrait_hegemony17.png");
		amanda.setFaction("hegemony");
		amanda.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
		amanda.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
		amanda.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
		amanda.getStats().setLevel(3);
		house.setCaptain(amanda);


		// Set up the enemy fleet.
		//api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, "HSS Executor", false);

		PersonAPI andrada = Global.getSector().getFaction("hegemony").createRandomPerson(FullName.Gender.MALE);
		andrada.setId("hegtales_andrada");
		andrada.getName().setFirst("Phillip");
		andrada.getName().setLast("Andrada");
		andrada.getName().setGender(FullName.Gender.MALE);
		andrada.setPersonality("aggressive");
		andrada.setPortraitSprite("graphics/hegtales/portraits/hegtales_andrada.png");
		andrada.setFaction("hegemony");
		andrada.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
		andrada.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
		andrada.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
		andrada.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
		andrada.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
		andrada.getStats().setLevel(5);

		if (Global.getSettings().getModManager().isModEnabled("swp")) {

			FleetMemberAPI executor = api.addToFleet(FleetSide.ENEMY, "swp_conquest_xiv_eli", FleetMemberType.SHIP,"HSS Executor", true);executor.setCaptain(andrada);;
			api.addToFleet(FleetSide.ENEMY, "swp_hammerhead_xiv_eli", FleetMemberType.SHIP,"HSS Opis Pride", false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "swp_hammerhead_xiv_eli", FleetMemberType.SHIP,"HSS High on Octane", false).getRepairTracker().setCR(0.85f);
		}
		else {
			FleetMemberAPI executor = api.addToFleet(FleetSide.ENEMY, "conquest_Standard", FleetMemberType.SHIP,"HSS Executor", true);executor.setCaptain(andrada);;
			api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP,"HSS Opis Pride", false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, "HSS High on Octane",false).getRepairTracker().setCR(0.85f);
		}

		FleetMemberAPI dominator = api.addToFleet(FleetSide.ENEMY, "dominator_XIV_Elite", FleetMemberType.SHIP,"HSS Oaxaca", false);
		PersonAPI dominatorCaptain = Global.getSector().getFaction("hegemony").createRandomPerson(FullName.Gender.FEMALE);
		dominatorCaptain.getStats().setLevel(4);
		dominatorCaptain.setPortraitSprite("graphics/hegtales/portraits/hegtales_hyder.png");
		dominatorCaptain.getName().setFirst("Oxana");
		dominatorCaptain.getName().setLast("Hyder");
		dominatorCaptain.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
		dominatorCaptain.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
		dominatorCaptain.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
		dominatorCaptain.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
		dominatorCaptain.setPersonality("aggressive");
		dominator.setCaptain(dominatorCaptain);
		dominator.getRepairTracker().setCR(0.85f);

		api.addToFleet(FleetSide.ENEMY, "condor_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "condor_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_hegemony_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_hegemony_Standard", FleetMemberType.SHIP, false);

		// Set up the map.
		float width = 12000f;
		float height = 14000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

		float minX = -width/2;
		float minY = -height/2;

		for (int i = 0; i < 7; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 800f;
			api.addNebula(x, y, radius);
		}


		api.addAsteroidField(minX, minY + height * 0.5f, 0, 18000f,
				20f, 70f, 250);

		api.addPlanet(0, 4000f, 750f, "rocky_metallic", 250f);
		api.setBackgroundSpriteName("graphics/backgrounds/background3.jpg");
		api.addPlanet(0, 0f,1250f, "star_red_giant", 1f);
	}
}





