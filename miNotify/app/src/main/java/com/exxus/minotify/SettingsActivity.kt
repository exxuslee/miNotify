package com.exxus.minotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = "mICokeBar"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d("qweasdqwe", "Key: $key Value: $value")
            }
        }
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        private val database = Firebase.database
        private val myRef = database.reference
        private var uniqueID: String? = null
        private val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"
        private var uuid : String? = null


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val kc0b : SwitchPreferenceCompat? = findPreference("kc0b")
            val kc1b : SwitchPreferenceCompat? = findPreference("kc1b")
            val kc2b : SwitchPreferenceCompat? = findPreference("kc2b")
            val kc3b : SwitchPreferenceCompat? = findPreference("kc3b")
            val kc4b : SwitchPreferenceCompat? = findPreference("kc4b")

            val kc0m : SwitchPreferenceCompat? = findPreference("kc0m")
            val kc1m : SwitchPreferenceCompat? = findPreference("kc1m")
            val kc2m : SwitchPreferenceCompat? = findPreference("kc2m")
            val kc3m : SwitchPreferenceCompat? = findPreference("kc3m")
            val kc4m : SwitchPreferenceCompat? = findPreference("kc4m")

            val listener =
                Preference.OnPreferenceChangeListener { preference, o ->
                    val pref = preference as SwitchPreferenceCompat
                    val key = pref.key
                    val value = o as Boolean

                    if (key == "kc0b" && value) {
                        if (kc1b!!.isChecked) notifyON("kc1b")
                        if (kc2b!!.isChecked) notifyON("kc2b")
                        if (kc3b!!.isChecked) notifyON("kc3b")
                        if (kc4b!!.isChecked) notifyON("kc4b")
                    }
                    if (key == "kc0b" && !value)
                    {
                        if (kc1b!!.isChecked) notifyOFF("kc1b")
                        if (kc2b!!.isChecked) notifyOFF("kc2b")
                        if (kc3b!!.isChecked) notifyOFF("kc3b")
                        if (kc4b!!.isChecked) notifyOFF("kc4b")
                    }

                    if (key == "kc0m" && value) {
                        if (kc1m!!.isChecked) notifyON("kc1m")
                        if (kc2m!!.isChecked) notifyON("kc2m")
                        if (kc3m!!.isChecked) notifyON("kc3m")
                        if (kc4m!!.isChecked) notifyON("kc4m")
                    }
                    if (key == "kc0m" && !value)
                    {
                        if (kc1m!!.isChecked) notifyOFF("kc1m")
                        if (kc2m!!.isChecked) notifyOFF("kc2m")
                        if (kc3m!!.isChecked) notifyOFF("kc3m")
                        if (kc4m!!.isChecked) notifyOFF("kc4m")
                    }

                    if (value) notifyON(key)
                    else notifyOFF(key)

                    true
                }

            kc0b?.onPreferenceChangeListener = listener
            kc1b?.onPreferenceChangeListener = listener
            kc2b?.onPreferenceChangeListener = listener
            kc3b?.onPreferenceChangeListener = listener
            kc4b?.onPreferenceChangeListener = listener
            kc0m?.onPreferenceChangeListener = listener
            kc1m?.onPreferenceChangeListener = listener
            kc2m?.onPreferenceChangeListener = listener
            kc3m?.onPreferenceChangeListener = listener
            kc4m?.onPreferenceChangeListener = listener

            uuid = id(requireContext())
            getToken()
        }


        private fun notifyON(key: String) {
            FirebaseMessaging.getInstance().subscribeToTopic(key)
            myRef.child(uuid!!).child(key).setValue(true)
        }


        private fun notifyOFF(key: String) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(key)
            myRef.child(uuid!!).child(key).setValue(false)
        }

        private fun getToken() {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    myRef.child(uuid!!+"/token").setValue(token)
                })
        }

        @Synchronized
        fun id(context: Context): String? {
            if (uniqueID == null) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE
                )
                uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
                if (uniqueID == null) {
                    uniqueID = UUID.randomUUID().toString()
                    val editor = sharedPrefs.edit()
                    editor.putString(PREF_UNIQUE_ID, uniqueID)
                    editor.commit()
                }
            }
            return uniqueID
        }

        companion object {
            private const val TAG = "MainActivity"
        }
    }



}