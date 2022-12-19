<?php
	Function User_Identification($con, $User_K) {
		$response = mysqli_query($con, "SELECT * from users WHERE user_key='$User_K'");
		$Num_row=mysqli_num_rows($response); 
		if($Num_row == 0) {
			return False;
		} else {
			$dsatz = mysqli_fetch_assoc($response);
			return True;
		}
	}
	
	Function Give_Echo($Value) {    
		#0=Kein User_Key, 1=Erfolgreich, 2=Error_Identification, 3=Error Insert or Update, 4=URL_Key Falsch
		$json = array();
		$json["Erfolgreich"] = $Value;
		echo json_encode($json);
	}
		
	Function F_Close_Bet() {
		if(isset($_REQUEST["u_key"])){
			$User_K = $_REQUEST["u_key"];
			$con = mysqli_connect("localhost", "dbo00******", "*****************", "db00******");  //db_userbets
			if (User_Identification($con, $User_K) == True) {
				$Security_Key = $_REQUEST["sec_key"];
				$Start_Time = $_REQUEST["start_time"];
				$Start_Price = $_REQUEST["start_price"];
				$End_Time = $_REQUEST["end_time"];
				$Long_Short = $_REQUEST["l_s"];
				$Einsatz = $_REQUEST["einsatz"];
				
				$result_ins = mysqli_query($con, "Insert Into current_bets (user_key, security_key, start_time, start_price,
					end_time, long_short, einsatz) Values ('$User_K', '$Security_Key', '$Start_Time', '$Start_Price', 
					'$End_Time', '$Long_Short', '$Einsatz')");
				if(! $result_ins ) {
					echo "Error Insert Bet" . mysqli_error($con) . "<br>";
					mysqli_close($con);
					return 3;
				}
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
	
	Give_Echo(F_Close_Bet());
?>

