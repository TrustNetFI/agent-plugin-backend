# 1. Introduction
Java backend for TrustNet browser plugin
## 1.1 Requirements
1. Access to Indy pool
2. libIndy v1.3.1
# 2. API
## 2.1 Create new account
Creates new account. Sets up user wallet and cretes first  `did`.
```json
{"operation" : "CREATE_ACCOUNT", "params" :{"username" : "xxxx", "password" : "yyyy"}} 
```
Example response: 
```json
{"status":"SUCCESS","value":{"username":"xxxxxx","walletname":"eb7a5aaf-9dfa-4e61-b5ed-eff9aaae2d8d"}}
```
## 2.2 Login to account
Logs in to account, returns `token`
```json
{"operation" : "LOGIN", "params" :{"username" : "xxxxxx", "password" : "yyyy"}}
```
Example response:
```json
{"status":"SUCCESS","value":{"token":"66bd38e4-0e8d-46af-8b5c-2abada5f7eb7"}}
```

## 2.3 Create new DID
Creates new `DID`
```json
{"operation" : "CREATE_DID", "params" :{"token" : "66bd38e4-0e8d-46af-8b5c-2abada5f7eb7"}}
```
Example response:
```json
{"status":"CREATED","value":{"did":"MsHajCPU2KjqVTYzgSYmwH","verkey":"CNeXKuzqdJMxbeC6sZHZCGyDjARqwQgkMaRTSX8CJ1p9"}}
```

## 2.4 Sign data
Signs data.
Example request:
```json
{"operation" : "SIGN_DATA", "params" :{"token" : "66bd38e4-0e8d-46af-8b5c-2abada5f7eb7", "did" : "MsHajCPU2KjqVTYzgSYmwH", "datatosign" :"testdata"}}
```
Example response:
```json
{"status":"SUCCESS","value":{"dataToSign":"testdata","wallet":"eb7a5aaf-9dfa-4e61-b5ed-eff9aaae2d8d","signature":"laOJ6ACNnBcRIwG+OtG1CE7FWtGK3WCiyndYZ84SN/KIjX5aMQpeyJ7ryfMiZ/0lrCE57jzcP3JC09MXCiHQAQ==","verkey":"CNeXKuzqdJMxbeC6sZHZCGyDjARqwQgkMaRTSX8CJ1p9","did":"MsHajCPU2KjqVTYzgSYmwH"}}
```
## 2.5 Verify signature
Verifies signature.
Example command:
```json
{"operation" : "VERIFY_SIGNATURE", "params" :{"token" : "66bd38e4-0e8d-46af-8b5c-2abada5f7eb7", "did" : "MsHajCPU2KjqVTYzgSYmwH", "message" :"testdata", "signature" :"laOJ6ACNnBcRIwG+OtG1CE7FWtGK3WCiyndYZ84SN/KIjX5aMQpeyJ7ryfMiZ/0lrCE57jzcP3JC09MXCiHQAQ=="}}
```
Example response:
```json
{"status":"SUCCESS","value":{"verified":true,"message":"testdata","signature":"laOJ6ACNnBcRIwG+OtG1CE7FWtGK3WCiyndYZ84SN/KIjX5aMQpeyJ7ryfMiZ/0lrCE57jzcP3JC09MXCiHQAQ==","did":"MsHajCPU2KjqVTYzgSYmwH"}}
```

## 2.6 Log out
Logs out and invalidates the token.
Example command:
```json
{"operation" : "LOGOUT" , "params" :{"token" : "66bd38e4-0e8d-46af-8b5c-2abada5f7eb7"}}
```
Example response:
```json
{"status":"SUCCESS","value":{"loggedout":true}}
```


# 3. Standalone
Make sure `RUN_AS_EXTENSION = false` in `Configuration.java`. Check that the `NETWORK_NAME` in `Configuration.java`
matches with your Indy pool configuration.

If `CREATE_USER`or `LOGIN` commands seem to hang decrease `workload`parameter in `userstore.java`.


# 4. Browser extension
Make sure `RUN_AS_EXTENSION = true` in `Configuration.java`.' Check that the `NETWORK_NAME` in `Configuration.java`
matches with your Indy pool configuration.

Build JAR and make it executable or create script that runs the JAR.
## 4.1 Chrome configuration
The following is for enabling for individual user.'
Create file `fi.trustnet.browserplugin.json file` to `~/.config/google-chrome/NativeMessagingHosts' directory.
File contents:
```json
    {
     "name": "fi.trustnet.browserplugin",
     "description": "TrustNet browser plugin backend",
     "path": "PATH_TO_EXECUTABLE_JAR",
     "type": "stdio",
     "allowed_origins": [
       "chrome-extension://YOUR_EXTENSION_ID/"
     ]
    }
```


## 4.2 Usage with Chrome
### 4.2.1 Demo
directory `example-app` contains example Crome App that can communicate with the native extension. To install it:
1. Open Chrome
2. go to `chrome://extensions/`
3. Make sure Developer mode is enabled
4. Select Load unpacked
5. No the extension ID, make sure it is same as in `fi.trustnet.browserplugin.json` file, if not change the id in the file.
6. Navigate to example-app directory and open it
7. Go to `chrome://apps` and select TrustNet Demo

### 4.2.1 Extension-background-native communication

In background script have something like 
```code
let port = null
...
chrome.runtime.onMessage.addListener((request) =>{
    if(request.operation==="CONNECT" {
        let hostName="fi.trustnet.browserplugin"
        port = chrome.runtime,connectNative(hostName)
        port.onMessage.addListener(onNativeMessage)
        port.onDisconnect.addListener(onDisconnected)
    }
    if(request.operation==="CREATE_ACCOUNT){
       port.postMessage(request)
    }
    //Other messages
    
}

const onNativeMessage = (message) => {
    chrome.runtime.sendMessage({from : "background", 
                                subject: "message",
                                message: "message"})
                                
}

const onDisconnected = () => {
    port=null
    chrome.runtime.sendMessage({from : "background", 
                               subject: "message",
                               message: "DISCONNECTED"})
                                                              
}
```

Code in Extension side:
```code
chrome.runtime.sendMessage(EXTENSION_ID, MESSAGE}
```
Examples:
```code
const connect=()=>{
   chrome.runtime.sendMessage("kjhkdhldjldwlmlcwdd", {operation: "CONNECT"})
}
const createAccount=(username, password) => {
   chrome.runtime.sendMessage("kjhkdhldjldwlmlcwdd",{"operation" : "CREATE_ACCOUNT", "params"} :{"username" : username, "password" : password}} 
}
```
Receive messages from background
```code
chrome.runtime.onMessage.addListener((message, sender)=> {
    if(sender==="background") {
    //handle messages from native extension
    }
}
```
