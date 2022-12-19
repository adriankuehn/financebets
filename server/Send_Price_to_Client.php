<?php
	Function User_Identification($con_users, $User_K) {
		$response = mysqli_query($con_users, "SELECT * from users WHERE user_key='$User_K'");
		$Num_row=mysqli_num_rows($response); 
		if($Num_row == 0) {
			return False;
		} else {
			return True;
		}
	}
	Function Give_Echo_u_Random($Value) {    
		#0=Kein User_Key, 1=Erfolgreich, 2=Error_Identification, 4=URL_Key Falsch
		#Die vielen If-Else Anweisungen sind ntwendig um bei allen falschen url eingaben keinen echo Error zu generieren, sodass immer nur json Fehlercode ausgegeben wird
		$json = array();
		$json["Erfolgreich"] = $Value;
		if ($Value==1) {
			$con_währ = mysqli_connect("localhost", "dbo00******", "*****************", "db00******");  //db_securities
			$Table_Währ_Name = $_REQUEST["sec_key"];
			$newest_TS = intval( mysqli_fetch_assoc( mysqli_query($con_währ, "SELECT timestamp FROM $Table_Währ_Name ORDER BY timestamp DESC LIMIT 1") )["timestamp"] );
			$response = mysqli_query($con_währ, "SELECT * from $Table_Währ_Name WHERE timestamp = $newest_TS");
			if ($response) {
				$Num_rows=mysqli_num_rows($response); 
				if ($Num_rows!=0) {
					$dsatz = mysqli_fetch_assoc($response);
					$json["Timestamp"] = intval($dsatz["timestamp"]);
					$json["Price"] = floatval($dsatz["price"]);     
					$json["Date_Time"] = $dsatz["date_time"];
				} else {
					$json["Timestamp"] = 99;
					$json["Price"] = 99;
					$json["Date_Time"] = 99;
				}
			} else {
				$json["Timestamp"] = 99;
				$json["Price"] = 99;
				$json["Date_Time"] = 99;
			}
			
			$Verlauf_100 = $_REQUEST["verlauf_100"];
			if ($Verlauf_100==0) {   //keine letzten 100 kurspunkte pro währung mitgeben
				$json["verlauf"] = 99;
			} else {
				$Waehr_verlauf = array_fill(0, 6, array_fill(0, 100, 0));
				$Arr_waehrungen = array("eur_usd", "usd_jpy", "gbp_usd", "aud_usd", "usd_chf", "btc_eur");
				for ($i = 0; $i < count($Arr_waehrungen); $i++) {
					$Table_Währ_Name = $Arr_waehrungen[$i];
					$response = mysqli_query($con_währ, "SELECT * from $Table_Währ_Name ORDER BY timestamp_local_system DESC LIMIT 100");
					$Z_arr=0;
					while ($dsatz = mysqli_fetch_assoc($response)) {
						$Waehr_verlauf[$i][$Z_arr] = $dsatz["price"];
						$Z_arr+=1;	
					}
				}
				$json["verlauf"] = $Waehr_verlauf;
			}
			mysqli_close($con_währ);
			
		} else {
			$json["Timestamp"] = 99;
			$json["Price"] = 99;
			$json["Date_Time"] = 99;
			$json["verlauf"] = 99;
		}
		echo json_encode($json);
	}
	
	Function F_Close_Bet() {
		if(isset($_REQUEST["u_key"])){
			$User_K = $_REQUEST["u_key"];
			$con =mysqli_connect("localhost", "dbo00******", "****************", "db00******");  //db_userbets
			if (User_Identification($con, $User_K) == True) {
				mysqli_close($con);
				return 1; 
			} else {
				mysqli_close($con);
				return 2; 
			}
		} else {
			return 4; 
		}
	}
	
	Give_Echo_u_Random(F_Close_Bet());
?>
