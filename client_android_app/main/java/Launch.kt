package aaa.financebets.alpha_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.alpha_v1.R
import com.google.firebase.auth.FirebaseAuth


class Launch : AppCompatActivity() {

    val IP_Adresse = "https://financebets.de"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        if (FirebaseAuth.getInstance().currentUser!=null) {
            val intent_x = Intent(this, MainActivity::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            intent_x.putExtra("email", FirebaseAuth.getInstance().currentUser!!.email)
            startActivity(intent_x)
        } else {
            val intent_x = Intent(this, LoginV::class.java)
            intent_x.putExtra("IP_Adresse", IP_Adresse)
            startActivity(intent_x)
        }
    }
}