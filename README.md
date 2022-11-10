### Compressor

`教学`用，基于哈夫曼树的文件压缩器


### 用法

```java
Compressor compressor = new Compressor("file");
compressor.compress("zip.dat");
DeCompressor deCompressor = new DeCompressor("zip.dat");
deCompressor.decompress("file2");
```

### 作者

熊爷