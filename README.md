# java-utils #
English | [Chinese](README-zh.md)
----------------------------------
## Supporting environment ##
>**Note:** At present, the project has not completed the compatibility test, and this compatibility data is only for reference. 
- Base:
  - Java: 1.8.0 or Upper
- Encrypt:
  - Java: 1.8.0_161 or Upper (Or install JCE.policy on Java to lift restrictions on AES256 and above)
- Event:
  - Java: 1.8.0 or Upper

## Introduction ##
### The purpose of this project ###
The purpose of this project is to summarize all the frequently used in Java development in one library, to avoid the situation of "the same function has been developed many times in different projects".

## Utils ##
### base ###
Package path: `net.lamgc.utils.base`  
Introduction: Basic tools.  

- `ArgumentsProperties` - Used to parse the parameter list to generate parameter key-value pairs, which can quickly find parameters.

### encrypt ###
Package path: `net.lamgc.utils.encrypt`  
Introduction: Encrypt Utilscontains encryption-related tools such as AES encryption and decryption, RSA encryption and decryption, and signatures. All tool classes are developed based on Java's own encryption suite.  

- `AESEncrypt` - AES encryption and decryption related classes.
- `DiffieHellmanEncrypt` - DiffieHellman Key exchange algorithm class.
    - Type of encryption algorithm supported by the shared key:
        - AES
        - RC2
        - RC4
        - Blowfish
        - DES
        - DES-EDE
        - HmacMD5
        - HmacSHA1
        - HmacSHA256
        - HmacSHA384
        - HmacSHA512
- `MessageDigestUtils` - Message digest algorithm tool class.
    - Supported message digest algorithms:
        - MD2
        - MD5
        - SHA-1
        - SHA-256
        - SHA-384
        - SHA-512
- `RSAEncrypt` - RSA encryption and decryption tool class.
- `RSAEncryptWithAES` - Data encryption and decryption tool class combining RSA and AES.
- `RSASign` - RSA signature tool class.
    - Supported signature algorithms:
        - MD2
        - MD5
        - SHA-1
        - SHA-224
        - SHA-256
        - SHA-384
        - SHA-512
### event ###
Package path: `net.lamgc.utils.event`
Introduction: Event Utils is a simple, general-purpose event system that can quickly add flexible event functions to a project.The event system is similar to the event system of the Bukkit API.  

- `BasicEventHandlerList` - Built-in default EventHandlerList.
- `EventExecutor` - Event executor, used to post events to event methods.
- `EventHandler` - An empty interface required to implement an event handler, used to identify that a class contains event methods.
- `EventObject` - An empty interface required to implement events, used to identify a class as an event object, and passed to the event method as parameters.
- `EventHandlerList` - List interface for storing event methods, which can be implemented to change the storage method.
- `EventHandlerObjectMap` - Map interface that stores EventHandler objects.
- `EventInvokeException` - It is used to wrap the information thrown by the event method, and it is handed over to the EventUncaughtExceptionHandler.
- `EventUncaughtExceptionHandler` - An exception handling interface for handling event method exceptions.
- `HashHandlerObjectMap` - Built-in default EventHandlerObjectMap, implemented by HashMap.

## LICENSE ##
This project complies with the `Apache 2` license
```
   Copyright 2019 LamGC

   Licensed under the Apache License, Version 2.0 (the "License");
   You must not use the project and the files in the project except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
