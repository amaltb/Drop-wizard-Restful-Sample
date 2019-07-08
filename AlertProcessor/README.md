# AlertProcessor Service
  Alert processor service is is a micro-service indented to generate 
alert report based on selected criteria. This service follows Restful 
architecture and is written in Java using Drop-wizard framework. Alert 
instances are stored in the backed in a PostgreSql database.

## Architecture
#### Daigram
![Architecure Diagram](docs/images/Arch-diagram.png)

#### API Contract

(POST)

/api/alerts/report?startTime=&endTime=

**Headers:<br/>**
Content-Type = application/json<br/>
(Signing headers are to be added while enabling API Signing)

**Request Body Parameters:<br/>**
```
{
    "anomaly_template_alias" : anomaly template alias name (Required: True),
     "key_set": {
        "key1" : [value1, value2] (Optional True),
        "key2" : [value1, value2] (Optional True)
    }
}
```

#### ER Diagram
![ER Diagram](docs/images/ER-Diagram.png)

## Setup
To be filled.

## Sample Request & Response
**Request**
```
curl -X POST \
  'https://localhost:8443/api/alerts/report?startTime=2019-06-30%2023:53:49&endTime=2019-07-01%2022:53:49' \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: 7596d6d0-0f4e-41db-b57d-3b378fe1df2c' \
  -d '{
	"anomaly_template_alias": "flight alert template",
	"key_set": {
		"operation name" : ["GET"],
		"service name": ["Booking"]
	}
}'
```
**Response**
```
[
  {
    "alertHashId": "pqrst12345",
    "alertValue": 43.2,
    "createdOn": 1561994629000,
    "templateName": "abc template",
    "aliasName": "flight alert template",
    "alertType": {
      "alertTypeName": "error",
      "id": 1
    },
    "alertKeyValue": [
      {
        "id": 2,
        "key": "operation name",
        "value": "GET"
      },
      {
        "id": 1,
        "key": "service name",
        "value": "booking"
      }
    ],
    "algorithmDefinition": {
      "id": 1,
      "algoName": "standard deviation",
      "aliasName": "flight alert template",
      "algoParameters": [
        {
          "id": 2,
          "key": "direction",
          "val": "UP"
        },
        {
          "id": 1,
          "key": "band",
          "val": "2 weeks"
        }
      ]
    },
    "alertModelState": {
      "id": 1,
      "params": [
        {
          "id": 2,
          "key": "mean",
          "value": "25"
        },
        {
          "id": 1,
          "key": "percent diff",
          "value": "10%"
        }
      ]
    },
    "alertSubscriptions": [
      {
        "id": 2,
        "messageText": "This is a dummy alert message",
        "subscriptionChannels": [
          {
            "id": 4,
            "key": "sns",
            "value": "[]"
          },
          {
            "id": 5,
            "key": "slack",
            "value": "[doppler-devs, doppler]"
          },
          {
            "id": 3,
            "key": "email",
            "value": "[amalbabu53@gmail.com, ambabu@expedia.com]"
          }
        ]
      }
    ]
  }
]
```