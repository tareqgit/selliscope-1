package com.humaclab.selliscope.utility_db.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/***
 * Created by mtita on 22,August,2019.
 */
@Entity(tableName = "regular_performance_entity")
public class RegularPerformanceEntity implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int performance_id__db;

    public String date;



    public double distance;

    public String outlets_checked_in;



    public RegularPerformanceEntity() {
    }

    private RegularPerformanceEntity(Builder builder) {
        date = builder.date;
        distance = builder.distance;
        outlets_checked_in = builder.outlets_checked_in;
    }

    public static final class Builder {
        private String date;
        private double distance;
        private String outlets_checked_in;

        public Builder() {
        }

        public Builder withDate(String val) {
            date = val;
            return this;
        }

        public Builder withDistance(double val) {
            distance = val;
            return this;
        }

        public Builder withOutlets_checked_in(String val) {
            outlets_checked_in = val;
            return this;
        }

        public RegularPerformanceEntity build() {
            return new RegularPerformanceEntity(this);
        }
    }
}
