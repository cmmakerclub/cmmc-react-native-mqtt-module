# React Native CMMC

## Installation
```sh
npm install react-native-cmmc --save
```

### Installation (iOS)
not now

### Installation (Android)
```gradle
...
include ':react-native-cmmc'
project(':react-native-cmmc').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-cmmc/android')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
    ...
    compile project(':react-native-cmmc')
}
```

* register module (in MainActivity.java)

```java
import v5.cmmc.*;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new RTCCMMCPackage())              // <------ add here
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ExampleRN", null);

    setContentView(mReactRootView);
  }

  ......
}
```

### Screencasts

## Usage

### Example
```js
var React = require('react-native');
var {
    StyleSheet,
    View,
    Image
} = React;

```


