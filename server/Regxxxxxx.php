<?php
	Function PW_Confirmation($con, $PW) {
		$PW_Server = "access_*********************************";
		if ($PW_Server==$PW) {
			return true;
		} else {
			return false;
		}
	}
	
	Function Give_Echo($Value) {    
		#1=Erfolgreich, 2=Error_Identification, 3=Error Insert or Update, 4=URL_Key Falsch
		$json = array();
		$json["Erfolgreich"] = $Value;
		echo json_encode($json);
	}
		
	Function F_Close_Bet() {
		if(isset($_REQUEST["u_key"])){
			$User_K = $_REQUEST["u_key"];
			$con = mysqli_connect("localhost", "dbo00******", "**************", "db00******");  //db_userbets
			$PW = $_REQUEST["pw"];
			$NAME = $_REQUEST["name"];
			if (PW_Confirmation($con, $PW) == True) {
				
				$result_ins = mysqli_query($con, "Insert Into users (user_key, coins, user_name, anz_wetten) Values ('$User_K', 10000, '$NAME', 0)");
				$con_his = mysqli_connect("localhost", "dbo00******", "**************", "db00******");  //db_user_history
				$result_create = mysqli_query($con_his, "Create Table history_$User_K (security_key varchar(30), start_time bigint(20), start_price double, end_time bigint(20), end_price double, long_short int(1), stock_move int(1), einsatz int(11), primary key (security_key, start_time))");
				mysqli_close($con_his);
				
				if(! $result_ins || !$result_create) {
					echo "Error Create Account" . mysqli_error($con) . "<br>";
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

