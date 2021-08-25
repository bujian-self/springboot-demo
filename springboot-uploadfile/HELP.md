# springboot 项目 第十四次 搭建

* 搭建文件上传

1. `application.yml` 添加 配置
```yaml
spring:
  servlet:
    # 项目启动时删除旧文件夹，默认为 true
    delete-before-starting: false
    multipart:
      # 开启文件上传支持，默认为 true
      enabled: true
      # 文件写入磁盘的阀值，默认为 0
      file-size-threshold: 0B
      # 所有请求最大值
      max-request-size: 50MB
      # 单文件最大值
      max-file-size: 10MB
      # 文件是否延迟解析，默认为 false
      resolve-lazily: false
```

2. 添加`UploadFile.java`
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile {
    private String fileName;
    private String url;
}
```

3. 添加`FileStorageService.java`接口和实现
```java
/**
 * 文件操作
 * @author bujian
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path path = Paths.get("fileStorage");

    @Override
    public void init() throws IOException {
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.err.println(e + " (FileStorageServiceImpl.java:33)");
        }
    }

    @Override
    public void save(MultipartFile multipartFile) throws IOException {
        Files.copy(multipartFile.getInputStream(),this.path.resolve(multipartFile.getOriginalFilename()));
    }

    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path file = path.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        Assert.isTrue(resource.exists() || resource.isReadable(),"Could not read the file.");
        return resource;
    }

    @Override
    public Stream<Path> load() throws IOException {
        return Files.walk(this.path,1).filter(path -> !path.equals(this.path)).map(this.path::relativize);
    }

    @Override
    public void clear(String filename) {
        FileSystemUtils.deleteRecursively(path.resolve(filename).toFile());
    }

    @Override
    public void clearAll() {
        FileSystemUtils.deleteRecursively(path.toFile());
    }
}
```

4. 添加`FileUploadConfig.java`启动配置
```java
@Component
public class FileUploadConfig implements CommandLineRunner {

    @Value("${spring.servlet.delete-before-starting:true}")
    private boolean deleteBeforeStarting;

    @Autowired
    FileStorageService fileStorageService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(
                        " ___         ___          __        __        __              ___    \n" +
                        "|__  | |    |__     |  | |__) |    /  \\  /\\  |  \\    | |\\ | |  |     \n" +
                        "|    | |___ |___    \\__/ |    |___ \\__/ /~~\\ |__/    | | \\| |  |  ...\n" +
                        "                                                                     \n" +
                        "version: 1.0.0,\tFont Name: JS Stick Letters\n" +
                        "banner: http://patorjk.com/software/taag/\n" +
                        "GitHub：https://github.com/ramostear/springboot2.0-action"
        );
        if (deleteBeforeStarting) {
            fileStorageService.clearAll();
        }
        fileStorageService.init();
    }
}
```

---

### [springboot 项目 第十三次 搭建](https://github.com/bujian-self/springboot-demo/blob/main/springboot-quartz/HELP.md)
