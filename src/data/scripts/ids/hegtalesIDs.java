package data.scripts.ids;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

public class hegtalesIDs {

        public static final String HEGTALES = "hegtales";


        //Missions
        public static String HEGTALES_FINISHED_QUEST1 = "$hegtales_Quest1_missionCompleted";
        public static String HEGTALES_FINISHED_QUEST2 = "$hegtales_Quest2_missionCompleted";
        public static String HEGTALES_FINISHED_QUEST3 = "$hegtales_Quest3_missionCompleted";
        public static String HEGTALES_FINISHED_QUEST4 = "$hegtales_Quest4_missionCompleted";
        public static String HEGTALES_FINISHED_QUEST5 = "$hegtales_Quest5_missionCompleted";
        public static String HEGTALES_FINISHED_QUEST6 = "$hegtales_Quest6_missionCompleted";
        public static String OF_HOUSE_OSSUM = "$hegtales_house_ossum_start";

        // People
        public static String HEGTALES_AUSTRALIS = "hegtales_australis";

        public static PersonAPI getPerson(String id) {
                return Global.getSector().getImportantPeople().getPerson(id);
        }
}
