<?php
/*
 * Basic HTTP message router between two peers.
 * Works with Android & CO.
 */
	function get_db() {
		$db = new SQLite3('../db/gcm.db');
		// $db->busyTimeout(5000);
		$db->exec("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT,
			uid VARCHAR(250), regid VARCHAR(250), time DATETIME)");
		$db->exec("CREATE TABLE IF NOT EXISTS messages(id INTEGER PRIMARY KEY AUTOINCREMENT,
			fromid VARCHAR(250), toid VARCHAR(250), time DATETIME)");
		return $db;
	}

	function add_user($uid, $regid) {
		remove_user($uid, $regid);

		$sql = "insert into users(uid, regid, time) values('%s', '%s', DATETIME())";
		$db = get_db();
		$db->exec(sprintf($sql, $uid, $regid));
	}

	function remove_user($uid, $regid) {
		$db = get_db();

		$sql = "delete from users where uid = '%s' and regid = '%s'";

		$db = get_db();
		$db->exec(sprintf($sql, $uid, $regid));
	}

	function get_regid($uid) {
		$db = get_db();

		$sql = "select * from users where uid = '%s'";
		$results = $db->query(sprintf($sql, $uid));

		while ($row = $results->fetchArray()) {
			$regid = $row['regid'];
			if (strlen($regid) > 0) {
				return $regid;
			}
		}
		return null;
	}

	function do_push($from, $regid) {
		$url = 'https://android.googleapis.com/gcm/send';
		$fields = array(
			'registration_ids' => array($regid),
			'data' => array('messageid' => $from),
		);

		$headers = array(
			'Authorization: key=' . 'AIzaSyCyNzvkn9OPc138AaG1xSCLyIOtS7DLTEI',
			'Content-Type: application/json'
		);

		// Open connection
		$ch = curl_init();

		// Set the url, number of POST vars, POST data
		curl_setopt($ch, CURLOPT_URL, $url);

		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

		// Disabling SSL Certificate support temporarly
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

		// Execute post
		$result = curl_exec($ch);
		if ($result === FALSE) {
			die('Curl failed: ' . curl_error($ch));
		}

		echo 'result: ' . $result;

		// Close connection
		curl_close($ch);
	}

	function push_message($from, $to, $message) {
		$db = get_db();

		$regid = get_regid($to);

		if ($regid != null) {
			$sql = "insert into messages(fromid, toid, time) values('%s', '%s', DATETIME())";

			$db = get_db();
			$db->exec(sprintf($sql, $from, $to, $message));
			$messageid = $db->lastInsertRowid();

			file_put_contents($messageid, $message);

			// do the push if has regid

			if ($regid != 'j2se') {
				do_push(messageid, $regid);
				echo 'pushed';
			} else {
				echo $messageid;
			}
		} else {
			echo 'sender not found';
		}
	}

	function pop_message($to) {
		$db = get_db();

		$result = '';
		$sql = "select * from messages where toid = '%s' order by time asc";
		$results = $db->query(sprintf($sql, $to));

		$json = '{"from": "%s", "to": "%s", "messageid" : %d, "time": %d}';
		if ($row = $results->fetchArray()) {
			$messageid = $row['id'];
			$result = sprintf($json, urldecode($row['fromid']), urldecode($row['toid']), $row['id'], strtotime($row['time']));

			$messageid = $row['id'];
			$sql = "delete from messages where id = " . $row['id'];
			$db->exec($sql);
		}

		return $result;
	}

	function list_users() {
		$db = get_db();

		$sql = "select * from users";
		$results = $db->query($sql);

		echo 'Users: <br />';

		while ($row = $results->fetchArray()) {
			$regid = urldecode($row['regid']);
			$regid = strlen($regid) > 10 ? substr($regid, 0, 10) . "..." : $regid;
			$time = date("H:i, d/m", intval(strtotime($row['time'])));

			$format = "%s -> %s [%s] <br />";
			echo sprintf($format, $time, urldecode($row['uid']), $regid);
		}

		echo '<br />';
	}

	function list_messages() {
		$db = get_db();

		$sql = "select * from messages";
		$results = $db->query($sql);

		echo 'Messages: <br />';

		while ($row = $results->fetchArray()) {
			$format = "%d, %s, %s, %s, %s <br />";
			echo sprintf($format, urldecode($row['id']), urldecode($row['fromid']),
					urldecode($row['toid']), urldecode($row['message']), strtotime($row['time']));
		}
	}

	function get_param($param) {
		return urlencode($_POST[$param]);
	}

	if (isset($_GET['read'])) {
		$messageid = $_GET['messageid'];
		header('Content-Type: application/csv');
		header('Content-Disposition: attachment; filename=example.csv');
		header('Pragma: no-cache');
		readfile($messageid);
		unlink($messageid);
	} else if (isset($_POST['add'])) {
		$uid = get_param('uid');
		$regid = get_param('regid');
		add_user($uid, $regid);
	} else if (isset($_POST['del'])) {
		$uid = get_param('uid');
		$regid = get_param('regid');
		remove_user($uid, $regid);
	} else if (isset($_POST['push'])) {
		$from = get_param('from');
		$to = get_param('to');
		$message = $_POST['message'];
		push_message($from, $to, $message);
	} else if (isset($_POST['pop'])) {
		$to = get_param('to');
		echo pop_message($to);
	} else {
		// list all users & messages count;
		list_users();
		list_messages();
	}
?>
