package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashSet;
import java.util.Set;

public class PillRegimenBuilder {

    private PillRegimen pillRegimen = new PillRegimen();


    public PillRegimenBuilder withDosages(Set<Dosage> dosages) {
        pillRegimen.setDosages(dosages);
        return this;
    }

    public PillRegimenBuilder withExternalId(String externalId) {
        pillRegimen.setExternalId(externalId);
        return this;
    }

    public PillRegimenBuilder withReminderRepeatWindowInHours(int reminderRepeatWindowInHours) {
        pillRegimen.setReminderRepeatWindowInHours(reminderRepeatWindowInHours);
        return this;
    }

    public PillRegimenBuilder withReminderRepeatIntervalInMinutes(int reminderRepeatIntervalInMinutes) {
        pillRegimen.setReminderRepeatIntervalInMinutes(reminderRepeatIntervalInMinutes);
        return this;
    }

    public PillRegimen build() {
        return pillRegimen;
    }

    public static PillRegimenBuilder newPillRegimen() {
        return new PillRegimenBuilder();
    }

    public PillRegimenBuilder withSingleDosage(final Dosage dosage) {
        pillRegimen.setDosages(new HashSet<Dosage>() {{
            add(dosage);
        }});
        return this;
    }
}
