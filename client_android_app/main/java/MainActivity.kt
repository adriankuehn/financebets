package aaa.financebets.alpha_v1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import org.json.JSONObject
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.example.alpha_v1.R

//cronjobs: https://www.cronjob.de
//Firebase und Firestore für pw: https://console.firebase.google.com
//Strato server Login, Kunden Login und phpmyadmin
//Bei History_User maximal 500 wetten speichern, Maixxxxxx muss älteste Wette löschen wenn neue wette in history eingefügt wird
//wenn user neu registriert wird muss User_Key beim ersten mal von Firebase in MySQL Datenbank gespeichert werden

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val IP_Adresse = intent.getStringExtra("IP_Adresse")
        var user_key = FirebaseAuth.getInstance().getCurrentUser()!!.uid.toLowerCase()
        val email = FirebaseAuth.getInstance().getCurrentUser()!!.email


        Create_Navigation_Drawer(IP_Adresse, user_key, email)
        Create_Bottom_Navigation(IP_Adresse, user_key)
        Test_PHP_Server_Laptop(user_key, IP_Adresse)


        val Bu_Reload : ImageView = findViewById(R.id.imageView22)
        Bu_Reload.setOnClickListener {
            finish();   //recreate is Flacker zu stark
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }

        val viewFlipper : ViewFlipper = findViewById(R.id.myViewFlipper)
        val bu_wettverlauf : Button = findViewById(R.id.button4)
        val bu_bestenliste : Button = findViewById(R.id.button5)
        val view_links : View = findViewById(R.id.divider4_1)
        val view_rechts : View = findViewById(R.id.divider4_2)
        bu_wettverlauf.setOnClickListener {
            viewFlipper.setDisplayedChild(0)
            view_rechts.setBackgroundColor(Color.argb(255, 255, 255, 255))
            view_links.setBackgroundColor(Color.rgb( 85, 175, 255))
        }
        bu_bestenliste.setOnClickListener {
            viewFlipper.setDisplayedChild(1)
            view_links.setBackgroundColor(Color.argb(255, 255, 255, 255))
            view_rechts.setBackgroundColor(Color.rgb(85, 175, 255))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun Create_Navigation_Drawer(IP_Adresse:String?, user_key:String?, email:String?) {
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItem1 -> {
                    val intent_x = Intent(this,  MainActivity::class.java)
                    intent_x.putExtra("IP_Adresse", IP_Adresse)
                    startActivity(intent_x)
                    overridePendingTransition(0, 0)
                }
                R.id.miItem2 -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent_x = Intent(this,  LoginV::class.java)
                    intent_x.putExtra("IP_Adresse", IP_Adresse)
                    startActivity(intent_x)
                    overridePendingTransition(0, 0)
                    Toast.makeText(applicationContext, "Logged out", Toast.LENGTH_SHORT).show()
                    finish()
                }
                R.id.miItem3 -> {
                    val intent_x = Intent(this, Info::class.java)
                    intent_x.putExtra("IP_Adresse", IP_Adresse)
                    intent_x.putExtra("user_key", user_key)
                    startActivity(intent_x)
                    overridePendingTransition(0, 0)
                }
                R.id.miItem4 -> {
                    startActivity(Intent(this, Datenschutz::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.miItem5 -> {
                    startActivity(Intent(this, Lizenzen::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.miItem6 -> {
                    startActivity(Intent(this, Impressum::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.miItem7 -> {
                    val unter_zurueck = Intent(this, Zuruecksetzten::class.java)
                    unter_zurueck.putExtra("IP_Adresse", IP_Adresse)
                    unter_zurueck.putExtra("user_key", user_key)
                    startActivity(unter_zurueck )
                    overridePendingTransition(0, 0)
                }
            }
            true
        }
        val navigationView2 = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView2.getHeaderView(0)
        val navEmail= headerView.findViewById<View>(R.id.tv_header_email) as TextView
        navEmail.text = email
    }

    fun Create_Bottom_Navigation(IP_Adresse:String?, user_key:String?) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.home
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
                R.id.home -> return@OnNavigationItemSelectedListener true
                R.id.about -> {
                    val unter_m_wetten = Intent(this, Meine_Wetten::class.java)
                    unter_m_wetten.putExtra("IP_Adresse", IP_Adresse)
                    unter_m_wetten.putExtra("user_key", user_key)
                    startActivity(unter_m_wetten)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }


    fun Test_PHP_Server_Laptop(user_key:String?, IP_Adresse:String?) {
        /*
        Necessary Permissions to add in AndroidManifest.xml
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
        android:usesCleartextTraffic="true"
        */

        val thread = Thread {
            try {
                val URL_Data = URL(IP_Adresse+"/Send_User_Data_to_Client.php?u_key="+user_key).readText()
                val obj = JSONObject(URL_Data)

                val V_Erfolgreich = obj.getInt("Erfolgreich")
                val V_user_name = obj.getString("user_name")
                val V_coins = obj.getInt("coins")
                val V_anz_wetten = obj.getInt("anz_wetten")
                val bestenl_name = obj.getJSONArray("bestenl_name")
                val bestenl_coins = obj.getJSONArray("bestenl_coins")
                val wettverlauf = obj.getJSONArray("wettverlauf")
                val Coins_Verlauf_200_PHP = obj.getJSONArray("coinsverlauf")
                val Platz = obj.getInt("platz")
                val Coins_Verlauf_200: ArrayList<Int> = ArrayList()

                for (aa in 0 until Coins_Verlauf_200_PHP.length() step 1) {
                    if (aa==V_anz_wetten) {
                        break
                    }
                    Coins_Verlauf_200.add(Coins_Verlauf_200_PHP.getInt(aa))
                }
                Coins_Verlauf_200.reverse()

                val tv_Userdata : TextView = findViewById(R.id.tv_willkommen)
                val tv_Platz : TextView = findViewById(R.id.tv_dein_platz)
                if (V_Erfolgreich==1) {
                    val navigationView2 = findViewById<View>(R.id.nav_view) as NavigationView
                    val headerView = navigationView2.getHeaderView(0)
                    val navName= headerView.findViewById<View>(R.id.tv_header_name) as TextView
                    var Array_Wettverlauf = Convert_Arrays_1(wettverlauf)
                    var Array_Securitynames = Convert_Arrays_2(wettverlauf)

                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        tv_Platz.text = "  Dein Platz: "+Platz+"  "
                        tv_Userdata.text = "Coins:  "+V_coins+"\nWetten:  "+V_anz_wetten
                        navName.text = V_user_name
                        Display_Wettverlauf(Array_Wettverlauf, Array_Securitynames)
                        Display_Bestenliste(bestenl_name, bestenl_coins)
                        Create_Diagramm_Coinsverlauf(Coins_Verlauf_200, V_anz_wetten)
                    })
                } else {
                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        tv_Userdata.text = "Daten konnten nicht geladen werden"
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

    fun Create_Diagramm_Coinsverlauf(Coins_Verlauf_200:ArrayList<Int>, V_anz_wetten:Int) {  //letzten 200 wetten werden dargestellt

        val dataPoints1 = arrayOfNulls<DataPoint>(Coins_Verlauf_200.size)
        for (i in 0 until Coins_Verlauf_200.size) {
            if (V_anz_wetten>199) {
                dataPoints1[i] = DataPoint(V_anz_wetten-199 + i.toDouble(), Coins_Verlauf_200[i].toDouble())
            } else {
                dataPoints1[i] = DataPoint(i.toDouble()+1, Coins_Verlauf_200[i].toDouble())
            }
        }

        var series1 = LineGraphSeries(dataPoints1)
        series1.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series1.setDrawBackground(true);
        var graph1 = findViewById<View>(R.id.graph_home) as GraphView
        graph1.addSeries(series1)
        graph1.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph1.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph1.getGridLabelRenderer().setNumVerticalLabels(4);
        graph1.setTitleTextSize(60f)
        graph1.viewport.isXAxisBoundsManual = true
        graph1.viewport.isScalable = true
    }

    fun Convert_Arrays_1(wettverlauf: JSONArray): Array<Array<Double>> {
        var Array_Wettverlauf = Array(30) {Array(6) {0.0} }
        for (i in 0 until wettverlauf.length() step 1) {
            Array_Wettverlauf[i][0]=wettverlauf.getJSONArray(i).getString(1).toDouble()
            Array_Wettverlauf[i][1]=wettverlauf.getJSONArray(i).getString(3).toDouble()
            val Long_Short = wettverlauf.getJSONArray(i).getString(5).toDouble()
            Array_Wettverlauf[i][2]=Long_Short
            Array_Wettverlauf[i][3]=wettverlauf.getJSONArray(i).getString(2).toDouble()
            Array_Wettverlauf[i][4]=wettverlauf.getJSONArray(i).getString(4).toDouble()
            val Stock_Move=wettverlauf.getJSONArray(i).getString(6).toDouble()
            var Betrag = wettverlauf.getJSONArray(i).getString(7).toDouble()
            if (Stock_Move==2.0) {
                Betrag=0.0
            }
            else if (Stock_Move==Long_Short) {
                //pass
            } else if (Stock_Move!=Long_Short) {
                Betrag = -Betrag
            }
            Array_Wettverlauf[i][5]=Betrag
        }
        return(Array_Wettverlauf)
    }

    fun Convert_Arrays_2(wettverlauf: JSONArray): Array<String> {
        var Array_Securitynames = Array(30) {""}
        for (i in 0 until wettverlauf.length() step 1) {
            Array_Securitynames[i]=wettverlauf.getJSONArray(i).getString(0)
        }
        return(Array_Securitynames)
    }





    fun wandelDateInTimeFormate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    @SuppressLint("SetTextI18n")
    fun Display_Wettverlauf(Array_Wettverlauf:Array<Array<Double>>, Array_Securitynames:Array<String>) {
        // Syntax: {Unix_Tstamp_beginn, Tstamp_ende, S=0/L=1, Start_P, End_P, Gewinn/Verlust}
        /*val Array_Wettverlauf = arrayOf(doubleArrayOf(1627725916.0, 1627725976.0, 1.0, 1.1874, 1.1875, -2000.0),
            doubleArrayOf(1627726916.0, 1627726976.0, 0.0, 1.1884, 1.1881, -2000.0))*/

        val layout_first : LinearLayout = findViewById(R.id.firstLayout)
        val Arr_LongShort = arrayOf<String>("Short","Long ")
        val Securities_Groß =  arrayOf<String>("EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CHF", "BTC/EUR")
        val Securities_Klein = arrayOf<String>("eur_usd", "usd_jpy", "gbp_usd", "aud_usd", "usd_chf", "btc_eur")

        for (i in 0 until Array_Wettverlauf.size step 1) {
            if (Array_Wettverlauf[i][0]==0.0) {
                break
            }

            val tv_dynamic = TextView(this)
            tv_dynamic.setTypeface(Typeface.MONOSPACE);
            tv_dynamic.setTextScaleX(0.8f);
            tv_dynamic.textSize = 17f

            var security_name = ""
            for (x in 0 until Securities_Groß.size step 1) {
                if (Array_Securitynames[i]==Securities_Klein[x]) {
                    security_name=Securities_Groß[x]
                    break
                }
            }
            val Datum = wandelDateInTimeFormate((Array_Wettverlauf[i][0]*1000).toLong(), "dd.MM.yy")
            val Zeit1 = wandelDateInTimeFormate((Array_Wettverlauf[i][0]*1000).toLong(), "HH:mm:ss")
            val Zeit2 = wandelDateInTimeFormate((Array_Wettverlauf[i][1]*1000).toLong(), "HH:mm:ss")
            var Gewi_Verl : String
            var Kursentwicklung = Array_Wettverlauf[i][3].toString()+"=>"+Array_Wettverlauf[i][4].toString()
            var Auffüllung = " ".repeat(21-Kursentwicklung.length)
            if (Array_Wettverlauf[i][5]>=0) {
                Gewi_Verl = Auffüllung+"Gewinn:  "+ abs(Array_Wettverlauf[i][5].toInt())
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_green))
            } else {
                Gewi_Verl = Auffüllung+"Verlust: "+abs(Array_Wettverlauf[i][5].toInt())
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_red))
            }
            if (security_name=="BTC/EUR") {
                tv_dynamic.text = " " + "BTC/USD" + "  " + Zeit1 + "=>" + Zeit2 + "   " + Datum + "\n" +
                            " " + Arr_LongShort[Array_Wettverlauf[i][2].toInt()] + "    " + Kursentwicklung + Gewi_Verl + "\n"
            } else {
                tv_dynamic.text = " " + security_name + "  " + Zeit1 + "=>" + Zeit2 + "   " + Datum + "\n" +
                            " " + Arr_LongShort[Array_Wettverlauf[i][2].toInt()] + "    " + Kursentwicklung + Gewi_Verl + "\n"
            }

            layout_first.addView(tv_dynamic)
        }

        val tv_dynamic_end = TextView(this)
        tv_dynamic_end.text = ""
        tv_dynamic_end.setHeight(162)
        layout_first.addView(tv_dynamic_end)
    }

    fun Display_Bestenliste(bestenl_name: JSONArray, bestenl_coins: JSONArray) {
        val layout_second : LinearLayout = findViewById(R.id.secondLayout)

        for (i in 0 until bestenl_name.length() step 1) {
            val tv_dynamic = TextView(this)
            tv_dynamic.textSize = 20f
            if (i==0) {
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_gold))
            } else if (i==1) {
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_silver))
            } else if (i==2) {
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_bronze))
            } else {
                tv_dynamic.setBackground(getResources().getDrawable(R.drawable.round_normal_white))
            }
            tv_dynamic.text = "  "+(i+1).toString()+".)    "+bestenl_name.getString(i)+"     "+bestenl_coins.getString(i)+"\n"
            layout_second.addView(tv_dynamic)
        }
        val tv_dynamic_end = TextView(this)
        tv_dynamic_end.text = ""
        tv_dynamic_end.setHeight(162)
        layout_second.addView(tv_dynamic_end)
    }
}