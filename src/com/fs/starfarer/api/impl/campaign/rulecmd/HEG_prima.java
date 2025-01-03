package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;


public class HEG_prima extends BaseCommandPlugin {
    public HEG_prima() {
    }
    //Taken from Timid stuff.
    //The whole Content Unlocking missions for House rao but not randomised

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        } else {
            if (params.isEmpty()) {

                PersonAPI prima = Global.getSector().getImportantPeople().getPerson("hegtales_recruit_prima");
                prima.setPersonality(Personalities.AGGRESSIVE);
                TextPanelAPI text = dialog.getTextPanel();
                Color hl = Misc.getHighlightColor();
                text.addSkillPanel(prima, false);
                text.setFontSmallInsignia();
                String personality = Misc.lcFirst(prima.getPersonalityAPI().getDisplayName());
                text.addParagraph("Personality: " + personality + ", level: ");
                text.highlightInLastPara(hl, new String[]{personality, ""});
                text.addParagraph(prima.getPersonalityAPI().getDescription());
                text.setFontInsignia();
                dialog.getVisualPanel().showSecondPerson(prima);
                Global.getSector().getMemoryWithoutUpdate().set("$hegtales_recruit_prima", prima, 0.0F);
            } else {
                PersonAPI officer = (PersonAPI)Global.getSector().getMemoryWithoutUpdate().get("$hegtales_recruit_prima");
                officer.setPostId(Ranks.POST_OFFICER);
                officer.getMemoryWithoutUpdate().set("$hegtales_recruit_prima_with", true);
                Global.getSector().getPlayerFleet().getFleetData().addOfficer(officer);
                AddRemoveCommodity.addOfficerGainText(officer, dialog.getTextPanel());
                officer.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_LEVEL, 8f);
                officer.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, 3);
            }

            return true;
        }
    }
}