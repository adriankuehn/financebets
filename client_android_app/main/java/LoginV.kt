package aaa.financebets.alpha_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.alpha_v1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginV : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_v)


        val IP_Adresse = intent.getStringExtra("IP_Adresse")

        val tv_forgot : TextView = findViewById(R.id.textView6_log)
        tv_forgot.setOnClickListener{
            val intent_x = Intent(this, ResetPW::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        val tv_register : TextView = findViewById(R.id.textView7_log)
        tv_register.setOnClickListener{
            val intent_x = Intent(this, RegisterV::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        val btn_login : TextView = findViewById(R.id.btn_login)
        val et_login_email : EditText = findViewById(R.id.et_login_email)
        val et_login_password : EditText = findViewById(R.id.et_login_password)
        btn_login.setOnClickListener{
            when {
                TextUtils.isEmpty(et_login_email.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@LoginV, "Please enter email.", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(et_login_password.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@LoginV, "Please enter password.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val email:String = et_login_email.text.toString().trim{it <= ' '}
                    val password:String = et_login_password.text.toString().trim{it <= ' '}

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(this@LoginV, "Yue are logged in successfully.", Toast.LENGTH_SHORT).show()
                                val intent_x = Intent(this@LoginV, MainActivity::class.java)
                                intent_x.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent_x.putExtra("email", email)
                                intent_x.putExtra("IP_Adresse", IP_Adresse.toString())
                                startActivity(intent_x)
                                finish()
                            } else {
                                Toast.makeText(this@LoginV, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }


            }
        }
    }
}