package com.benmohammad.mobiusapplication.addedittask.domain;

import com.spotify.dataenum.DataEnum;
import com.spotify.dataenum.dataenum_case;

@DataEnum
interface AddEditTaskMode_dataenum {

    dataenum_case Create();

    dataenum_case update(String id);
}
