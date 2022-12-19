package aaa.financebets.alpha_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import androidx.appcompat.app.ActionBar
import com.example.alpha_v1.R


class Info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) { actionBar.setDisplayHomeAsUpEnabled(true) }

        val IP_Adresse = intent.getStringExtra("IP_Adresse")
        val user_key = intent.getStringExtra("user_key")
        Get_Last_Active_Time(user_key, IP_Adresse)
    }

    fun Get_Last_Active_Time(user_key:String?, IP_Adresse:String?) {
        val thread = Thread {
            try {
                val URL_Data = URL(IP_Adresse+"/Send_User_Data_to_Client.php?u_key="+user_key).readText()
                val obj = JSONObject(URL_Data)
                val V_Erfolgreich = obj.getInt("Erfolgreich")
                val last_active_maintane = obj.getString("last_active_maintane")
                val last_active_store = obj.getString("last_active_store")
                val la_maintane : TextView = findViewById(R.id.tv_last_active1)
                val la_store : TextView = findViewById(R.id.tv_last_active2)

                if (V_Erfolgreich==1) {
                    this@Info.runOnUiThread(java.lang.Runnable {
                        la_maintane.text = last_active_maintane
                        la_store.text = last_active_store
                    })
                } else {
                    this@Info.runOnUiThread(java.lang.Runnable {
                        la_maintane.text = "Err"
                        la_maintane.text = "Err"
                    })
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

