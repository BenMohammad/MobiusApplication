package com.benmohammad.mobiusapplication.addedittask.view;

import android.os.Bundle;

import com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskMode;
import com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskModel;
import com.benmohammad.mobiusapplication.data.TaskBundlePacker;
import com.google.common.base.Optional;

import static androidx.core.util.Preconditions.checkNotNull;
import static com.benmohammad.mobiusapplication.data.TaskBundlePacker.taskDetailsFromBundle;

public class AddEditTaskModeBundlePacker {

    public static Bundle addEditTaskModelToBundle(AddEditTaskModel model) {
        Bundle b = new Bundle();
        b.putBundle("task_details", TaskBundlePacker.taskDetailsToBundle(model.details()));
        Optional<Bundle> modelBundle = addEditModeToBundle(model.mode());
        if(modelBundle.isPresent()) b.putBundle("add_edit_mode", modelBundle.get());
        return b;
    }

    public static AddEditTaskModel addEditTaskModelFromBundle(Bundle bundle) {
        return AddEditTaskModel.builder()
                .details(taskDetailsFromBundle(checkNotNull(bundle.getBundle("task_details"))))
                .mode(addEditTaskModeFromBundle(bundle.getBundle("add_edit_mode")))
                .build();
    }

    private static Optional<Bundle> addEditModeToBundle(AddEditTaskMode mode) {
        return mode.map(
                create -> Optional.absent(),
                update -> {
                    Bundle b = new Bundle();
                    b.putString("task_id", update.id());
                    return Optional.of(b);
                }
        );
    }


    private static AddEditTaskMode addEditTaskModeFromBundle(Bundle bundle) {
        if(bundle == null) return AddEditTaskMode.create();
        return AddEditTaskMode.update(checkNotNull(bundle.getString("task_id")));
    }
}
