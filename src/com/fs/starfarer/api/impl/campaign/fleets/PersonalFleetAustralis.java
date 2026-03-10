package com.fs.starfarer.api.impl.campaign.fleets;

import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import data.scripts.ids.hegtalesIDs;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.missions.FleetCreatorMission;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.FleetQuality;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.FleetSize;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.OfficerNum;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.OfficerQuality;
import com.fs.starfarer.api.impl.campaign.missions.hub.MissionFleetAutoDespawn;
import com.fs.starfarer.api.loading.VariantSource;

public class PersonalFleetAustralis extends PersonalFleetScript {
	
	public PersonalFleetAustralis() {
		super("hegtales_australis");
		setMinRespawnDelayDays(10f);
		setMaxRespawnDelayDays(20f);
	}

	@Override
	protected MarketAPI getSourceMarket() {
		return Global.getSector().getEconomy().getMarket("eventide");
	}

	@Override
	public CampaignFleetAPI spawnFleet() {

		MarketAPI eventide = getSourceMarket();

		FleetCreatorMission m = new FleetCreatorMission(random);
		m.beginFleet();

		Vector2f loc = eventide.getLocationInHyperspace();
		
		m.triggerCreateFleet(FleetSize.HUGE, FleetQuality.DEFAULT, Factions.HEGEMONY, FleetTypes.PATROL_LARGE, loc);
		m.triggerSetFleetOfficers( OfficerNum.MORE, OfficerQuality.DEFAULT);
		m.triggerSetFleetCommander(getPerson());
		m.triggerSetFleetFaction(Factions.HEGEMONY);
		m.triggerSetPatrol();
		m.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_SOURCE_MARKET, eventide);
		m.triggerFleetSetNoFactionInName();
		m.triggerPatrolAllowTransponderOff();
		m.triggerFleetSetName("Eventide Noble Defense Armada");
		m.triggerFleetSetPatrolActionText("patrolling");
		m.triggerOrderFleetPatrol(eventide.getPlanetEntity());
		m.triggerFleetSetPatrolLeashRange(100.0f);

		CampaignFleetAPI fleet = m.createFleet();
		FleetDataAPI data = fleet.getFleetData();
		if (Global.getSector().getMemoryWithoutUpdate().contains(hegtalesIDs.HEGTALES_FINISHED_QUEST3))   {
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "heg_retaliation_standard"));
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "heg_retaliation_standard"));
		}
		else if (Global.getSector().getMemoryWithoutUpdate().contains(hegtalesIDs.OF_HOUSE_OSSUM))   {
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "heg_retaliation_standard"));
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "heg_retaliation_standard"));
		}
		else {
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "retribution_Standard"));
			data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "retribution_Standard"));
		}
		data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "enforcer_XIV_Elite"));
		data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "enforcer_XIV_Elite"));
		FleetMemberAPI member = null;
		fleet.removeScriptsOfClass(MissionFleetAutoDespawn.class);
		eventide.getContainingLocation().addEntity(fleet);
		fleet.setLocation(eventide.getPlanetEntity().getLocation().x, eventide.getPlanetEntity().getLocation().y);
		fleet.setFacing((float) random.nextFloat() * 360f);
		//data.addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "tempest_Attack"));

		FleetMemberAPI currFlag = fleet.getFlagship();
		currFlag.setFlagship(false);
		FleetMemberAPI australisFlagship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant("onslaught_xiv_Elite").clone());
		fleet.getFleetData().addFleetMember(australisFlagship);
		fleet.getFleetData().removeFleetMember(currFlag);
		fleet.getFleetData().setFlagship(australisFlagship);
		australisFlagship.setFlagship(true);
		fleet.getFlagship().getVariant().setSource(VariantSource.REFIT);
		fleet.getFlagship().setShipName("HSS My Brother in Ludd");
		australisFlagship.getRepairTracker().setCR(australisFlagship.getRepairTracker().getMaxCR());
		fleet.getFleetData().sort();
		fleet.forceSync();

		return fleet;
	}

	@Override
	public boolean canSpawnFleetNow() {
		MarketAPI eventide = Global.getSector().getEconomy().getMarket("eventide");
		if (eventide == null || eventide.hasCondition(Conditions.DECIVILIZED)) return false;
		if (!eventide.getFactionId().equals(Factions.HEGEMONY)) return false;
		return true;
	}

	@Override
	public boolean shouldScriptBeRemoved() {
		return false;
	}

}




