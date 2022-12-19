package aaa.financebets.alpha_v1

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.alpha_v1.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.net.URL

//Fragt anfangs Array von PHP Server mit aktiven Wetten an, Updaten dann bis Progress Bar fertig ist immer den aktuellen Preis
//Färbt TextView Rot oder Grün je nach dem ob in Gewinn- oder Verlustzone
//Wenn ProgressBar fertig, dann Wette vom Bildschirm gelöscht, kann in anderer Aktivity  unter Wetthistorie nachgesehen werden
//Wenn WaährungsIDs und Namen geändert werden, muss dies im PHP Skript Send_ActiveBets ebenfalls geändert werden

class Meine_Wetten : AppCompatActivity() {
    val Arr_Waehrungen = arrayOf<String>("EUR/USD","USD/JPY","GBP/USD","AUD/USD","USD/CHF", "BTC/EUR")  //0-5
    val Arr_Waehrungen_PHP = arrayOf<String>("eur_usd","usd_jpy","gbp_usd","aud_usd","usd_chf", "btc_eur")
    val Arr_LongShort = arrayOf<String>("Short","Long")
    var Max_Anz_Progessbars = 50
    val textViewArray = arrayOfNulls<TextView>(Max_Anz_Progessbars)
    val ProrgessBArray = arrayOfNulls<ProgressBar>(Max_Anz_Progessbars)
    var Download_Fertig = false
    val AcvtiveBets_Main: ArrayList<ArrayList<Double>> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meine_wetten)

        var IP_Adresse = intent.getStringExtra("IP_Adresse")
        var user_key = intent.getStringExtra("user_key")

        Create_Bottom_Navigation(IP_Adresse, user_key)

        Get_Array_from_Server_and_Create_Views(IP_Adresse, user_key)

        val thread = Thread{
            try {
                while (Download_Fertig == false) {
                    //pass, extrem wichtig da sonst error
                }
                if (AcvtiveBets_Main.size!=0) {
                    while (true) {

                        val Arr_Akt_Waehrungen_ID = find_active_waehrungen()
                        var Arr_Akt_Waehrungen_Werte = DoubleArray(Arr_Akt_Waehrungen_ID.size)
                        for (i in 0 until Arr_Akt_Waehrungen_ID.size step 1) {
                            Arr_Akt_Waehrungen_Werte[i] = Get_Current_Price(Arr_Akt_Waehrungen_ID[i], IP_Adresse, user_key)
                        }
                        runOnUiThread { update_textviews(IP_Adresse, user_key, Arr_Akt_Waehrungen_Werte, Arr_Akt_Waehrungen_ID) }
                        Thread.sleep(1000)
                    }
                } else {
                    runOnUiThread { Create_Text_Keine_aktiven_Wetten()}
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

    fun Create_Text_Keine_aktiven_Wetten() {
        val layout_activebets : LinearLayout = findViewById(R.id.layout_activebets)
        val tv_dynamic = TextView(this)
        tv_dynamic.textSize = 17f
        tv_dynamic.text = "Zur Zeit sind keine Aktiven Wetten ausstehend:)"
        layout_activebets.addView(tv_dynamic)
    }

    fun Create_Bottom_Navigation(IP_Adresse:String?, user_key:String?) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.about
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboard -> {
                    val unter_charts = Intent(this, Charts_Activity::class.java)
                    unter_charts.putExtra("IP_Adresse", IP_Adresse)
                    unter_charts.putExtra("user_key", user_key)
                    startActivity(unter_charts)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    val unter_home = Intent(this, MainActivity::class.java)
                    unter_home.putExtra("IP_Adresse", IP_Adresse)
                    startActivity(unter_home)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }


    fun update_textviews(IP_Adresse:String?, user_key:String?, Arr_Akt_Waehrungen_Werte:DoubleArray, Arr_Akt_Waehrungen_ID:MutableList<Int>) {

        for (a in 0 until AcvtiveBets_Main.size step 1) {
            val Str_waehrung = Arr_Waehrungen[AcvtiveBets_Main[a][0].toInt()]
            val Str_LS = Arr_LongShort[AcvtiveBets_Main[a][1].toInt()]
            var aktive_price = Arr_Akt_Waehrungen_Werte[Arr_Akt_Waehrungen_ID.indexOf(AcvtiveBets_Main[a][0].toInt())]

            var aktive_price_Str : String
            if (AcvtiveBets_Main[a][6].toInt()<0) {
                aktive_price_Str = "Error"
            } else {
                aktive_price_Str = aktive_price.toString()
            }
            if (Str_waehrung=="BTC/EUR") {
                textViewArray[a]!!.text =
                    " " + "BTC/USD" + "    " + Str_LS + "    " + AcvtiveBets_Main[a][2].toInt() + "    " +
                            AcvtiveBets_Main[a][3].toInt() + "s    \n" + " Start: " + AcvtiveBets_Main[a][4] +
                            "      Aktuell: " + aktive_price_Str
            } else {
                textViewArray[a]!!.text =
                    " " + Str_waehrung + "    " + Str_LS + "    " + AcvtiveBets_Main[a][2].toInt() + "    " +
                            AcvtiveBets_Main[a][3].toInt() + "s    \n" + " Start: " + AcvtiveBets_Main[a][4] +
                            "      Aktuell: " + aktive_price_Str
            }

            if (ProrgessBArray[a]!!.progress >= AcvtiveBets_Main[a][3]) {
                textViewArray[a]!!.setBackgroundColor(Color.parseColor("#ECECEC"))
                textViewArray[a]!!.setBackground(getResources().getDrawable(R.drawable.round_normal_gray))
            } else {
                if (AcvtiveBets_Main[a][4] <= aktive_price && AcvtiveBets_Main[a][1] == 1.0) {
                    textViewArray[a]!!.setBackground(getResources().getDrawable(R.drawable.round_normal_green))
                } else if (AcvtiveBets_Main[a][4] > aktive_price && AcvtiveBets_Main[a][1] == 1.0) {
                    textViewArray[a]!!.setBackground(getResources().getDrawable(R.drawable.round_normal_red))
                } else if (AcvtiveBets_Main[a][4] <= aktive_price && AcvtiveBets_Main[a][1] == 0.0) {
                    textViewArray[a]!!.setBackground(getResources().getDrawable(R.drawable.round_normal_red))
                } else if (AcvtiveBets_Main[a][4] > aktive_price && AcvtiveBets_Main[a][1] == 0.0) {
                    textViewArray[a]!!.setBackground(getResources().getDrawable(R.drawable.round_normal_green))
                }
            }
        }
    }


    fun find_active_waehrungen() : MutableList<Int> {
        var Akt_Waehrungen = mutableListOf<Int>()
        for  (i in 0 until AcvtiveBets_Main.size step 1) {
            if (Akt_Waehrungen.contains(AcvtiveBets_Main[i][0].toInt())==false) {
                Akt_Waehrungen.add(AcvtiveBets_Main[i][0].toInt())
            }
        }
        return Akt_Waehrungen
    }

    fun Get_Array_from_Server_and_Create_Views(IP_Adresse:String?, user_key:String?){
        // Format: {Währ_ID, L/S, Betrag, Dauer, Startpreis, Aktuellerpreis, verbleibende Zeit}
        //var array = arrayOf(doubleArrayOf(5.0, 1.0, 2000.0, 120.0, 1.1874, 1.1875, 60.0), doubleArrayOf(2.0, 0.0, 1000.0, 500.0, 1.1221, 1.1222, 120.0))
        val thread = Thread{
            try {
                val URL_Data = URL(IP_Adresse+"/Send_ActiveBets_to_Client.php?u_key="+user_key).readText()
                val obj = JSONObject(URL_Data)
                var Arr_ActiveBets_JSON = obj.getJSONArray("arr_activebets")
                for (i in 0 until Arr_ActiveBets_JSON.length() step 1) {
                    val List_Zwi: ArrayList<Double> = ArrayList<Double>(7)
                    for (x in 0 until 7  step 1) {
                        List_Zwi.add(Arr_ActiveBets_JSON.getJSONArray(i).getDouble(x))
                    }
                    AcvtiveBets_Main.add(List_Zwi)
                }
                this@Meine_Wetten.runOnUiThread(java.lang.Runnable {
                    Create_Views()
                    Download_Fertig=true
                })
            } catch (e:Exception) {
                println("_")
                println("Try-Catch-Error:")
                e.printStackTrace()
                println("_")
            }
        }
        thread.start()
    }

    fun Create_Views() {
        val layout_activebets : LinearLayout = findViewById(R.id.layout_activebets)
        for  (i in 0 until AcvtiveBets_Main.size step 1) {
            textViewArray[i] = TextView(this)
            textViewArray[i]!!.textSize = 21f
            val Str_waehrung = Arr_Waehrungen[AcvtiveBets_Main[i][0].toInt()]
            val Str_LS = Arr_LongShort[AcvtiveBets_Main[i][1].toInt()]
            if (Str_waehrung=="BTC/EUR") {
                textViewArray[i]!!.text =
                    " " + "BTC/USD" + "    " + Str_LS + "    " + AcvtiveBets_Main[i][2].toInt() + "    " + AcvtiveBets_Main[i][3].toInt() +
                            "s    \n" + " Start: " + AcvtiveBets_Main[i][4] + "      Aktuell: " + AcvtiveBets_Main[i][5]
            } else {
                textViewArray[i]!!.text =
                    " " + Str_waehrung + "    " + Str_LS + "    " + AcvtiveBets_Main[i][2].toInt() + "    " + AcvtiveBets_Main[i][3].toInt() +
                            "s    \n" + " Start: " + AcvtiveBets_Main[i][4] + "      Aktuell: " + AcvtiveBets_Main[i][5]
            }

            layout_activebets.addView(textViewArray[i])

            ProrgessBArray[i] = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            ProrgessBArray[i]!!.progressDrawable.setColorFilter(Color.parseColor("#9F0091EA"), android.graphics.PorterDuff.Mode.SRC_IN)
            layout_activebets.addView(ProrgessBArray[i])
            val Anzahl_Sek_Ges = AcvtiveBets_Main[i][3].toInt()
            ProrgessBArray[i]!!.max = Anzahl_Sek_Ges
            if (AcvtiveBets_Main[i][6].toInt()<0) {
                ProrgessBArray[i]!!.progress = Anzahl_Sek_Ges  //Fehler, PHP Server Maintain Skript konnte bestimmt nicht richtig löschen, negative verbleibende Zeit
                ObjectAnimator.ofInt(ProrgessBArray[i], "progress", Anzahl_Sek_Ges).setDuration((0*1000).toLong()).start()
            }else {
                ProrgessBArray[i]!!.progress = Anzahl_Sek_Ges - AcvtiveBets_Main[i][6].toInt()
                ObjectAnimator.ofInt(ProrgessBArray[i], "progress", Anzahl_Sek_Ges).setDuration((AcvtiveBets_Main[i][6].toInt()*1000).toLong()).start()
            }


            val space_dynamic = Space(this)
            space_dynamic.minimumHeight=40
            layout_activebets.addView(space_dynamic)
        }
    }

    fun Get_Current_Price(Waehr_ID : Int, IP_Adresse:String?, user_key:String?) : Double{
        val URL_Data = URL(IP_Adresse + "/Send_Price_to_Client.php?u_key=" + user_key + "&sec_key=" + Arr_Waehrungen_PHP[Waehr_ID]+"&verlauf_100=0").readText()
        val obj = JSONObject(URL_Data)
        print("Price: "+obj.getDouble("Price"))
        return obj.getDouble("Price")
    }

}