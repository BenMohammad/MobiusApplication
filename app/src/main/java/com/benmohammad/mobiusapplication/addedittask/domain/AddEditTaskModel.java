package com.benmohammad.mobiusapplication.addedittask.domain;

import com.benmohammad.mobiusapplication.data.TaskDetails;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AddEditTaskModel {

    public abstract AddEditTaskMode mode();

    public abstract TaskDetails details();

    public AddEditTaskModel withDetails(TaskDetails details) {
        return toBuilder().details(details).build();
    }

    public abstract Builder toBuilder();

    public static Builder build() {
        return new AutoValue_AddEditTaskModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder mode(AddEditTaskMode mode);

        public abstract Builder details(TaskDetails details);

        public abstract AddEditTaskModel build();
    }
}