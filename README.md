# emlibrary
ui工具帮助类

1.确保项目gradle文件中有如下依赖：
maven { url = uri("https://jitpack.io") }

2.模块目录下配置gradle（可替换最新版本）：
implementation("com.github.Harbor2:emlibrary:v1.0.8")

3.使用时需要对EMLibrary做初始化处理：
EMLibrary.init(this)
