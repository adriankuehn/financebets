package aaa.financebets.alpha_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.View
import com.example.alpha_v1.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONObject
import java.net.URL

class Charts_Activity : AppCompatActivity() {

    val mHandler = Handler()
    var ZZZ = 99.0
    var Zeitspanne = 30.0   //Diese Zeitspanne wird immer von der Währung dargestellt
    var Arr_price_main : Array<Double> = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    var Arr_time_main : Array<String> = arrayOf(" ", " ", " ", " ", " ", " ")
    var Download_Fertig = false
    val Arr_Waehrungen_PHP = arrayOf<String>("eur_usd","usd_jpy","gbp_usd","aud_usd","usd_chf", "btc_eur")
    var Waehr_Verlauf_100 = Array(6) {Array(100) {0.0} }

    var series1: LineGraphSeries<DataPoint>? = null
    lateinit var graph1 : GraphView
    var series2: LineGraphSeries<DataPoint>? = null
    lateinit var graph2 : GraphView
    var series3: LineGraphSeries<DataPoint>? = null
    lateinit var graph3 : GraphView
    var series4: LineGraphSeries<DataPoint>? = null
    lateinit var graph4 : GraphView
    var series5: LineGraphSeries<DataPoint>? = null
    lateinit var graph5 : GraphView
    var series6: LineGraphSeries<DataPoint>? = null
    lateinit var graph6 : GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        val IP_Adresse = intent.getStringExtra("IP_Adresse")
        val user_key = intent.getStringExtra("user_key")
        val unter_trading = Intent(this, trading_activity::class.java)
        unter_trading.putExtra("user_key", user_key)
        unter_trading.putExtra("IP_Adresse", IP_Adresse)

        Create_Bottom_Navigation(IP_Adresse, user_key)


        val thread1 = Thread{
            try {
                Get_100PVerlauf_from_Server(user_key, IP_Adresse)
                while (Download_Fertig==false) {
                    //pass
                }
                this@Charts_Activity.runOnUiThread(java.lang.Runnable {
                    Create_Diagramms (unter_trading)
                })
            } catch (e:Exception) {
                println("_")
                println("Try-Catch-Error:")
                e.printStackTrace()
                println("_")
            }
        }
        thread1.start()

