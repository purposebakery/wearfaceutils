# Wear Face Utils

Helps to calculate a Point on a genric Android Wear Watchface.

## Usage ##

Inside your [CanvasWatchFaceService.Engine](https://developer.android.com/reference/android/support/wearable/watchface/CanvasWatchFaceService.Engine.html) call:

```java
@Override
public void onApplyWindowInsets(WindowInsets insets) {
    super.onApplyWindowInsets(insets);
    WearFaceUtils.INSTANCE.init(insets);
}
```

Then use the actual method to calculate your point(s). No need to define your watch face format. One method to rule them all :) 

```java
@Override
public void onDraw(Canvas canvas, Rect bounds) {
    int outerMarginPx = 30; // your margin from the outer boundary of your (generic) watchface.
    double angle = Math.PI / 2; // angle starting at 3 o Clock. 
    Point pointOnFace = WearFaceUtils.INSTANCE.pointOnFace(outerMarginPx, angle, bounds);
    // use your point to draw stuff to the canvas ;)
}
```

![Circle Face](https://github.com/techlung/wearfaceutils/blob/master/circle.png=100x100)

![Chin Face](https://github.com/techlung/wearfaceutils/blob/master/chin.png=100x100)

![Square Face](https://github.com/techlung/wearfaceutils/blob/master/square.png=100x100)

Download
--------

coming soon...

License
-------

    Copyright 2017 Oliver Metz
        
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        
       http://www.apache.org/licenses/LICENSE-2.0
              
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




