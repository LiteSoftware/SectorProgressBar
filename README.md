# SectorProgressBar
Sector progress bar
You can visually see the use of this library on [Google Play](https://play.google.com/store/apps/details?id=com.litesoftteam.phonecleaner)

[![](https://jitpack.io/v/LiteSoftware/SectorProgressBar.svg)](https://jitpack.io/#LiteSoftware/SectorProgressBar)

<img src="screens/1_1.png" width="30%" /> <img src="screens/1_2.png" width="30%" /> <img src="screens/1_3.png" width="30%" />

## How to use
Add view to your layout:
```xml
<com.javavirys.sectorprogressbar.SectorProgressBar
    android:id="@+id/progressView"
    android:layout_width="190dp"
    android:layout_height="190dp"
    app:spbCenterBackgroundImage="@drawable/ic_radial_circular_progress_background"
    app:spbCenterForegroundImage="@drawable/ic_rocket"
    app:spbOuterCircleWidth="4dp"
    app:spbProgress="60" />
```

Than find it in code and set progress:
```kotlin
val progressView = findViewById<SectorProgressBar>(R.id.progressView)
// you can set current progress value
progressView.setProgress(value)
// you can get progress value using following getter
progressView.getProgress()
```

---

### Download using Gradle

Add this in your root `build.gradle` at the end of `repositories` in `allprojects` section:
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Then add this dependency to your **module-level** `build.gradle` in `dependencies` section:
```groovy
implementation 'com.github.LiteSoftware:SectorProgressBar:$version'
```

---

### License

```
   Copyright 2021 Vitaliy Sychov. All rights reserved.

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
