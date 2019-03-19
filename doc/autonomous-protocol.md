 # 自动驾驶通信协议文档

**1.0-version **

### ProtocolFormat

```
{
	"version":"0.1",
	"type":cmd,
	"ack":ack,
	"vin":,
	"requestId":  int,
	"data":{}
}

type:
    0x00: registration, // for the first login purpose, leave to implement.
    0x01: login
    0x02: heartbeat, // this command will be thrown in the near future.
    0x03: logout
    0x04: mode change, // convert to autonomous, or mannual driving mode. for driving rights assigned.
    0x05: module check, // use prepare and do autonomous instead, deprected this command
    
	0x11: real time autoTask info, // report autoTask status, including percentage and values.
	0x12: real time gps info, // use 0x15 instead
    0x13: stop autonomous,    // the client must do stop command.
    0x14: real time obstacle info, // 
    0x15: terminal info, // report terminal status periodically
    0x16: final autoTask info, // when the autoTask is terminated(fininshed or interrupted due to internal or external error), try to push autoTask's detail msg to server.

    0x20: prepare autonomous, // prepare autonomous, the client check the terminal status.
    0x22: fire lane-based autonomous, followed by joint-points.
	
ack:
    0x01: success,
    0x02: not login,
    0x03: illegal token.
    0x04: illegal protocol,
    0x05: repeated.
    0x06: not supported protocol
    0x07: server error,
    0xFE: command

requestId:
	请求编号，用于异步化处理
```



### Login

**Client->Server**

```
data: {
  "tripId": string,
  "nodeList": [
    string,
    string,
    ...
  ],
}
```

### heartbeat

**Client->Server**

### logout

**Client->Server**

#### Mode

**Server->Client**
**Request**

```
data:{
"mode": integer,
}
mode:
	0 means manual drive,
	1 means autonomous
```

### Module Check

**Server->client**
**Response**



to check

```
data: {
	"gpsState": integer,
	"canState": integer,
	"cameraState": integer,
}
state:
	0: ok,
	1: not started,
	2: device not found,
	3: device error
	-1: INVALID
```

### 0x11 Real time Task info

**Client->Server**

**Request**

```
data:{
	"taskId": "",
    "status": integer, // finished, or running or interrupted, or aborted.
    "offset": integer, // offset-th action , from 0
    "precentage": integer, // current action id, precentage, 0~100
}

status:
    0x08 autoTask finished,
    0x03 autoTask running,
    0x10 autoTask stopped due to Obstacle,
    0x18 autoTask stopped due to device error. // extend in future.
```

### GPS Info

@deprecated 

**Client->Server**

**Request**
```
data:{
	"timestamp": float,
	"longitude": float,
	"latitude": float,
	"altitude": float,
  "status": integer
}
```

__Response__

```
data: {
}
```
#### Terminal Info

**Client->Server**
**Request**

```
data: {
  "timestamp": long long, // MilliSecond.
  "pose": {
      "point": {
          "x": double,
          "y": double,
          "z": double,
          "status": integer
      }
      "orientation": {
          "roll": double,
          "pitch": double,
          "yaw": double,
          "status": integer 
      }
      // "status": integer
      // 0: estimated status, 用于车道级粗精度定位, 根据航位推算得到
      // 1: fixed, 依据参考点，定点定位得到
  },
  "v": ,
  "wheelAngle": ,
  "gear": ,
  "nodes": [{
    "name": string,
    "status": integer,
    "type": integer
  }, {
    "name": string,
    "status": integer,
    "type": integer
  }, ...
  ]
}

status: 
	0, // refer health
	1, // module not started
	2, // module device not found
	3, // module device error
	4, // module error
```

### 0x16 Final Task Info

**client->server**

**Request**

```
data: {
    "taskId": string, // refer to taskId
    "state": integer, // refer to 0x11 TaskState, running 3, FINISHED 8, INTERRUPTED 16, DEVICE_INTERRUPTED 24, TASK_KILLED 49
    "actions": [
        {
            "index": integer, // action's index, from 0
            "type": string, // LANE, TRJECTORY, REPLAY
            "startStamp": long, // action start timestamp, milliseconds
            "endStamp": long, // action end timestamp, milliseconds
            "state": integer // action's execution state, refer to 0x11 taskState, running 3, FINISHED 8, INTERRUPTED 16, DEVICE_INTERRUPTED 24,  TASK_KILLED 49
        }, {
            "index": integer,
            "type": string,
            "startStamp": long,
            "endStamp": long,
            "state": integer
        }
    ]
}
```

#### 0x13 Stop Autonomous

**Server->Client**

#### Prepare Autonomous

**Response**
```
ack: success or failed.

data: {
  "nodes": [{
    "name": string,
    "status": integer,
    "type": integer
  }, {
    "name": string,
    "status": integer,
    "type": integer
  }, ...
  ]
}
```

### fire lane-based autonomous

**Request**

```
data: {
  "taskId": string,
  "speed": double, 
  "route": [{
      	"outset": {
        	"name": string, 
        	"x": double,
        	"y": double,
      	}
        "goal": {
            "name": string,
            "x": double,
            "y": double,
        }
      	"v": double,
      	"type": str,
      	"points": if type equals to TRJ.
      	"instructions": if type equals to REPLAY.
    	}, {
       "outset": {
         "name": string, 
         "x": double,
         "y": double,
       }
       "goal": {
         "name": string,
         "x": double,
         "y": double,
       }
       "type": str,
       "points": [] if type equals to TRJ.
       "instructions": [] if type equals to REPLAY.
     }, 
  ]
}

type:
  LANE: followed nothing.
  REPLAY: followed by instructions
    frequency: int // default period: 10ms.
    instructions: [
      {
        "motor": double,
        "servo": double,
        "shift": integer,
        "brake": "",
      },
    ]
   TRJ: followed by waypoints
    points:[
       {
          "x": "",
          "y": "",
          "t": "",
          "v": "",
       },{
          "x": "",
          "y": "",
          "t": "",
          "v": ""
       }, ...
    ]
    SITE: site 
    <site fromQr="" fromBaseX="" fromBaseZ="" toQr="" toBaseX="" toBaseZ="" scene="" curveRadius="" curveAngle="" speed="" shift="">
    </site>
```
**Response**

refer to default response.