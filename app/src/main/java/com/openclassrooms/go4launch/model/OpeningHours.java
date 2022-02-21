package com.openclassrooms.go4launch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 14/01/2022
 */
public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
/*
    @SerializedName("periods")
    @Expose
    private List<Period> periods = null;
*/
    @SerializedName("weekday_text")
    @Expose
    private List<Object> weekdayText = null;

    /**
     *
     * @return
     * The openNow
     */
    public Boolean getOpenNow() {
        return openNow;
    }

    /**
     *
     * @param openNow
     * The open_now
     */
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

/*
    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }
*/

    /**
     *
     * @return
     * The weekdayText
     */
    public List<Object> getWeekdayText() {
        return weekdayText;
    }

    /**
     *
     * @param weekdayText
     * The weekday_text
     */
    public void setWeekdayText(List<Object> weekdayText) {
        this.weekdayText = weekdayText;
    }

}
