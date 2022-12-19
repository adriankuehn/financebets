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
			$con_his = mysqli_connect("localhost", "dbo00******", "*****************", "db00******");  //db_user_history
			$user_key = $_REQUEST["u_key"];
			$History_Table_Name = "history_" . $user_key;
			$response = mysqli_query($con, "Update users set coins=10000 where user_key='$user_key'");
			$response = mysqli_query($con, "Update users set anz_wetten=0 where user_key='$user_key'");
			$response = mysqli_query($con_his, "Delete from $History_Table_Name");
			mysqli_close($con_his);
			
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
	
	$con = mysqli_connect("localhost", "dbo00******", "*****************", "db00******");  //db_userbets
	Give_Echo_Data(Check($con), $con);
	
?>

