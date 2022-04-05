# 简单易用的网络框架

* 原作者项目地址：[Github](https://github.com/getActivity/EasyHttp)

#### 集成步骤

*  Gradle 依赖方法

```groovy
 repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
}
```

* 配置完远程仓库后，在项目 app 模块下的 `build.gradle` 文件中加入远程依赖

```groovy

dependencies {
    // 网络请求框架：https://github.com/xihan123/EasyHttp
    implementation 'com.github.xihan123.IDEA-EasyHttp:EasyHttp-11:11.0'
    // OkHttp 框架：https://github.com/square/okhttp
    implementation 'com.squareup.okhttp3:okhttp:<okhttp-version 4.x-5.x>'
}
```
*  Maven 依赖方法

```maven
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
	    <groupId>com.github.xihan123.IDEA-EasyHttp</groupId>
	    <artifactId>EasyHttp-11</artifactId>
	    <version>11.0</version>
	</dependency>



```


* 需要注意的是：框架支持 IDEA Java/Kotlin 的项目集成
            
## [框架的具体用法请点击这里查看](HelpDoc.md)


## License

```text
Copyright 2019 Huang JinQun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```