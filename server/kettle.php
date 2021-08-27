<?php
include "config.php";

$db = mysqli_connect(
    $conf["server"], 
    $conf["user"], 
    $conf["password"],
    $conf["dbname"]
);


function request($sql) {
    global $db;
    $res = mysqli_query($db, $sql);
    if ($res === false)
        return [];
    else
        return mysqli_fetch_all($res, MYSQLI_ASSOC);
}

class Kettle {
    public $id = 0;
    public $room = 0;
    public $time = 0;
    public $volume = 0;
    function __construct() {
    }
}

function add($room) { // return id
    $id = request("SELECT MAX(id) FROM kettles")[0]["MAX(id)"];
    $check = request("SELECT COUNT(1) FROM kettles WHERE room=$room")[0]["COUNT(1)"];
    if ($check == 0) return 0; // === "0"

    $id = $id + 1;
    request("INSERT INTO kettles VALUES ($id, $room, 0, 0)");
    return $id;
}

function remove($id) {
    request("DELETE FROM kettles WHERE id=$id");
}

function boil($id, $boil_time, $volume) {
    request("UPDATE kettles SET time=$boil_time, volume=$volume WHERE id=$id");
}

function ask() {
    return join(
        "\n", 
        array_map(
            function ($str) {
                return "${str['id']} ${str['room']} ${str['time']} ${str['volume']}";
            },
            request("SELECT * FROM kettles")
        )
    );
}

function map() {
/*
    $rooms = join(
        " ",
        array_map(
            function ($str) { 
                return $str["room"];
            }, 
            request("SELECT room FROM kettles")
        )
    );
*/
    return file_get_contents("map.graph");
}
