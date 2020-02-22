package com.benmohammad.mobiusapplication.statistics.domain;

import com.benmohammad.mobiusapplication.data.Task;
import com.google.common.collect.ImmutableList;
import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
interface StatisticsEvent_dataenum {

    dataenum_case TasksLoaded(ImmutableList<Task> tasks);

    dataenum_case TasksLoadingFailed();
}
