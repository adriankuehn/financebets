<?php
	Function User_Identification($User_K, $con) {
		$response = mysqli_query($con, "SELECT * from users WHERE user_key='$User_K'");
		$Num_row=mysqli_num_rows($response); 
		if($Num_row == 0) {
			return False;
		} else {
			return True;
		}
	}
	Function Give_Echo_Data($Value, $con) {    
		#0=Kein User_Key, 1=Erfolgreich, 2=Error_Identification, 4=URL_Key Falsch
		$json = array();
		$json["Erfolgreich"] = $Value;
		if ($Value==1) {
			$User_K = $_REQUEST["u_key"];
			$response1 = mysqli_query($con, "SELECT * from users WHERE user_key='$User_K'");
			$dsatz1 = mysqli_fetch_assoc($response1);
			$response2 = mysqli_query($con, "SELECT user_name, coins FROM `users` ORDER BY coins DESC LIMIT 10");
			$BestenL_Arr_name = array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
			$BestenL_Arr_coins = array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
			$con_his = mysqli_connect("localhost", "dbo00******", "*****************", "db00******");  //db_user_history
			$response3 = mysqli_query($con_his, "SELECT * FROM history_$User_K ORDER BY end_time DESC LIMIT 30");
			$response4 = mysqli_query($con_his, "SELECT * FROM history_$User_K ORDER BY end_time DESC LIMIT 199");
			mysqli_close($con_his);
			$last_active_maintane = mysqli_fetch_assoc(mysqli_query($con, "SELECT date_time from log_scripts WHERE name='maintane_database'"))["date_time"];
			$last_active_store = mysqli_fetch_assoc(mysqli_query($con, "SELECT date_time from log_scripts WHERE name='strore_data_random'"))["date_time"];
			$resp_platz = mysqli_query($con, "SELECT user_key from users ORDER BY coins DESC");
			
			
			$Wettverlauf = array_fill(0, 30, array_fill(0, 8, 0)); 
			$Z=0;
			
			while ($dsatz2 = mysqli_fetch_assoc($response2)) {
				$BestenL_Arr_coins[$Z] = $dsatz2["coins"];
				$BestenL_Arr_name[$Z] = $dsatz2["user_name"];
				$Z+=1;	
			}
			
			$Z=0;
			while ($dsatz3 = mysqli_fetch_assoc($response3)) {
				$Wettverlauf[$Z][0] = $dsatz3["security_key"];
				$Wettverlauf[$Z][1] = $dsatz3["start_time"];
				$Wettverlauf[$Z][2] = $dsatz3["start_price"];
				$Wettverlauf[$Z][3] = $dsatz3["end_time"];
				$Wettverlauf[$Z][4] = $dsatz3["end_price"];
				$Wettverlauf[$Z][5] = $dsatz3["long_short"];
				$Wettverlauf[$Z][6] = $dsatz3["stock_move"];
				$Wettverlauf[$Z][7] = $dsatz3["einsatz"];
				$Z+=1;	
			}
			
			$Coinsverlauf = array_fill(0, 200, 0); 
			$Coins_Aktuell = intval($dsatz1["coins"]);	
			$Coinsverlauf[0] = $Coins_Aktuell;
			$Z=1;
			while ($dsatz4 = mysqli_fetch_assoc($response4)) {
				if ($dsatz4["stock_move"]==2) {                             //Neutral
					$Coins_Vorher = $Coins_Aktuell;
				} else if ($dsatz4["long_short"]==$dsatz4["stock_move"]) {  //Gewinn
					$Coins_Vorher = $Coins_Aktuell - $dsatz4["einsatz"];
				} else if ($dsatz4["long_short"]!=$dsatz4["stock_move"]) {  //Verlust
					$Coins_Vorher = $Coins_Aktuell + $dsatz4["einsatz"];
				}
				
				$Coinsverlauf[$Z] = $Coins_Vorher;
				$Coins_Aktuell = $Coins_Vorher;
				$Z+=1;	
			}
			
			$Z_Pl=1;
			$Platzierung=12345;
			while ($dsatz_pl = mysqli_fetch_assoc($resp_platz)) {
				if ($dsatz_pl["user_key"]==$User_K) {
					$Platzierung=$Z_Pl;
					break;
				}
				$Z_Pl+=1;	
			}
			
			
			$json["user_name"] = $dsatz1["user_name"];
			$json["coins"] = intval($dsatz1["coins"]);
			$json["anz_wetten"] = intval($dsatz1["anz_wetten"]);
			$json["bestenl_name"] = $BestenL_Arr_name;
			$json["bestenl_coins"] = $BestenL_Arr_coins;
			$json["wettverlauf"] = $Wettverlauf;
			$json["coinsverlauf"] = $Coinsverlauf;
			$json["last_active_maintane"] = $last_active_maintane;
			$json["last_active_store"] = $last_active_store;
			$json["platz"] = $Platzierung;
			
		} else {
			$json["user_name"] = "99";
			$json["coins"] = 99;
			$json["anz_wetten"] = 99;
			$json["bestenl_name"] = 99;
			$json["bestenl_coins"] = 99;
			$json["wettverlauf"] = 99;
			$json["coinsverlauf"] = 99;
			$json["last_active_maintane"] = 99;
			$json["last_active_store"] = 99;
			$json["platz"] = 99;
		}
		
		mysqli_close($con);
		echo json_encode($json);
	}
	
	Function Check($con) {
		if(isset($_REQUEST["u_key"])){
			$User_K = $_REQUEST["u_key"];
			if (User_Identification($User_K, $con) == True) {
				return 1; 
			} else {
				return 2; 
			}
		} else {
			return 4; 
		}
	}
	
	$con = mysqli_connect("localhost", "dbo00******", "******************", "db00******");  //db_userbets
	Give_Echo_Data(Check($con), $con);
	
?>

