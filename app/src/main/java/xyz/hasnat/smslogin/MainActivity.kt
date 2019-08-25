package xyz.hasnat.smslogin

import androidx.appcompat.app.AppCompatActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast

import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import xyz.hasnat.sweettoast.SweetToast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.facebook.accountkit.*
import kotlinx.android.synthetic.main.activity_welcome.*


class MainActivity : AppCompatActivity() {
    companion object {
        var APP_REQUEST_CODE = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //getHashedKey()
    }

    private fun getHashedKey() {
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                @SuppressLint("WrongConstant") val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signatures = packageInfo.signingInfo.apkContentsSigners
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    val signatureBase64 = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Log.d("* KeyHash ", signatureBase64)
                }
            } else {
                val info = packageManager.getPackageInfo("xyz.hasnat.smslogin", PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d("KeyHash ", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun phoneLogin(view: View) {
        val intent = Intent(this, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,
                AccountKitActivity.ResponseType.TOKEN) // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build())
        startActivityForResult(intent, APP_REQUEST_CODE)
    }

    fun emailLogin(view: View) {
        val intent = Intent(this, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.EMAIL,
                AccountKitActivity.ResponseType.TOKEN) // or .ResponseType.TOKEN // CODE
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build())
        startActivityForResult(intent, APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            val loginResult = data!!.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            if (loginResult!!.error != null){
                SweetToast.error(this, loginResult.error!!.errorType.message)
            } else if(loginResult.wasCancelled()){
                Log.e("tag", "CANCEL")
                //SweetToast.error(this, "Cancel")
            } else{
                if (loginResult.accessToken != null){
                    AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
                        override fun onSuccess(account: Account?) {
                            // call your own api or, check
                            // check user existence registered user from your own server
                            // if user is not registered before, go SetUp Profile activity to finish register
                            // and IF user is registered, go to your HOME activity

                            // for testing purpose... :)
                            //SweetToast.success(this, "Logged In")
                            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }

                        override fun onError(p0: AccountKitError?) {
                        }

                    })


                }

            }
        }
    }

}
