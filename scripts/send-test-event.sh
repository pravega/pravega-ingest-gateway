#!/usr/bin/env bash
curl -v \
--header "Content-Type: application/json" \
--request POST \
--data '
{
"message_id": "40e4517d-3aff-480a-b872-cb8f039b2807",
"timestamp": 1588203203990,
"device_id": 23456,
"temperature": 30.96,
"humidity": 90.2
}
' \
http://localhost:3000/data/my_routing_key_1
