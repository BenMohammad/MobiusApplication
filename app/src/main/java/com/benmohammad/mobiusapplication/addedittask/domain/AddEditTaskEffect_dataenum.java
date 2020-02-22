package com.benmohammad.mobiusapplication.addedittask.domain;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskDetails;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
interface AddEditTaskEffect_dataenum {

    dataenum_case NotifyEmptyTaskNotAllowed();

    dataenum_case CreateTask(TaskDetails taskDetails);

    dataenum_case SaveTask(Task task);

    dataenum_case Exit(boolean successfull);
}
