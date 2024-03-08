# CloudstreamApi

[![](https://jitpack.io/v/Blatzar/CloudstreamApi.svg)](https://jitpack.io/#Blatzar/CloudstreamApi)

A minimal subset of the Cloudstream Provider API used to allow local compilation and testing. 
Currently work in progress and may not work with all providers. 
Please make an issue if there are any useful missing methods from the main app.

## Getting started

### Setup

In your build.gradle.kts:

```kt
    dependencies {
        val apkTasks = listOf("deployWithAdb", "build")
        val useApk = gradle.startParameter.taskNames.any { taskName ->
            apkTasks.any { apkTask ->
                taskName.contains(apkTask, ignoreCase = true)
            }
        }

        val implementation by configurations
        val apk by configurations

        // If the task is specifically to compile the app then use the stubs, otherwise us the library.
        if (useApk) {
            // Stubs for all Cloudstream classes
            apk("com.lagradost:cloudstream3:pre-release")
        } else {
            // For running locally
            implementation("com.github.Blatzar:CloudstreamApi:0.1.6")
        }

        // Rest of your code here...
   }
```

### Testing locally

Running it is as simple as defining a main method and running it by clicking the green arrow in Android Studio.
![image](https://github.com/Blatzar/CloudstreamApi/assets/46196380/bf37cd84-5fa1-40f6-9a54-b53d865cbd3a)

```kt
suspend fun main() {
    val providerTester = com.lagradost.cloudstreamtest.ProviderTester(YourProvider())
    providerTester.testAll()
}
```

Using the ProviderTester it becomes easy to test all methods at once, or just test individual methods with printing.
It is mainly to prevent a lot of boilerplate code in every provider.

Note that you can run the providers locally too, without using ProviderTester:

```kt
suspend fun main() {
    val responses = YourProvider().search("Query")
    println(responses)
}
```

An automated testing system, or testing all providers does not exist currently, but is likely to come in the future.
