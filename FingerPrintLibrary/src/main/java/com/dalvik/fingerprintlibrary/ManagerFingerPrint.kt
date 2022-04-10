package com.dalvik.fingerprintlibrary

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

@RequiresApi(Build.VERSION_CODES.P)
class ManagerFingerPrint private constructor(private val activity: WeakReference<AppCompatActivity>) {

    //Global variables
    private var callback: (Boolean, String) -> Unit = { _, _ -> }
    private val requiredPermissions = Manifest.permission.USE_BIOMETRIC

    //LoginWithFingerPrint Variables
    private var cancellationSignal: CancellationSignal? = null

    companion object {
        fun from(activity: AppCompatActivity) = ManagerFingerPrint(WeakReference(activity))
    }

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                callback(false, "$errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                activity.get()?.let {
                    callback(true, activity.get()!!.getString(R.string.msg_authentication_success))
                }
            }
        }


    fun initAuthentication(callback: (Boolean, String) -> Unit) {
        this.callback = callback
        checkBiometricSupport()
    }

    private val permissionCheck =
        activity.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { grantResults ->
                sendResult(grantResults)
            }

    private fun sendResult(grantPermission: Boolean) {
        if (grantPermission) {
            scanFingerPrint()
        } else {
            activity.get()?.let {
                callback(
                    false,
                    activity.get()!!.getString(R.string.msg_fingerprint_not_enable_settings)
                )
            }

        }
    }

    private fun scanFingerPrint() {
        activity.get()?.let {
            val biometricPrompt: BiometricPrompt = BiometricPrompt.Builder(activity.get())
                .setTitle(activity.get()!!.getString(R.string.msg_title_authentication))
                .setSubtitle(
                    activity.get()!!.getString(R.string.msg_principal_autheintication_required)
                )
                .setNegativeButton(
                    activity.get()!!.getString(R.string.msg_label_cancel),
                    activity.get()!!.mainExecutor
                ) { _, _ ->
                    callback(false, activity.get()!!.getString(R.string.msg_cancel_by_user))
                }.build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                activity.get()!!.mainExecutor,
                authenticationCallback
            )
        }

    }

    private fun getCancellationSignal(): CancellationSignal {

        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            activity.get()?.let {
                callback(false, activity.get()!!.getString(R.string.msg_cancel_by_user))
            }
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {

        val keyguardManager: KeyguardManager =
            activity.get()?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure) {
            callback(
                false,
                activity.get()!!.getString(R.string.msg_fingerprint_not_enable_settings)
            )
            return false
        }

        permissionCheck?.launch(requiredPermissions)

        return if (activity.get()?.packageManager!!.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }

}