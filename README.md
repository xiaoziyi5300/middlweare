#个人版open-fegin调用
## pom引用
```java
 <dependency>
            <groupId>zhenfei.liu</groupId>
            <artifactId>spring-boot-starter-my-openfegin</artifactId>
            <version>1.1.2</version>
  </dependency>
```
## interface api 示例
```java
@MyFeginClient(name = "server-provider")
public interface UserService {

    @RequestMapping(value = "/queryUser", method = RequestMethod.GET)
    User queryUser(@RequestParam("name") String name);

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    User select(@RequestParam("name") String name,@RequestParam("password") String password);

    @RequestMapping(value = "/selectIsExist", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    String selectIsExist(@RequestBody User user);

    @RequestMapping(value = "/selectMapToUser", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String,User> selectMapToUser(@RequestParam("name") String name,@RequestParam("age") Integer age);
}

```
### 使用@myFeginClient标注接口
## 开始open-fegin调用 
```java
@SpringBootApplication(scanBasePackages={"cn.com.lzf","zhenfei.liu"})
@EnableDiscoveryClient
@EnableMyFeginClients(basePackages = {"server.api"})
public class ServerClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerClientApplication.class, args);
	}
}
```
EnableMyFeginClients ## basePackages -> service interface 接口包名 可以多个
SpringBootApplication ## scanBasePackages ## zhenfei.liu ->当前项目 open-core包名
application·yml ## open.fegin.client.enable = true 开启功能



