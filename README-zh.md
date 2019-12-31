# java-utils #
[English](README.md) | Chinese
----------------------------------
## 支持环境 ##
>**提示:** 目前，该项目尚未完成兼容性测试，此兼容性数据仅供参考。   
- Base:
  - Java：1.8.0或更高版本
- Encrypt:
  - Java：1.8.0_161或更高版本(或对Java安装JCE.policy以解除对AES256及以上的限制)
- Event:
  - Java：1.8.0或更高版本

## 更新日志 ##
> 提示：每个版本都有对应的tag
- `1.0.0` - 2019/12/25
    - 第一个稳定版本, 增加了一些工具类（详见README - [工具](#工具)章节）

## 介绍 ##
### 这个项目的用途？ ###
这个项目的目的是想将一些在Java开发中经常用到的全部总结在一个库内，避免“同一个轮子造了很多次”的情况出现。

## 工具 ##
### base ###
包路径：`net.lamgc.utils.base`  
介绍：存放着一些基础工具类.  

- `ArgumentsProperties` - 用于解析参数列表以生成参数键值对, 可以快速的查找参数.

### encrypt ###
包路径：`net.lamgc.utils.encrypt`  
介绍：Encrypt Utils包含了AES加解密, RSA加解密和签名等与加密有关的工具类, 所有工具类均以Java自带加密套件为基础开发.  

- `AESEncrypt` - AES加解密相关类.
- `DiffieHellmanEncrypt` - DiffieHellman密钥交换算法类.
    - 共享密钥所支持的加密算法类型：
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
- `MessageDigestUtils` - 消息摘要算法工具类.
    - 支持的消息摘要算法：
        - MD2
        - MD5
        - SHA-1
        - SHA-256
        - SHA-384
        - SHA-512
- `RSAEncrypt` - RSA加解密工具类.
- `RSAEncryptWithAES` - RSA联合AES的长数据加解密工具类.
- `RSASign` - RSA签名工具类.
    - 支持的签名算法：
        - MD2
        - MD5
        - SHA-1
        - SHA-224
        - SHA-256
        - SHA-384
        - SHA-512

### event ###
包路径：`net.lamgc.utils.event`
介绍：Event Utils是一套简单, 泛用的事件系统, 可以快速的为项目添加灵活的事件功能, 该事件系统以Bukkit API为原型开发.  

- `BasicEventHandlerList` - 内置的默认EventHandlerList.
- `EventExecutor` - 事件执行器, 用于投递事件到事件方法中.
- `EventHandler` - 实现事件处理器所需的空接口, 用于标识某个类包含事件方法.
- `EventObject` - 实现事件所需的空接口, 用于标识某个类为事件对象, 作为参数传递给事件方法.
- `EventHandlerList` - 存储事件方法的List接口, 可实现该接口来改变存储方式.
- `EventHandlerObjectMap` - 存储EventHandler对象的Map接口.
- `EventInvokeException` - 用于包装事件方法抛出异常的信息, 转交给EventUncaughtExceptionHandler处理.
- `EventUncaughtExceptionHandler` - 用于处理事件方法抛出异常的异常处理接口.
- `HashHandlerObjectMap` - 内置的默认EventHandlerObjectMap, 通过HashMap实现

## LICENSE / 版权许可 ##
本项目遵行`Apache 2`许可证开源：
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
