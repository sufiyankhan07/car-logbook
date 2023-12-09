package com.cmtech.amslogbook.Permission



import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope


class PermissionRequest(val context: FragmentActivity) {

    var callBack:PermissionCallBack?=null

    interface PermissionCallBack{
        fun onPermissionGranted(type:String)
    }

    fun askLocationPermission(callBack:PermissionCallBack){


        PermissionX.init(context)
            .permissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .onExplainRequestReason{ scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "We need location permission to know your current location. Please accept our request.",
                    "Ok",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("success")
                }
            }
    }

    fun askCameraPermission(callBack:PermissionCallBack){
        this.callBack = callBack
        PermissionX.init(context)
            .permissions(android.Manifest.permission.CAMERA)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Without this permission the app is unable to capture images from camera. " +
                            "Are you sure you want to deny this permission?",
                    "RE-TRY",
                    "I'M SURE"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("camera")
                }
            }
    }

    fun askStoragePermissionOld(callBack:PermissionCallBack){
        this.callBack = callBack
        PermissionX.init(context)
            .permissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Without this permission the app is unable to capture images from gallery. " +
                            "Are you sure you want to deny this permission?",
                    "RE-TRY",
                    "I'M SURE"
                )
            }

            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("success")
                }
            }
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.BASE)
    fun askStoragePermissionNew(callBack:PermissionCallBack){
        this.callBack = callBack

        PermissionX.init(context)
            .permissions(android.Manifest.permission.READ_MEDIA_IMAGES)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Without this permission the app is unable to capture images from gallery. " +
                            "Are you sure you want to deny this permission?",
                    "RE-TRY",
                    "I'M SURE"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("success")
                }
            }
    }


    fun askAudioPermission(callBack:PermissionCallBack){
        this.callBack = callBack
        PermissionX.init(context)
            .permissions(android.Manifest.permission.RECORD_AUDIO)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Without this permission the app is unable to capture your voice. " +
                            "Are you sure you want to deny this permission?",
                    "RE-TRY",
                    "I'M SURE"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("success")
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askNotificationPermission(callBack:PermissionCallBack){


        PermissionX.init(context)
            .permissions(android.Manifest.permission.POST_NOTIFICATIONS)
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "We need notification permission to send notification. Please accept our request.",
                    "Ok",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    callBack.onPermissionGranted("success")
                }
            }
    }
}


