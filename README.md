# Ripple Layout Animation
It's a custom layout to create a ripple animation

## Gradle:

Gradle.build

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
  
Gradle.app

```
dependencies {
  implementation 'com.github.linhcn:rippleanimation:Tag'
}
```

## Integration:

<xml>
  
    <com.linhcn.ripplelayout.RippleLayout
        android:id="@+id/ripple_bg"
        android:layout_width="250dp"
        android:layout_height="250dp"
        app:rl_color="@android:color/white"
        app:rl_radius="32dp"
        app:rl_rippleAmount="5"
        app:rl_duration="5000"
        app:rl_shadow="10"
        app:rl_scale="4">

        <ImageView
            android:layout_width="70dp"
            android:layout_height="70dp"
            android:scaleType="fitCenter"
            android:src="@drawable/ic_baseline_account_circle"
            android:layout_centerInParent="true"
            android:id="@+id/centerImage"
            tools:ignore="ContentDescription" />
    </com.linhcn.ripplelayout.RippleLayout>
   
 <xml>
  

## Example:

![Example](https://github.com/linhcn/rippleanimation/blob/master/example.gif)
