#### 自定义的富文本组件
一个基于`Android`原生的`EditText`的富文本组件，支持文字输入，可以插入图片，并且会根据自动调整图片大小，宽度适应控件的宽度，高度等比例伸缩，图片支持本地图片和网络图片。
##### 使用方法
该组件使用了一个开源的图片加载库`ImageLoader`，用于图片的同步和异步加载。所以使用该组件需要导入第三方库`ImageLoader`。然后将`RichEditText.java`拷贝自己的工程里，将需要使用富文本的地方替换为`RichEditText`。
* 输入文字和普通的`EditText`没有区别，主要是在插入图片的时候要进行处理
* 插入图片时，需要将图片转化为`Bitmap`，然后指定其存储路径，调用`addImage(bitmap,filePath)`，如下面的代码所示：
```
originalBitmap = ImageLoader.getInstance().loadImageSync(originalUri.toString());
contentRichEditText.addImage(originalBitmap,	getAbsoluteImagePath(originalUri));
```
* 获取富文本的内容可以调用`getRichText`也可以调用`getText().toString`

##### 其他功能
有时候我们可能需要对已经保存的文本进行编辑，那么就可以调用`setRichEditText(content)`，content中的图片路径可以是本地路径，也可以是网路路径，但是一定要是`<img src="path"/>`的格式.

##### 例子
详细使用方法可以参照`sample`下的例子，下面是例子的截图
<p>
<img src="https://raw.githubusercontent.com/KDF5000/RichEditText/master/screenshot/1.jpg" alt="1" width="200px" weight="100px" />
<img src="https://raw.githubusercontent.com/KDF5000/RichEditText/master/screenshot/2.jpg" alt="2" width="200px" weight="100px" />
<img src="https://raw.githubusercontent.com/KDF5000/RichEditText/master/screenshot/3.jpg" alt="3" width="200px" weight="100px" />
<img src="https://raw.githubusercontent.com/KDF5000/RichEditText/master/screenshot/4.jpg" alt="4" width="200px" weight="100px" />
<img src="https://raw.githubusercontent.com/KDF5000/RichEditText/master/screenshot/5.jpg" alt="5" width="200px" weight="100px" />
