package aaa.financebets.alpha_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.widget.Spinner
import com.example.alpha_v1.R
import org.json.JSONObject
import java.net.URL

class trading_activity : AppCompatActivity() {

    var price_main : Double = 0.0
    var time_main : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trading)
        supportActionBar?.hide()

        val waehrung = intent.getIntExtra("waehrung", 0)
        val long_short = intent.getIntExtra("long_short", 0)
        val user_key = intent.getStringExtra("user_key")
        val IP_Adresse = intent.getStringExtra("IP_Adresse")
        val Arr_Waehrungen = arrayOf<String>("EUR/USD","USD/JPY","GBP/USD","AUD/USD","USD/CHF", "BTC/EUR")
        val Arr_Waehrungen_PHP = arrayOf<String>("eur_usd","usd_jpy","gbp_usd","aud_usd","usd_chf", "btc_eur")
        val PHP_waehrung = Arr_Waehrungen_PHP[waehrung]
        val Str_waehrung = Arr_Waehrungen[waehrung]
        val tv_Waehrung : TextView = findViewById(R.id.textVieww01)
        if (Str_waehrung=="BTC/EUR")
            tv_Waehrung.text = "      "+"BTC/USD"
        else {
            tv_Waehrung.text = "      "+Str_waehrung
        }

        val thread = Thread{
            try {
                Update_Price_from_PHP_Server(PHP_waehrung, user_key,  IP_Adresse)
            } catch (e:Exception) {
                println("_")
                println("Try-Catch-Error:")
                e.printStackTrace()
                println("_")
            }
        }
        thread.start()

        val radio1 : RadioButton = findViewById(R.id.rb_male)
        val radio2 : RadioButton = findViewById(R.id.rb_female)
        if (long_short==0) {
            radio1.setChecked(true)
            radio2.setChecked(false)
        } else if (long_short==1) {
            radio1.setChecked(false)
            radio2.setChecked(true)
        }


        val Bu_fertig : Button = findViewById(R.id.button_End_2)
        Bu_fertig.setOnClickListener {
            val spinn_1 : Spinner = findViewById(R.id.spinner1)
            val spinn_2 : Spinner = findViewById(R.id.spinner2)
            var dauer = spinn_2.selectedItem.toString().substring(0, spinn_2.selectedItem.toString().length-1).toInt()
            var l_s = " "
            var L_S = " "
            var einsatz = spinn_1.selectedItem.toString()
            if (radio1.isChecked()) {
                l_s="0"
                L_S="Short"
            } else {
                l_s="1"
                L_S="Long"
            }
            val thread = Thread{
                try {
                    if (time_main!=0) { //Sonst wird fehlerhafte wette ausgeführt welche dauerhaft in aktive wetten bleibt
                        Start_Bet_PHP_Server(IP_Adresse, user_key, PHP_waehrung, dauer, l_s, einsatz,  Str_waehrung, L_S)
                    }
                } catch (e:Exception) {
                    println("_")
                    println("Try-Catch-Error:")
                    e.printStackTrace()
                    println("_")
                }
            }
            thread.start()
            finish()
        }

        val Bu_cancel : Button = findViewById(R.id.button_End_1)
        Bu_cancel.setOnClickListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun Start_Bet_PHP_Server(IP_Adresse:String?, user_key:String?, PHP_waehrung:String?, dauer:Int, l_s:String, einsatz:String, Str_waehrung:String, L_S:String) {
        val URL_Data = URL(IP_Adresse + "/Start_Bet.php?u_key=" + user_key + "&sec_key=" + PHP_waehrung + "&start_time="
                +time_main.toString()+"&start_price="+price_main.toString()+"&end_time="+(time_main+dauer).toString()+"&l_s="+l_s+"&einsatz="+einsatz).readText()

        val obj = JSONObject(URL_Data)
        this@trading_activity.runOnUiThread(java.lang.Runnable {
            if (obj.getInt("Erfolgreich")==1) {
                if (Str_waehrung=="BTC/EUR") {
                    Toast.makeText(this, "Erfolgreich: "+"BTC/USD" + ", "+L_S+", " + einsatz + ", " + dauer, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erfolgreich: "+Str_waehrung + ", "+L_S+", " + einsatz + ", " + dauer, Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, "Error: Wette nicht gestartet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun Update_Price_from_PHP_Server(waehrung:String, user_key: String?, IP_Adresse:String?) {
        val tv_Price : TextView = findViewById(R.id.textVieww02)
        while (true) {
            val URL_Data = URL(IP_Adresse + "/Send_Price_to_Client.php?u_key=" + user_key + "&sec_key=" + waehrung+"&verlauf_100=0").readText()
            val obj = JSONObject(URL_Data)
            price_main = obj.getDouble("Price")
            time_main = obj.getInt("Timestamp")
            var V_Erfolgreich = obj.getInt("Erfolgreich")
            if (V_Erfolgreich==1) {
                this@trading_activity.runOnUiThread(java.lang.Runnable {
                    tv_Price.text = price_main.toString()
                })
            } else {
                tv_Price.text = "Err"
            }

            Thread.sleep(1000)
        }
    }


}