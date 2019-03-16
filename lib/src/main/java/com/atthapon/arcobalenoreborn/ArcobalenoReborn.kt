package com.atthapon.arcobalenoreborn

import android.app.Activity
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.app.ActivityManager
import android.os.Process


/**
 * Created by Atthapon Korkaew on 16/3/2019 AD.
 */
class ArcobalenoReborn : Activity() {
    private val KEY_RESTART_INTENTS: String = "reborn_restart_intents"

    fun triggerRebirth(context: Context) {
        triggerRebirth(context, getRestartIntent(context))
    }

    fun triggerRebirth(context: Context, nextIntents: Intent) {
        val intent = Intent(context, ArcobalenoReborn::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.putParcelableArrayListExtra(KEY_RESTART_INTENTS, arrayListOf(nextIntents))
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }

    fun getRestartIntent(context: Context): Intent {
        val packageName: String = context.packageName
        val defaultIntent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)

        defaultIntent?.apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
            return this
        }

        throw IllegalStateException(
            "Unable to determine default activity for "
                    + packageName
                    + ". Does an activity specify the DEFAULT category in its intent filter?"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intents = intent.getParcelableArrayListExtra<Intent>(KEY_RESTART_INTENTS)
        startActivities(intents.toArray(arrayOfNulls<Intent>(intents.size)))
        finish()
        Runtime.getRuntime().exit(0)
    }

    fun isRebornProcess(context: Context): Boolean {
        val currentPid = Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = manager.runningAppProcesses
        if (runningProcesses != null) {
            for (processInfo in runningProcesses) {
                if (processInfo.pid == currentPid && processInfo.processName.endsWith(":reborn")) {
                    return true
                }
            }
        }
        return false
    }
}