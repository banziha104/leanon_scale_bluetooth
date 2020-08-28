package com.opusone.leanon.leanon_scale_bluetooth

import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionController(val activity: AppCompatActivity, val permissions: Array<String>) {

    companion object {
        val REQ_FLAG  = 10005

        fun onCheckResult(grantResults: IntArray): Boolean {
            var checkResult = true
            // 권한처리 결과값을 반복문을 돌면서 확인한 후 하나라도 승인되지 않았다면 false를 리턴해준다
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }
            return checkResult
        }

    }

    fun checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()
        } else {
            callInit(activity)
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        var isGranted = true
        for (permssion in permissions) {
            if (activity.checkSelfPermission(permssion) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
                break
            }
        }

        if (!isGranted) {
            activity.requestPermissions(permissions, REQ_FLAG)
        } else{
            callInit(activity)
        }

    }

    private fun callInit(activity: Activity){
        if(activity is CallBack){
            (activity as CallBack).init()
        } else {
            throw RuntimeException("must implement this.CallBack")
        }
    }

    interface CallBack{
        fun init()
    }
}