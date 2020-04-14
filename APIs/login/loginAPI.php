<?php

$conn = new mysqli('localhost', 'u413856335_uri', 'Shiv@2015','u413856335_uri');
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if($_SERVER['REQUEST_METHOD'] == "POST"){
	//$rest_point = isset($_POST['rest_point']) ? mysql_real_escape_string($_POST['rest_point']) : "";
	$mobileNo = $_POST['mobileNo'];
	$password = $_POST['password'];
	$sql = "SELECT `user_id`, `user_name`, `email_id` FROM `user_info` WHERE `contact_number` = '".$mobileNo."' AND `password_hash` = '".$password."'";
	
	$result = $conn->query($sql);


	if ($result->num_rows > 0) {
	    $status = $status.' : got result : '; 
	    // output data of each row
	    while($row = $result->fetch_assoc()) {
	    	$json = array("status" => 0, "user_id" => $row["user_id"], "user_name" =>  $row["user_name"], "email_id" =>  $row["email_id"]);
	    	break;
	    }
	} else {
	     $json = array("status" => 1, "error_desc" => "Invalid user id or password");
	}

}else{
	$json = array("status" => 2, "error_desc" => "Invalid request type");
}

$conn->close();

/* Output header */
	header('Content-type: application/json');
	echo json_encode($json);
?>