        val thread2 = Thread{
            try {
                while (true) {
                    Update_Price_from_PHP_Server("eur_usd", 0, user_key, IP_Adresse)
                    Update_Price_from_PHP_Server("usd_jpy", 1, user_key, IP_Adresse)
                    Update_Price_from_PHP_Server("gbp_usd", 2, user_key, IP_Adresse)
                    Update_Price_from_PHP_Server("aud_usd", 3, user_key, IP_Adresse)
                    Update_Price_from_PHP_Server("usd_chf", 4, user_key, IP_Adresse)
                    Update_Price_from_PHP_Server("btc_eur", 5, user_key, IP_Adresse)
                    Thread.sleep(1000)
                }

            } catch (e:Exception) {
                println("_")
                println("Try-Catch-Error:")
                e.printStackTrace()
                println("_")
            }
        }
        thread2.start()
    }

    fun Create_Bottom_Navigation(IP_Adresse:String?, user_key:String?) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.dashboard
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboard -> return@OnNavigationItemSelectedListener true
                R.id.home -> {
                    val unter_home = Intent(this, MainActivity::class.java)
                    unter_home.putExtra("IP_Adresse", IP_Adresse)
                    startActivity(unter_home)
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
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

    fun Get_100PVerlauf_from_Server (user_key: String?, IP_Adresse:String?) {
        for (i in 0 until Arr_Waehrungen_PHP.size step 1) {
            val URL_Data = URL( IP_Adresse +"/Send_Price_to_Client.php?u_key=" + user_key + "&sec_key=" + Arr_Waehrungen_PHP[i]+"&verlauf_100=1").readText()
            val obj = JSONObject(URL_Data)
            var Arr_preise = obj.getJSONArray("verlauf")

            for (x in 0 until 100 step 1) {
                Waehr_Verlauf_100[i][x]=Arr_preise.getJSONArray(i).getDouble(99-x)
            }
        }
        Download_Fertig=true
    }


    fun Create_Diagramms (unter_trading:Intent) {

        val Im_Button_1_1 : ImageButton = findViewById(R.id.imageButton1_1)
        Im_Button_1_1.setOnClickListener {
            val waehrung = 0 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("Waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_1_2 : ImageButton = findViewById(R.id.imageButton1_2)
        Im_Button_1_2.setOnClickListener {
            val waehrung = 0 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints1 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints1[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[0][i]*1000)
        }
        series1 = LineGraphSeries(dataPoints1)
        series1!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series1!!.setDrawBackground(true);
        graph1 = findViewById<View>(R.id.graph1) as GraphView
        graph1.addSeries(series1)
        graph1.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph1.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph1.getGridLabelRenderer().setNumVerticalLabels(4);
        graph1.viewport.setMinX(69+0.0)
        graph1.viewport.setMaxX(69+Zeitspanne)
        graph1.setTitleTextSize(60f)
        graph1.viewport.isXAxisBoundsManual = true
        graph1.viewport.isScalable = true
        //val formatter: Format = SimpleDateFormat("mm:ss")
        //graph1.getGridLabelRenderer().setLabelFormatter(formatter)
        //graph1.getGridLabelRenderer().setLabelFormatter(DateAsXAxisLabelFormatter(graph1.getContext()));
        /*graph1.getGridLabelRenderer().setLabelFormatter(object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val formatter: Format = SimpleDateFormat("mm:ss")
                    return formatter.format(value)
                }
                return super.formatLabel(value, isValueX)
            }
        })*/


        val Im_Button_2_1 : ImageButton = findViewById(R.id.imageButton2_1)
        Im_Button_2_1.setOnClickListener {
            val waehrung = 1 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_2_2 : ImageButton = findViewById(R.id.imageButton2_2)
        Im_Button_2_2.setOnClickListener {
            val waehrung = 1 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints2 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints2[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[1][i])
        }
        series2 = LineGraphSeries(dataPoints2)
        series2!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series2!!.setDrawBackground(true);
        graph2 = findViewById<View>(R.id.graph2) as GraphView
        graph2.addSeries(series2)
        graph2.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph2.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph2.getGridLabelRenderer().setNumVerticalLabels(4);
        graph2.viewport.setMinX(69+0.0)
        graph2.viewport.setMaxX(69+Zeitspanne)
        graph2.setTitleTextSize(60f)
        graph2.viewport.isXAxisBoundsManual = true
        graph2.viewport.isScalable = true


        val Im_Button_3_1 : ImageButton = findViewById(R.id.imageButton3_1)
        Im_Button_3_1.setOnClickListener {
            val waehrung = 2 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_3_2 : ImageButton = findViewById(R.id.imageButton3_2)
        Im_Button_3_2.setOnClickListener {
            val waehrung = 2 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints3 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints3[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[2][i]*1000)
        }
        series3 = LineGraphSeries(dataPoints3)
        series3!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series3!!.setDrawBackground(true);
        graph3 = findViewById<View>(R.id.graph3) as GraphView
        graph3.addSeries(series3)
        graph3.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph3.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph3.getGridLabelRenderer().setNumVerticalLabels(4);
        graph3.viewport.setMinX(69+0.0)
        graph3.viewport.setMaxX(69+Zeitspanne)
        graph3.setTitleTextSize(60f)
        graph3.viewport.isXAxisBoundsManual = true
        graph3.viewport.isScalable = true


        val Im_Button_4_1 : ImageButton = findViewById(R.id.imageButton4_1)
        Im_Button_4_1.setOnClickListener {
            val waehrung = 3 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_4_2 : ImageButton = findViewById(R.id.imageButton4_2)
        Im_Button_4_2.setOnClickListener {
            val waehrung = 3 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints4 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints4[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[3][i]*1000)
        }
        series4 = LineGraphSeries(dataPoints4)
        series4!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series4!!.setDrawBackground(true);
        graph4 = findViewById<View>(R.id.graph4) as GraphView
        graph4.addSeries(series4)
        graph4.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph4.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph4.getGridLabelRenderer().setNumVerticalLabels(4);
        graph4.viewport.setMinX(69+0.0)
        graph4.viewport.setMaxX(69+Zeitspanne)
        graph4.setTitleTextSize(60f)
        graph4.viewport.isXAxisBoundsManual = true
        graph4.viewport.isScalable = true


        val Im_Button_5_1 : ImageButton = findViewById(R.id.imageButton5_1)
        Im_Button_5_1.setOnClickListener {
            val waehrung = 4 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_5_2 : ImageButton = findViewById(R.id.imageButton5_2)
        Im_Button_5_2.setOnClickListener {
            val waehrung = 4 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints5 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints5[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[4][i]*1000)
        }
        series5 = LineGraphSeries(dataPoints5)
        series5!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series5!!.setDrawBackground(true);
        graph5 = findViewById<View>(R.id.graph5) as GraphView
        graph5.addSeries(series5)
        graph5.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph5.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph5.getGridLabelRenderer().setNumVerticalLabels(4);
        graph5.viewport.setMinX(69+0.0)
        graph5.viewport.setMaxX(69+Zeitspanne)
        graph5.setTitleTextSize(60f)
        graph5.viewport.isXAxisBoundsManual = true
        graph5.viewport.isScalable = true


        val Im_Button_6_1 : ImageButton = findViewById(R.id.imageButton6_1)
        Im_Button_6_1.setOnClickListener {
            val waehrung = 5 //0-5 für Währungsname
            val long_short = 0 // 0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val Im_Button_6_2 : ImageButton = findViewById(R.id.imageButton6_2)
        Im_Button_6_2.setOnClickListener {
            val waehrung = 5 //0-5 für Währungsname
            val long_short = 1 //0-1 für Short/Long
            unter_trading.putExtra("waehrung", waehrung)
            unter_trading.putExtra("long_short", long_short)
            startActivity(unter_trading)
        }
        val dataPoints6 = arrayOfNulls<DataPoint>(100)
        for (i in 0 until 100) {
            dataPoints6[i] = DataPoint(i.toDouble(), Waehr_Verlauf_100[5][i])
        }
        series6 = LineGraphSeries(dataPoints6)
        series6!!.setBackgroundColor(Color.argb(60, 152, 245, 255));
        series6!!.setDrawBackground(true);
        graph6 = findViewById<View>(R.id.graph6) as GraphView
        graph6.addSeries(series6)
        graph6.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        graph6.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph6.getGridLabelRenderer().setNumVerticalLabels(4);
        graph6.viewport.setMinX(69+0.0)
        graph6.viewport.setMaxX(69+Zeitspanne)
        graph6.setTitleTextSize(60f)
        graph6.viewport.isXAxisBoundsManual = true
        graph6.viewport.isScalable = true

        addRandomDataPoint(1.0,1.0,1.0,1.0,1.0,1.0)
    }

    fun Update_Price_from_PHP_Server(waehrung:String, waer_id:Int, user_key: String?, IP_Adresse:String?) {
        val URL_Data = URL( IP_Adresse + "/Send_Price_to_Client.php?u_key=" + user_key + "&sec_key=" + waehrung+"&verlauf_100=0").readText()
        val obj = JSONObject(URL_Data)
        Arr_price_main[waer_id] = obj.getDouble("Price")
        var ttt = obj.getString("Date_Time")
        Arr_time_main[waer_id] = ttt.substring(ttt.length-5, ttt.length)
    }




    private fun addRandomDataPoint(p1:Double, p2:Double, p3:Double, p4:Double, p5:Double, p6:Double) {
        mHandler.postDelayed({
            //var price1 = 1.187 + rnd.nextInt(11).toDouble()/10000
            //price1 = (Math.round(price1 * 10000) / 10000.0)
            val price1 = Arr_price_main[0]
            graph1.title = "         EUR/USD    "+price1+"               "+Arr_time_main[0]
            if (p1>price1) graph1.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph1.setTitleColor(Color.argb( 200,0, 238, 0))

            val price2 = Arr_price_main[1]
            graph2.title = "         USD/JPY    "+price2+"               "+Arr_time_main[1]
            if (p2>price2) graph2.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph2.setTitleColor(Color.argb(200, 0, 238, 0))

            val price3 = Arr_price_main[2]
            graph3.title = "         GBP/USD    "+price3+"               "+Arr_time_main[2]
            if (p3>price3) graph3.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph3.setTitleColor(Color.argb( 200,0, 238, 0))

            val price4 = Arr_price_main[3]
            graph4.title = "         AUD/USD    "+price4+"               "+Arr_time_main[3]
            if (p4>price4) graph4.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph4.setTitleColor(Color.argb( 200,0, 238, 0))

            val price5 = Arr_price_main[4]
            graph5.title = "         USD/CHF    "+price5+"               "+Arr_time_main[4]
            if (p5>price5) graph5.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph5.setTitleColor(Color.argb( 200,0, 238, 0))

            val price6 = Arr_price_main[5]
            graph6.title = "         BTC/USD    "+price6+"          "+Arr_time_main[5]
            if (p6>price6) graph6.setTitleColor(Color.argb( 200,255, 69, 0))
            else graph6.setTitleColor(Color.argb( 200,0, 238, 0))

            ZZZ++
            var Scroll_End : Boolean
            if (ZZZ>Zeitspanne-1) {
                Scroll_End = true
            } else {
                Scroll_End = false
            }
            series1!!.appendData(DataPoint(ZZZ, price1*1000), Scroll_End, 100)  //
            series2!!.appendData(DataPoint(ZZZ, price2), Scroll_End, 100)
            series3!!.appendData(DataPoint(ZZZ, price3*1000), Scroll_End, 100)
            series4!!.appendData(DataPoint(ZZZ, price4*1000), Scroll_End, 100)
            series5!!.appendData(DataPoint(ZZZ, price5*1000), Scroll_End, 100)
            series6!!.appendData(DataPoint(ZZZ, price6), Scroll_End, 100)
            addRandomDataPoint(price1, price2, price3, price4, price5, price6)
        }, 1000)
    }
}