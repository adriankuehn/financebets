<?php	
	function Real_Stock_Value($API_Waehr_Name) {
		if ($API_Waehr_Name=="BTCEUR" || $API_Waehr_Name=="USDJPY") {
			$v_round=2;
		} else {
			$api_key="***************";
			$v_round=4;
		}
		if ($API_Waehr_Name=="BTCEUR") {
			$url = "https://api.cryptonator.com/api/ticker/btc-usd";
			$data = file_get_contents($url);
			$data = json_decode($data, true);
			$price = floatval($data["ticker"]["price"]);
		} else {
			$url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=" . substr($API_Waehr_Name, 0, 3) . "&to_currency=" .  substr($API_Waehr_Name, 3, 6) . "&apikey=**************";
			$data = file_get_contents($url);
			$data = json_decode($data, true);
			$price = floatval($data["Realtime Currency Exchange Rate"]["5. Exchange Rate"]);
		}
		echo "Price: " . round( $price, $v_round) . "<br>";
		return (round( $price, $v_round));
	}
	
	Real_Stock_Value("BTCEUR");
	Real_Stock_Value("EURUSD"); 
	Real_Stock_Value("AUDUSD");
	Real_Stock_Value("USDJPY");
	Real_Stock_Value("USDCHF");
	Real_Stock_Value("GBPUSD"); 
?>

