pinyin-text-view 
========================

Pinyin-text-view is a library to display pinyin and text to users.

Requirements
-----------------------
Android 2.2+ 

Using pinyin-text-view library
-----------------------
### Step 1. ###
* If you are using `Android Studio`:<br>
  Add the following line to the dependencies section of your `build.gradle` file:
  ```gradle
  compile 'com.uudove.lib:pinyin-text-view:X.X.X'
  ```
  where `X.X.X` is the your preferred version. (Now there is no release)

* If you are using `Eclipse`:
  Download the latest release [here][], and add the jar to `libs` directory.
  [here]: https://github.com/titanseason/pinyin-text-view/raw/master/doc/release/pinyin-text-view-release-1.0.0.jar

### Setp 2. ###
  Define `PinyinTextView` in layout xml. If you don't understand what are the attributes mean, see [How this porject is designed](#How this porject is designed.)
```xml
  <com.uudove.pinyin.widget.PinyinTextView
      android:id="@+id/pinyin_text_view"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:background="#e0e0e0"
      android:horizontalSpacing="10dp"
      android:textColor="#000000"
      android:textColorHint="#666666"
      android:textSize="20sp"
      android:verticalSpacing="10dp" />
```

### Setp 3. ###
  Find `PinyinTextView` in Activity or Fragment, adn set the data to display.
```java
  mPinyinTextView = (PinyinTextView) findViewById(R.id.pinyin_text_view);
  
  List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>();
    pairList.add(Pair.create("你", "nǐ"));
    pairList.add(Pair.create("在", "zài"));
    pairList.add(Pair.create("哪", "nǎ"));
    pairList.add(Pair.create("？", " "));
    pairList.add(Pair.create("我", "wǒ"));
    pairList.add(Pair.create("在", "zài"));
    pairList.add(Pair.create("家", "jiā"));
    pairList.add(Pair.create("，", " "));
    pairList.add(Pair.create("很", "hěn"));
    pairList.add(Pair.create("高", "gāo"));
    pairList.add(Pair.create("兴", "xìng"));
    pairList.add(Pair.create("认", "rèn"));
    pairList.add(Pair.create("识", "shi"));
    pairList.add(Pair.create("你", "nǐ"));
    pairList.add(Pair.create("！", " "));
    
  mPinyinTextView.setPinyinText(pairList);
  
```
  
How this porject is designed.
-------------------------
![](https://github.com/titanseason/pinyin-text-view/raw/master/doc/image/pinyin-text-view.jpg)  
