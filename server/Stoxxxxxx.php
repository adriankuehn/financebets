<?php
	#V1 Primary Key = Local System Time in sek, so for every second 1 value, User for Maixxxxxx.php Server
	#V2 Primary Key = Broker Time, several or no value per second, Used for Apps to get most aktive chart - not needed any more

	function Get_Num_Rows($con, $waehr_name) {
		$res = mysqli_query($con, "Select * from $waehr_name");
		$num = mysqli_num_rows($res);
		return $num;
	}
	
	function Check_Update_Real_Stock_Values_4_Times($datum) { //06:05:00, 12:05:00, 18:05:00, 00:05:00
		$hour = substr($datum, strlen($datum)-8, strlen($datum));
		if ($hour=="06:05:00" || $hour=="12:05:00" || $hour=="18:05:00" || $hour=="00:05:00") {
			return True;
		} else {
			return False;
		}
	}
	
	function Real_Stock_Value($API_Waehr_Name) {
		if ($API_Waehr_Name=="BTCEUR" || $API_Waehr_Name=="USDJPY") {
			$v_round=2;
		} else {
			$api_key="*******************";
			$v_round=4;
		}
		if ($API_Waehr_Name=="BTCEUR") {
			$url = "https://api.cryptonator.com/api/ticker/btc-usd";
			$data = file_get_contents($url);
			$data = json_decode($data, true);
			$price = floatval($data["ticker"]["price"]);
		} else {
			$url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=" . substr($API_Waehr_Name, 0, 3) . "&to_currency=" .  substr($API_Waehr_Name, 3, 6) . "&apikey=****************";
			$data = file_get_contents($url);
			$data = json_decode($data, true);
			$price = floatval($data["Realtime Currency Exchange Rate"]["5. Exchange Rate"]);
		}
		return (round( $price, $v_round));
	}
	
	
	function Update_PriceTable ($waehr_name, $con, $last_price, $API_Waehr_Name, $V_Check_Update) {
		$timestamp = time();
		$System_Timestamp = $timestamp;
		$datum = date("d.m.Y - H:i:s", $timestamp); 
		
		if ($V_Check_Update==False) {
			if ($waehr_name=="btc_eur") {
				$price = round($last_price + random_int(-2000, 2000)/100,   2); 
			} else if ($waehr_name=="usd_jpy") {
				$price = round($last_price + random_int(-4, 4)/100,   2);  	
			} else {
				$price = round($last_price + random_int(-4, 4)/10000,   4);  
			}	
		} else {
			$price = Real_Stock_Value($API_Waehr_Name);
			if ($price==null) {  //Wochenende wird null ausgegeben das Währungsmarkt pausiert => Ausnahmebehandlung
				if ($waehr_name=="btc_eur") {
					$price = round($last_price + random_int(-2000, 2000)/100,   2); 
				} else if ($waehr_name=="usd_jpy") {
					$price = round($last_price + random_int(-4, 4)/100,   2);  	
				} else {
					$price = round($last_price + random_int(-4, 4)/10000,   4);  
				}	
			}
		}
		
		
		if ($timestamp%60==0) {
			//echo "P: " . $price . "  ---    W: " . $waehr_name . "  ---    T: " . $datum . "<br>"; 
		}
		while (Get_Num_Rows($con, $waehr_name)>=200) {  
			$oldest_Timestamp = intval( mysqli_fetch_assoc( mysqli_query($con, "SELECT Timestamp_Local_System FROM $waehr_name ORDER BY Timestamp_Local_System ASC LIMIT 1") )["Timestamp_Local_System"] );
			$return_val_2 = mysqli_query($con, "DELETE FROM $waehr_name WHERE Timestamp_Local_System = $oldest_Timestamp");
			if(! $return_val_2 ) echo "Error Delete". "<br>";
		}
		$return_v1 = mysqli_query($con, "Insert into $waehr_name (Timestamp_Local_System, Timestamp, Price, Date_Time) values
											('$System_Timestamp', '$timestamp', '$price', '$datum')");
	}
	
	Function get_current_price($waehr_name, $con) {
		$newest_TS = intval( mysqli_fetch_assoc( mysqli_query($con, "SELECT timestamp FROM $waehr_name ORDER BY timestamp DESC LIMIT 1") )["timestamp"] );
		$response = mysqli_query($con, "SELECT * from $waehr_name WHERE timestamp = $newest_TS");
		$dsatz = mysqli_fetch_assoc($response);
		return floatval($dsatz["price"]); 
	}
	
	Function Update_Log_Scripts($con_user) {
		$Update_Time = time();
		if ($Update_Time%5==0) {
			$Update_Time_D = date("d.m.Y - H:i:s", $Update_Time);
			$resp_update = mysqli_query($con_user, "Update log_scripts set timestamp='$Update_Time', date_time='$Update_Time_D' where name='strore_data_random'");
			if (! $resp_update ) {
				echo "Error Update Log Script: " . mysqli_error($con_user) . "<br>";
				lop;
			}
			
		}
	}
	
	
	
	ini_set('max_execution_time', 2000);
	$con = mysqli_connect("localhost", "dbo00******", "********************", "db00******");  //db_securities
	$con_user = mysqli_connect("localhost", "dbo00******", "*******************", "db00******");  //db_userbets
	
	
	$myfile = fopen('Change1.txt', 'w');   //Shuts down all existing Stoxxxxxx.php
	fclose($myfile);
	usleep( 3 * 1000000 );       //3 sekunden warten, damit vorherige Skriptversion sich schließen kann
	$Start_Time_Php = time();
	
	while (true) {
		if (file_exists('Stop.txt')) {
			echo "Abbruch PHP-Script, wegen Stop <br>";
			break;
		}
		if ((time()-$Start_Time_Php) >40) {  //Stop php script because new version startet
			if (file_exists('Change1.txt')) {
				//echo "dif break" . time()-$Start_Time_Php. "<br>";
				break;
			}
		}
		if ((time()-$Start_Time_Php) >20) {  //Delete 
			if (file_exists('Change1.txt')) {
				//echo "dif delete" . time()-$Start_Time_Php. "<br>";
				unlink("Change1.txt");
			}
		}
		
		Update_Log_Scripts($con_user);
		$last_price_eur_usd=get_current_price("eur_usd", $con);
		$last_price_usd_jpy=get_current_price("usd_jpy", $con);
		$last_price_gbp_usd=get_current_price("gbp_usd", $con);
		$last_price_aud_usd=get_current_price("aud_usd", $con);
		$last_price_usd_chf=get_current_price("usd_chf", $con);
		$last_price_btc_eur=get_current_price("btc_eur", $con);
		
		$V_Check_Update = Check_Update_Real_Stock_Values_4_Times( date("d.m.Y - H:i:s", time()) );
		Update_PriceTable("eur_usd", $con, $last_price_eur_usd, "EURUSD", $V_Check_Update);
		Update_PriceTable("usd_jpy", $con, $last_price_usd_jpy, "USDJPY", $V_Check_Update);
		Update_PriceTable("gbp_usd", $con, $last_price_gbp_usd, "GBPUSD", $V_Check_Update);
		Update_PriceTable("aud_usd", $con, $last_price_aud_usd, "AUDUSD", $V_Check_Update);
		Update_PriceTable("usd_chf", $con, $last_price_usd_chf, "USDCHF", $V_Check_Update);
		Update_PriceTable("btc_eur", $con, $last_price_btc_eur, "BTCEUR", $V_Check_Update);
		
		usleep( 0.4 * 1000000 );  
		# muss unter einer sekunde sein, sodass ohne Probleme für jede Sekunde ein Datenfeld zur verfügung steht, sonst kann wette mit end_time nicht abgeschlossen werden
		# in Maixxxxxx     => mehrfache Einträge pro Sekunde sind ausgeschlossen wegen primary key 
		# Eine DAtenbankabfrage wie get_current_price() dauert 0.001 sekunden im schnitt
	}
	mysqli_close($con);  
	mysqli_close($con_user);  
?>

