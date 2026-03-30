# emlibrary
ui工具帮助类

1.确保项目gradle文件中有如下依赖：
maven { url = uri("https://jitpack.io") }

2.模块目录下配置gradle（可替换最新版本）：
implementation("com.github.Harbor2:Emlibrary:v1.0.8")

3.使用时需要对EMLibrary做初始化处理：
EMLibrary.init(this)

4.手动下载aar 替换自己所需版本号
https://jitpack.io/com/github/Harbor2/Emlibrary/v2.1.0/Emlibrary-v2.1.0.aar

5.jitpack官网：https://jitpack.io/



## 发布步骤

1.新建app工程
2.File -> new Module
3.处理Module中的冗余文件：
    plugins {
        id("com.android.library")
        id("maven-publish")
        // 如需要android则添加
        id("org.jetbrains.kotlin.android")
    }
    
    defaultConfig {
        // 配置最小SDK版本
        minSdk = 24
    }

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    from(components["release"])
                    groupId = "com.github.Harbor2"
                    artifactId = "xxxx"
                    version = "1.0.1"
                }
            }
        }
    }

4. 根目录build.gradle中需要有application和library插件
   plugins {
        id("com.android.application") version "8.13.2" apply false
        id("com.android.library") version "8.13.2" apply false
        id("org.jetbrains.kotlin.android") version "2.2.0" apply false
   }
    
    