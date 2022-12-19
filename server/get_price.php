<?php
	$json = array();
	
	Function get_current_price($waehr_name, $con) {
		$newest_TS = intval( mysqli_fetch_assoc( mysqli_query($con, "SELECT timestamp FROM $waehr_name ORDER BY timestamp DESC LIMIT 1") )["timestamp"] );
		$response = mysqli_query($con, "SELECT * from $waehr_name WHERE timestamp = $newest_TS");
		$dsatz = mysqli_fetch_assoc($response);
		return floatval($dsatz["price"]); 
	}
	
	
	$con = mysqli_connect("localhost", "dbo00******", "**************", "db00******");  //db_securities
	$json["EURUSD"] = round( get_current_price("eur_usd", $con), 4);
	$json["USDJPY"] = round( get_current_price("usd_jpy", $con), 2);
	$json["GBPUSD"] = round( get_current_price("gbp_usd", $con), 4);
	$json["AUDUSD"] = round( get_current_price("aud_usd", $con), 4);
	$json["USDCHF"] = round( get_current_price("usd_chf", $con), 4);
	$json["BTCUSD"] = round( get_current_price("btc_eur", $con), 2);
	mysqli_close($con); 
	
	
	echo json_encode($json);     
?>