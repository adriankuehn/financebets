package aaa.financebets.alpha_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.example.alpha_v1.R
import org.json.JSONObject
import java.net.URL

class Zuruecksetzten : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zuruecksetzten)

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) { actionBar.setDisplayHomeAsUpEnabled(true) }

        var IP_Adresse = intent.getStringExtra("IP_Adresse")
        var user_key = intent.getStringExtra("user_key")

        val Bu_Zurücks : Button = findViewById(R.id.button_zurück)
        Bu_Zurücks.setOnClickListener {

            val thread = Thread {
                try {
                    val tv_result: TextView = findViewById(R.id.textView_zurücks)
                    val URL_Data = URL(IP_Adresse+"/Res***************.php?u_key="+user_key).readText()
                    val obj = JSONObject(URL_Data)
                    val V_Erfolgreich = obj.getInt("Erfolgreich")
                    if (V_Erfolgreich==1) {
                        tv_result.text = "Konto Erfolgreich zurückgesetzt!"
                    } else {
                        tv_result.text = "Konto konnte nicht zurückgesetzt werden!"
                    }
                } catch (e:Exception) {
                    println("_")
                    println("Try-Catch-Error:")
                    e.printStackTrace()
                    println("_")
                }
            }
            thread.start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}