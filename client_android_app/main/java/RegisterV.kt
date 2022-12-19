package aaa.financebets.alpha_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.example.alpha_v1.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.net.URL


class RegisterV : AppCompatActivity() {

    var Success_Firebase = false
    var Firebase_Fertig=false
    var Success_PHPServer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_v)

        val IP_Adresse = intent.getStringExtra("IP_Adresse")

        val btn_register : Button = findViewById(R.id.btn_register)
        val et_register_name : EditText = findViewById(R.id.et_register_name)
        val et_register_email : EditText = findViewById(R.id.et_register_email)
        val et_register_password : EditText = findViewById(R.id.et_register_password)

        val tv_login : TextView = findViewById(R.id.textView7_reg)
        tv_login.setOnClickListener{
            val intent_x = Intent(this, LoginV::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        val tv_forgot : TextView = findViewById(R.id.textView6_reg)
        tv_forgot.setOnClickListener{
            val intent_x = Intent(this, ResetPW::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }

        btn_register.setOnClickListener{
            when {
                TextUtils.isEmpty(et_register_name.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@RegisterV, "Please enter username.", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(et_register_email.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@RegisterV, "Please enter email.", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(et_register_password.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(this@RegisterV, "Please enter password.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val name:String = et_register_name.text.toString().trim{it <= ' '}
                    if (Enthält_Umlaute_oder_zeichen(name)==true) {
                        Toast.makeText(this@RegisterV, "Umlaute oder Sonderzeichen für Benutzternamen verboten. Nur Buchstaben, Unterstrich und Zahlen sind erlaubt.", Toast.LENGTH_SHORT).show()
                    } else {

                        val email:String = et_register_email.text.toString().trim{it <= ' '}
                        val password:String = et_register_password.text.toString().trim{it <= ' '}

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@RegisterV, "Firebase Registratrion Succesfull.", Toast.LENGTH_SHORT).show()
                                    Register_at_PHP_Server(IP_Adresse, FirebaseAuth.getInstance().currentUser!!.uid.toLowerCase(), name, FirebaseAuth.getInstance().currentUser!!.email)
                                    Success_Firebase = true

                                } else {
                                    Toast.makeText(this@RegisterV, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                                Firebase_Fertig=true
                        })
                    }
                }
            }
        }

    }


    fun Enthält_Umlaute_oder_zeichen(name:String) : Boolean {
        val Str_1="adrian"
        val Str_2=arrayOf("o","o","o","o","i","o")
        val Str_3="adrian"
        if (Str_1 == Str_3) {
            println("o")
        }
        if (Str_1[3].toString()==Str_2[4].toString()) {
            println("u")
        }
        for (i in 0 until Str_1.length step 1) {
            for (x in 0 until Str_2.size step 1) {
                if (Str_1[i].equals(Str_2[x])) {
                    println("g")
                }
            }
        }



        val Arr_Verboten = arrayOf("ä","ö","ü","!","§","$","%","&","/","(",")","=","?","+","#",".",":",",",";",">","<","-","'","Ä","Ö","Ü","@","*")
        for (i in 0 until name.length step 1) {
            for (x in 0 until Arr_Verboten.size step 1) {
                if (name[i].toString()==Arr_Verboten[x]) {
                    return true
                }
            }
        }
        return false
    }

    fun Register_at_PHP_Server(IP_Adresse:String?, user_key:String, name:String, email:String?) {  //User_KEy und Name wird auf PHP Server registriert, damit sich zukünftig nutzter registrieren kann
        val thread = Thread {
            val db = Firebase.firestore
            var pw_data_Str = "99"
            db.collection("PW").get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id=="Passwort") {    //PHP Server mit registration und PW wird aufgerufen
                        pw_data_Str = document.data.toString().substring(4, document.data.toString().length-1)
                        break
                    }
                }
            }.addOnFailureListener { exception ->
                    println("RESULTTT: "+"Error getting documents."+ exception)
            }

            Thread.sleep(2000)
            var Erfolgreich=0
            if (pw_data_Str!= "99") {
                try {
                    val URL_Data = URL(IP_Adresse + "/Regxxxxxx.php?u_key=" + user_key + "&name=" + name + "&pw=" + pw_data_Str).readText()
                    val obj = JSONObject(URL_Data)
                    Erfolgreich = obj.getInt("Erfolgreich")
                } catch (e:Exception) {
                    println("_")
                    println("Try-Catch-Error:")
                    e.printStackTrace()
                    println("_")
                }
            }

            if (Erfolgreich==1){
                this@RegisterV.runOnUiThread(java.lang.Runnable {
                    Toast.makeText(this@RegisterV, "Server Registration Succesfull.", Toast.LENGTH_SHORT).show()
                })
                val intent_x = Intent(this@RegisterV, MainActivity::class.java)
                intent_x.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent_x.putExtra("email", email)
                intent_x.putExtra("IP_Adresse", IP_Adresse)
                startActivity(intent_x)
                finish()
            } else {
                this@RegisterV.runOnUiThread(java.lang.Runnable {
                    Toast.makeText(this@RegisterV, "Server Registration ERROR. Firebase reset.", Toast.LENGTH_SHORT).show()
                })
                var user = FirebaseAuth.getInstance().currentUser!!
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("User account deleted")
                    }
                }
            }
        }
        thread.start()
    }
}