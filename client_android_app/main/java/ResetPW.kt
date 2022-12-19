package aaa.financebets.alpha_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.alpha_v1.R
import com.google.firebase.auth.FirebaseAuth

class ResetPW : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pw)

        val IP_Adresse = intent.getStringExtra("IP_Adresse")

        val tv_login : TextView = findViewById(R.id.textView6_res)
        tv_login.setOnClickListener{
            val intent_x = Intent(this, LoginV::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        val tv_register : TextView = findViewById(R.id.textView7_res)
        tv_register.setOnClickListener{
            val intent_x = Intent(this, RegisterV::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        val et_reset_email : EditText = findViewById(R.id.et_reset_email)
        val btn_reset : Button = findViewById(R.id.btn_reset)
        btn_reset.setOnClickListener{
            when {
                TextUtils.isEmpty(et_reset_email.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@ResetPW, "Please enter email.", Toast.LENGTH_SHORT).show()
                }
                else -> {

                    val email:String = et_reset_email.text.toString().trim{it <= ' '}

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@ResetPW, "Password reset successfull. Watch your Emails with Reset-Link", Toast.LENGTH_SHORT).show()
                                val intent_x = Intent(this@ResetPW, LoginV::class.java)
                                intent_x.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent_x.putExtra("IP_Adresse", IP_Adresse)
                                startActivity(intent_x)
                                finish()
                            } else {
                                Toast.makeText(this@ResetPW, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}