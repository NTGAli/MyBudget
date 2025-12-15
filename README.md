# MyBudget
MyBudget is a comprehensive Android application engineered to empower users in managing their personal finances with precision and ease. This tool facilitates meticulous expense tracking, strategic budget allocation, and insightful financial analysis, all within a user-friendly interface designed for seamless navigation.

## Features

|Transactions|Report|Profile|
|:-----:|:-----:|:-----:|
|<img width="240" src="https://s6.uupload.ir/files/1_ql9i.png">|<img width="240" src="https://s6.uupload.ir/files/3_6azk.png">|<img width="240" src="https://s6.uupload.ir/files/android_large_-_37_rzh.png">|

|Expense|Income|Internal transfer|
|:-----:|:-----:|:-----:|
|<img width="240" src="https://s6.uupload.ir/files/android_large_-_9_em3l.png">|<img width="240" src="https://s6.uupload.ir/files/android_large_-_10_vdyx.png">|<img width="240" src="https://s6.uupload.ir/files/android_large_-_11_38h0.png">

## Development

### Required

- IDE : Android Studio Ladybug (or newer)
- JDK : Java 17 or higher
- Kotlin Language : 1.9.24

### Language

- Kotlin

### Architecture
- Modularization Based on [Now in Android](https://github.com/android/nowinandroid) with Clean Architecture


### Libraries

- AndroidX
  - Activity & Activity Compose
  - Core
  - Lifecycle & ViewModel Compose
  - Navigation
  - DataStore
  - Room

- Kotlin Libraries (Coroutine, Flow, Serialization)
- Compose
  - Material3
  - Navigation

- Dagger Hilt
- Retrofit
- Lottie
- Coil

  
#### Code Analyses
- Spotless
- Ktlint


#### Gradle Dependency

- Gradle Version Catalog



## Package Structure
The project follows a modular, feature-based package structure:
```
├── app
│   ├── MainActivity
│   └── Application
├── build-logic
├── core
│   ├── common
│   ├── data
│   ├── database
│   ├── datastore-proto
│   ├── datastore
│   ├── designsystem
│   ├── model
│   └── network
├── feature
│   ├── home
│   ├── login
│   ├── profile
│   ├── report
│   └── setup
├── sync
└── gradle
    └── libs.versions.toml
```

## Developer

|Ali Asghar Nateghi|Mahdi Jamshidi|
|:-:|:-:|
|<img src="https://s6.uupload.ir/files/105871528_p0uy.jpg" width=200>|<img src="https://s6.uupload.ir/files/101273209_zlop.jpg" width=200>|
|[@NTGAli](https://github.com/NTGAli)|[@Mahdi-Jamshidi](https://github.com/Mahdi-Jamshidi)|
