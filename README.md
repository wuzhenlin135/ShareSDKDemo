# 第三方登录分享库
--------------------------------
### 第三账号申请配置相关
keystore 创建及查看签名md5
- *生成debug 和 release 签名 方便调试。* -

1.创建debug keystore
``` shell
keytool -genkey -alias androiddebugkey -keypass android -keyalg RSA -keysize 1024 -validity 3650 -keystore key_debug.jks -storepass android -dname "CN=dn, OU=xx, O=xx, L=xx, ST=xx, C=xx"
```
2.拷贝创建 release keystroe
``` shell
copy key_debug.jks key_release.jks
```
3.修改release keystroe password、 alias、 alias password
``` shell
1.修改storepassword· 输入原始密码（android）、和新密码
keytool -storepasswd -keystore key_release.jks
2.修改keystore的alias ,<newalias> 为要设置的别名
keytool -changealias -keystore key_release.jks -alias androiddebugkey -destalias <newalias>
3.修改alias的密码，<newalias> 上一步设置的别名
keytool -keypasswd -keystore key_release.jks -alias <newalias> 
```
4.查看签名MD5、SHA
``` shell
keytool -v -list -keystore key_release.jks
```
5.将MD5配置到对应的后台

debug key 用于调试、release key 用于发布版本。
两个key的签名是一样的。
debug keystroe设置成eclipse的debug key用于调试。
androidstudio 可在gradle中配置。
``` shell
  signingConfigs {
        debug {
            storeFile file("../key_debug.jks") 
            storePassword 'android' 
            keyAlias 'androiddebugkey'
            keyPassword 'android' 
        }
        // * 替换成对应的alias passowrd
        release {
            storeFile file("../key_release.jks")
            storePassword ***
            keyAlias ***
            keyPassword ***
        }
    }
    
    buildTypes {
        release {
            minifyEnabled false
            signingConfig signingConfigs.release
        }

        debug {
            minifyEnabled false
            signingConfig signingConfigs.debug
        }
    }
```
