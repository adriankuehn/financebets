<?php      
	
	#   localhost/phpmyadmin
	#   long=0, short=1, constant=2
	#   https://www.topster.de/kalender/unixzeit.php      1618911120 == 09:32:00   
	
	#/****NOT NEEDED
	function Insert_TestUserBets($con, $T) { 
		$end_time=$T+30;
		$result_ins = mysqli_query($con, "Insert Into current_bets (user_key, security_key, start_time, start_price, end_time, long_short, einsatz)
										Values ('1aaaaabbbbbcccccddddd1111', 'eur_usd', '$T', 1.2222, $end_time, 0, 200),
										('2aaaaabbbbbcccccddddd2222', 'eur_usd', '$T', 1.0000, $end_time, 1, 500),
										('4aaaaabbbbbcccccddddd4444', 'eur_usd', '$T', 1.1111, $end_time, 0, 300)");
		//echo "Test_Bets eingefügt <br>";
		if(! $result_ins ) echo "Error Insert TestUserBets: " . mysqli_error($con) . "<br>"; 
	}     
	function Delete_old_Bets($T, $Time_Back, $con) {
		# Alte Wetten älter als $Time_Back in min werden gelöscht
		$Back_Value = $T-$Time_Back*60;
		$response = mysqli_query($con, "DELETE from current_bets where start_time < '$Back_Value'");
		if(! $response ) echo "Error delete old bets" . mysqli_error($con) . "<br>"; 
	}
	#/****NOT NEEDED
	
	
	function  Delete_Bets_Older_than_500($History_Table_Name, $con_hist) {
		$anz_rows_history = mysqli_num_rows(mysqli_query($con_hist, "SELECT * FROM $History_Table_Name"));
		while ($anz_rows_history>10) {
			$oldest_endtime = intval( mysqli_fetch_assoc( mysqli_query($con_hist, "SELECT end_time FROM $History_Table_Name ORDER BY end_time ASC LIMIT 1") )["end_time"] );
			$response_del = mysqli_query($con_hist, "Delete from $History_Table_Name WHERE end_time=$oldest_endtime");
			if (! $response_del ) echo "Error Delete Bets older than 500: " . mysqli_error($con_hist) . "<br>";
			$anz_rows_history = mysqli_num_rows(mysqli_query($con_hist, "SELECT * FROM $History_Table_Name"));
		}
	}
	
	
	function Check_if_Bet_End_and_Close($con, $T, $con_währ, $con_hist) {
		$response_1 = mysqli_query($con, "Select * from current_bets");
		while ($dsatz = mysqli_fetch_assoc($response_1)) {
			$end_time = $dsatz["end_time"];
			$Security_Table_Name = $dsatz["security_key"];
			$response_2 = mysqli_query($con_währ , "Select price from $Security_Table_Name where timestamp_local_system=$end_time");
			$Num_rows=mysqli_num_rows($response_2); 
			
			if ($Num_rows!=0) {
				
				$end_price = mysqli_fetch_assoc($response_2)["price"];
				$user_key = $dsatz["user_key"];
				$History_Table_Name = "history_" . $user_key;
				$security_key = $dsatz["security_key"];
				$start_time = $dsatz["start_time"];
				$start_price = $dsatz["start_price"];
				$long_short = $dsatz["long_short"];
				$einsatz = $dsatz["einsatz"];
				
				$Value_Bearb = mysqli_fetch_assoc(mysqli_query($con, "Select inbearbeitung from current_bets where user_key='$user_key' and security_key='$security_key' and start_time=$start_time"))["inbearbeitung"];
				if ($Value_Bearb==0) {  //Benötigt da Skript jede halbe h aufgerufen wird und teilweise parallel gleichzeitig läuft, sonst könnten wetten 2 mal geschlossen werden
					$respo_Bearb2 = mysqli_query($con, "Update current_bets set inbearbeitung=1  where user_key='$user_key' and security_key='$security_key' and start_time=$start_time");
					
					if ($start_price<$end_price) {    //Long
						$stock_move = 1;
					} else if ($start_price>$end_price) {   //Short
						$stock_move = 0;
					} else {
						$stock_move = 2;              //constant
					}
					$coins_aktuell = mysqli_query($con, "Select coins from users where user_key = '$user_key'");
					$coins_aktuell = mysqli_fetch_assoc($coins_aktuell)["coins"];
					if ($stock_move==2) {
						$coins_neu = $coins_aktuell;
					} else if ($stock_move==$long_short) {
						$coins_neu = $coins_aktuell + $einsatz;
					} else {
						$coins_neu = $coins_aktuell - $einsatz;
					}
					$response_3 = mysqli_query($con, "Update users set coins='$coins_neu' where user_key='$user_key'");
					if (! $response_3 ) echo "Error Update coins: " . mysqli_error($con) . "<br>";
					
					$anz_wetten = mysqli_query($con, "Select anz_wetten from users where user_key = '$user_key'");
					$anz_wetten = mysqli_fetch_assoc($anz_wetten)["anz_wetten"];
					$anz_wetten_neu = $anz_wetten+1;
					$response_3_2 = mysqli_query($con, "Update users set anz_wetten='$anz_wetten_neu' where user_key='$user_key'");
					if (! $response_3_2 ) echo "Error Update anz_wetten: " . mysqli_error($con) . "<br>";
					
					
					$response_4 = mysqli_query($con_hist, "Insert into $History_Table_Name (security_key, start_time, start_price, end_time, end_price,
						long_short, stock_move, einsatz) Values ('$security_key', '$start_time', '$start_price', '$end_time', '$end_price',
						'$long_short', '$stock_move', '$einsatz')");
					Delete_Bets_Older_than_500($History_Table_Name, $con_hist);   //History_Table soll nur letzten 1000 Wetten vom User speichern, wegen Speicherplatz
					
					if (! $response_4 ) echo "Error Insert Bet Into History: " . mysqli_error($con_hist) . "<br>";
					if (! $response_3 || ! $response_4) {
						echo "Endpreis gefunden, Wette konnte trotzdem nicht geschlossen werden! <br>";
					} else {
						$response_5 = mysqli_query($con, "Delete from current_bets where user_key='$user_key' and security_key='$security_key' and start_time=$start_time");
						//echo "Wette $user_key, $security_key, $start_time erfolgreich geschlossen <br>";
						if (! $response_5 ) echo "Error Delete Current Bet: " . mysqli_error($con) . "<br>";
					}
				}
			}
		}
	}
	
	Function Update_Log_Scripts($con) {
		$Update_Time = time();
		if ($Update_Time%5==0) {
			$Update_Time_D = date("d.m.Y - H:i:s", $Update_Time);
			$resp_update = mysqli_query($con, "Update log_scripts set timestamp='$Update_Time', date_time='$Update_Time_D' where name='maintane_database'");
			if (! $resp_update ) {
				echo "Error Update Log Script: " . mysqli_error($con) . "<br>";
			}
		}
	}
	
	ini_set('max_execution_time', 2000);
	ob_implicit_flush(true);
	ob_end_flush();
	$con = mysqli_connect("localhost", "dbo00******", "***************", "db00******");  //db_userbets
	$con_währ = mysqli_connect("localhost", "dbo00******", "***************", "db00******"); //db_securities
	$con_hist = mysqli_connect("localhost", "dbo00******", "***************", "db00******"); //db_user_history
	
	#Insert_TestUserBets($con, time());
	
	$myfile = fopen('Change2.txt', 'w');   //Shuts down all existing Maixxxxxx.php
	fclose($myfile);
	usleep( 3 * 1000000 );       //3 sekunden warten, damit vorherige Skriptversion sich schließen kann
	$Start_Time_Php = time();
	
	while (true) {     
		if (file_exists('Stop.txt')) {
			echo "Abbruch PHP-Script, wegen Stop <br>";
			break;
		}
		if ((time()-$Start_Time_Php) >40) {  //Stop php script because new version startet
			if (file_exists('Change2.txt')) {
				//echo "dif break" . time()-$Start_Time_Php. "<br>";
				break;
			}
		}
		if ((time()-$Start_Time_Php) >20) {  //Delete 
			if (file_exists('Change2.txt')) {
				//echo "dif delete" . time()-$Start_Time_Php. "<br>";
				unlink("Change2.txt");
			}
		}

		if (file_exists('Stop.txt')) {
			echo "Abbruch PHP-Script, wegen Stop <br>";
			break;
		}
	
		$T = time();
		if ($T%60==0) {
			//echo "run T: $T<br>";
		}
		
		Update_Log_Scripts($con);
		Check_if_Bet_End_and_Close($con, $T, $con_währ, $con_hist);
		#Delete_old_Bets($T, (10), $con);                           
		usleep( 0.4 * 1000000 );;
	}										
	
	mysqli_close($con);
?>

