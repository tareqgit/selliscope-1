/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.humaclab.selliscope.geo_fence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.gson.Gson
import com.humaclab.selliscope.SelliscopeApiEndpointInterface
import com.humaclab.selliscope.SelliscopeApplication
import com.humaclab.selliscope.geo_fence.model.GeoResponse
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass
import com.humaclab.selliscope.utils.SessionManager
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class GeofenceTransitionsJobIntentService : JobIntentService() {

  companion object {
    private const val LOG_TAG = "GeoTrIntentService"

    private const val JOB_ID = 573

    fun enqueueWork(context: Context, intent: Intent) {
      enqueueWork(
          context,
          GeofenceTransitionsJobIntentService::class.java, JOB_ID,
          intent)
    }
  }

  override fun onHandleWork(intent: Intent) {
    val geofencingEvent = GeofencingEvent.fromIntent(intent)
    if (geofencingEvent.hasError()) {
      val errorMessage = GeofenceErrorMessages.getErrorString(this,
          geofencingEvent.errorCode)
      Log.e(LOG_TAG, errorMessage)
      return
    }

    handleEvent(geofencingEvent)
  }

  private fun handleEvent(event: GeofencingEvent) {
    Log.d("tareq_test","event: "+event.geofenceTransition)
    if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER  ) {
      val reminder = getFirstReminder(event.triggeringGeofences)
      val message = reminder?.message +" Entered"
      val latLng = reminder?.latLng
      if (message != null && latLng != null) {
        sendNotification(this, message, latLng)
        getSharedPreferences("ReminderRepository", Context.MODE_PRIVATE).edit().putString("getIn", CurrentTimeUtilityClass.getCurrentTimeStamp() ).apply()
      }
    }

    if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

      val reminder = getFirstReminder(event.triggeringGeofences)
      val message = reminder?.message +" Exit"
      println(message)

      val latLng = reminder?.latLng
      if (message != null && latLng != null) {
        sendNotification(this, message, latLng)

        val sessionManager = SessionManager(this)
        val apiService = SelliscopeApplication.getRetrofitInstance2(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface::class.java)

        apiService.postGeoFence(reminder.id.toInt(),  CurrentTimeUtilityClass.getCurrentTimeStamp(),   getSharedPreferences("ReminderRepository", Context.MODE_PRIVATE).getString("getIn",null))
                .enqueue(object : retrofit2.Callback<GeoResponse> {
                  override fun onFailure(call: Call<GeoResponse>, t: Throwable) {
                  //  TODO("Not yet implemented")

                    Log.d("tareq_test", "OnFail:" + t.message)


                  }

                  override fun onResponse(call: Call<GeoResponse>, response: Response<GeoResponse>) {
                   // TODO("Not yet implemented")
                    Log.d("tareq_test","onRes: "+ Gson().toJson(response.body()))
                    getSharedPreferences("ReminderRepository", Context.MODE_PRIVATE).edit().remove("getIn").apply()

                  }

                })

      }
    }
  }

  private fun getFirstReminder(triggeringGeofences: List<Geofence>): Reminder? {
    val firstGeofence = triggeringGeofences[0]
    return ReminderRepository(applicationContext).get(firstGeofence.requestId)
  }
}