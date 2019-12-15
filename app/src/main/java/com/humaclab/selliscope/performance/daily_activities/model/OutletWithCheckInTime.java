package com.humaclab.selliscope.performance.daily_activities.model;

import com.humaclab.selliscope.model.Outlets;

import java.io.Serializable;

/***
 * Created by mtita on 24,August,2019.
 */
public class OutletWithCheckInTime  implements Serializable {

        Outlets.Outlet mOutlet;
        String time;

        public OutletWithCheckInTime(Outlets.Outlet outlet, String time) {
            mOutlet = outlet;
            this.time = time;
        }

        public Outlets.Outlet getOutlet() {
            return mOutlet;
        }

        public void setOutlet(Outlets.Outlet outlet) {
            mOutlet = outlet;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

}
