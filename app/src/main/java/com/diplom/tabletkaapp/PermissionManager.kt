package com.diplom.tabletkaapp

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference

/**
 * Менеджер по получению разрешений
 */
class PermissionManager {
    private val TAG = "PermissionManager"
    private val REQUEST_CODE = 10

    private var activityWeakReference: WeakReference<Activity>? = null
    private var runnable: Runnable? = null
    private var permissionsNotGranted: MutableList<String> = mutableListOf()

    private fun checkPermission(activity: Activity, permission: String): Int {
        return ActivityCompat.checkSelfPermission(activity, permission)
    }

    /**
     * Метод запроса разрешений
     */
    public fun requestPermissions(
        activity: Activity, vararg permissions: String,
        runThenPermissionGranted: Runnable? = null
    ) {
        permissionsNotGranted.clear()
        permissions.forEach { permission ->
            if (checkPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission)
            }
        }
        if (permissionsNotGranted.isNotEmpty()) {
            runnable = runThenPermissionGranted
            activityWeakReference = WeakReference(activity)
            ActivityCompat.requestPermissions(
                activity,
                permissionsNotGranted.toTypedArray(),
                REQUEST_CODE
            )
        } else {
            runThenPermissionGranted?.run()
        }
    }

    /**
     * Метод для построения диалога
     */
    private fun buildDialog(
        dialogBuilder: AlertDialog.Builder,
        activity: Activity,
        permission: String
    ) =
        dialogBuilder.apply {
            setTitle("${permission} permission is not granted")
            setMessage(
                "This permission is not granted.\n" +
                        "Please grand permission in settings on your phone and try again"
            )
            setPositiveButton("Confirm") { _: DialogInterface?,
                                           _: Int ->

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivityForResult(intent, REQUEST_CODE)
            }
            this.create()
            this.show()
        }

    /**
     * Метод для обработки не выданных разрешений
     */
    private fun handleDeniedPermissions(
        activity: Activity,
        permissions: Array<out String>,
        grantResult: IntArray,
        deniedPermissions: MutableList<String>
    ) = permissions.zip(grantResult.toTypedArray())
        .toList()
        .filter {
            it.second == PackageManager.PERMISSION_DENIED &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, it.first)
        }
        .forEach {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, it.first)) {
                buildDialog(AlertDialog.Builder(activity), activity, it.first)
            } else {
                deniedPermissions.add(it.first)
            }
        }

    /**
     * Выдача результатов запроса разрешений
     */
    public fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResult: IntArray
    ) {
        if (requestCode != REQUEST_CODE)
            return
        activityWeakReference?.get()?.let { activity ->
            var deniedPermissions = mutableListOf<String>()
            handleDeniedPermissions(activity, permissions, grantResult, deniedPermissions)
            if (deniedPermissions.isEmpty()) {
                runnable?.run()
            }
            activityWeakReference = null
        }
    }

    /**
     * Обработка результатов
     */
    public fun onActivityResult(requestCode: Int, resultCode: Int)
    {
        if (requestCode != REQUEST_CODE)
            return
        activityWeakReference?.get()?.let { activity ->
            var deniedPermissions = mutableListOf<String>()
            if(resultCode == Activity.RESULT_CANCELED) {
                permissionsNotGranted.forEach {
                    if(checkPermission(activity, it) == PackageManager.PERMISSION_DENIED){
                        deniedPermissions.add(it)
                    }
                }
            }
            if (deniedPermissions.isEmpty()) {
                runnable?.run()
                permissionsNotGranted.clear()
            }
            activityWeakReference = null
        }
    }
}