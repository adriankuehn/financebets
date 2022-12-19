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
	Function get_current_price($waehr_name, $con_währ) {
		$newest_TS = intval( mysqli_fetch_assoc( mysqli_query($con_währ, "SELECT timestamp FROM $waehr_name ORDER BY timestamp DESC LIMIT 1") )["timestamp"] );
		$response = mysqli_query($con_währ, "SELECT * from $waehr_name WHERE timestamp = $newest_TS");
		$dsatz = mysqli_fetch_assoc($response);
		return floatval($dsatz["price"]); 
	}
	
	
	Function Give_Echo_Data($Value, $con) {    
		#0=Kein User_Key, 1=Erfolgreich, 2=Error_Identification, 4=URL_Key Falsch
		$json = array();
		$json["Erfolgreich"] = $Value;
		if ($Value==1) {
			$User_K = $_REQUEST["u_key"];
			$response1 = mysqli_query($con, "SELECT * from current_bets WHERE user_key='$User_K' ORDER BY start_time DESC LIMIT 50");
			$Arr_ActiveBets = array_fill(0, mysqli_num_rows($response1), array_fill(0, 7, 0.0));
			$Z=0;
			$Arr_Waehrungen = array("eur_usd","usd_jpy","gbp_usd","aud_usd","usd_chf", "btc_eur");
			$con_währ = mysqli_connect("localhost", "dbo00******", "****************", "db00******");  //db_securities
			while ($dsatz1 = mysqli_fetch_assoc($response1)) {
				for ($i = 0; $i < count($Arr_Waehrungen); $i++) {
					if ($Arr_Waehrungen[$i]==$dsatz1["security_key"]) {
						$Arr_ActiveBets[$Z][0]=floatval($i);
						break;
					}
				}
				$Arr_ActiveBets[$Z][1] = floatval($dsatz1["long_short"]);
				$Arr_ActiveBets[$Z][2] = floatval($dsatz1["einsatz"]);
				$Arr_ActiveBets[$Z][3] = floatval($dsatz1["end_time"]-$dsatz1["start_time"]);
				$Arr_ActiveBets[$Z][4] = floatval($dsatz1["start_price"]);
				$Arr_ActiveBets[$Z][5] = get_current_price($dsatz1["security_key"], $con_währ);
				$Arr_ActiveBets[$Z][6] = floatval($dsatz1["end_time"]-time());
				$Z+=1;	
			}
			$json["arr_activebets"] = $Arr_ActiveBets;
			
		} else {
			$json["arr_activebets"] = 99;
		}
		
		mysqli_close($con);
		mysqli_close($con_währ);
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
	
	$con = mysqli_connect("localhost", "dbo00******", "****************", "db00******");  //db_userbets
	Give_Echo_Data(Check($con), $con);
	
?>

