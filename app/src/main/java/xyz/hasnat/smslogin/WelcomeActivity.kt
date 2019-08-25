package xyz.hasnat.smslogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.accountkit.Account
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.AccountKitCallback
import com.facebook.accountkit.AccountKitError
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun logOut(view: View) {
        AccountKit.logOut()
        finish()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        AccountKit.getCurrentAccount(object : AccountKitCallback<Account>{
            override fun onSuccess(account: Account?) {
                idTv.text = account!!.id
                phoneTv.text = account.phoneNumber.toString()
                emailTv.text = account.email
            }

            override fun onError(p0: AccountKitError?) {

            }

        })
    }
}
