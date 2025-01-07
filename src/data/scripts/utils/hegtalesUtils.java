package data.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;

public class hegtalesUtils {

    public static final String PERSON_BOREALIS = "hegtales_borealis";
    public static final String PERSON_AUSTRALIS = "hegtales_australis";

    public static PersonAPI getBorealis() {
        return Global.getSector().getImportantPeople().getPerson(PERSON_BOREALIS);
    }
    public static PersonAPI getAustralis() {
        return Global.getSector().getImportantPeople().getPerson("hegtales_australis");
    }
}
