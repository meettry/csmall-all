# 酷鲨商城微服务架构README

## TODO LIST

- 其他接口调整

  

## 项目的安装与使用

### 下载

从gitee下载项目 pull

### 运行 sentinel

在doc/sentinel中运行bat文件

### 启动项目

数一数一共是11个项目运行,建议运行顺序

leaf>product=ams=ums=order=front=sso=resource=seckill=search>gateway

启动后,到nacos查看一下注册信息

http://106.75.107.221:8848/nacos

登录名,密码 nacos/nacos

### 接口文档

根据2022年3月25日网关doc文档入口访问网关工程即可看到所有接口

### 注意事项

第四阶段成恒还在修改,和第五阶段接口对不上很正常,到时候合并一下分枝即可

## 更新日志

### 2022年5月2日

- 【新增】添加秒杀系统时间毫秒返回值
- 【修改】修改mall-leaf中数据库参数

### 2022年4月17日

- 【修改】订单新增,多条商品失败,调整sql语句为 一个insert多个values 而不是多个insert语句

### 2022年4月15日

- 【新增】ums修改密码功能
- 【新增】ums地址查询
- 【新增】ums地址删除
- 【新增】ums地址新增
- 【新增】ums地址更新

### 2022年4月14日

- 【新增】用户详情查询
- 【新增】用户详情新增
- 【新增】用户详情更新

### 2022年4月11日晚

- 【修改】mall-resource的配置文件修改

### 2022年4月11日

- 【修改】Front前台工程添加权限限制 permitAll
- 【修改】搜索工程添加权限控制,hasRole("ROLE_user")
- 【修改】订单工程添加权限控制,hasRole"ROLE_user"))
- 【修改】秒杀工程添加权限控制,hasRole("ROLE_user")) 和 permitAll()
- 【修改】JwtTokenUtils调整 当无法解析token的时候不会抛异常,而是返回空
- 【修改】调整过滤器,如果登陆者信息是空的,就做一个空的authentication对象,而不是抛异常

### 2022年4月9日

- 【修改】FrontCategoryEntity 修改字段isParent 为parent 带is属性全部去除
- 【修改】SSOWebSecurityConfig 添加跨域解决的cors配置
- 【修改】修改user注册之后无法登录的问题

### 2022年4月7日

- 【修改】sso的globalExceptionHandler使用common的导入配置
- 【修改】mvn 聚合命令因为mall-leaf失败,所以将mall-leaf移除,单独成为一个项目,单独执行聚合命令
- 【修改】修改sso过滤器中,token非法的逻辑

### 2022年4月6日

- 【修改】数据库字段timestamp调整回datetime
- 【修改】在mall-order 和mall-product中添加kryo依赖,实现undo_log序列化问题解决
- 【修改】调整所有VO的时间显示格式yyyy-MM-dd HH:mm:ss
- 【修改】在查询订单列表时,显示所有时间,包括支付时间,修改时间和创建时间
- 【说明:**重要**】在前后台交互过程中,LocalDateTime 最好是yyyy-MM-dd HH:mm:ss格式
- 【新增】sso中admin的登录日志记录功能

### 2022年4月1日

- 【修改】修改查询我的订单列表的返回值,OrderListVO OrderItemListVO
- 【修改:重要修改】为订单新增功能添加返回值OrderAddVO 包含订单id 编号 创建订单时间,和订单实际应支付金额
- 【修改】为订单模块的pojo添加DTO中的部分校验规则
- 【修改】调整redisTemplate和stringRedisTemplate的使用位置
- 【新增】为秒杀业务提供获取所有缓存key值的工具类SeckillCacheUtils
- 【新增】秒杀查询seckillSkuVO添加缓存逻辑
- 【修改:重要修改】发起秒杀会返回订单信息
- 【修改:重要修改】秒杀定时任务中布隆过滤器的spuId追加测试方法和生产方法

### 2022年3月31日

- 【修改】application.yml所有项目分成两组,一个dev 链接云端 一个test链接本地虚拟机
- 【修改】application.yml关闭dubbo配置中心,元数据中心的配置
- 【修改】application.yml配置dubbo.protocal.port -1自增端口号

### 2022年3月30日

- 【修改】leaf_db mall_oms数据库数据表格调整

### 2022年3月28日

- 【新增】订单seata分布式事务,数据一致性保证
- 【修改】秒杀查询spu 添加布隆过滤器,防止缓存穿透
- 【新增】定时任务 创建每天秒杀商品的布隆过滤器
- 【新增】所有项目新增application-dev.yml application-test.yml application-prod.yml

### 2022年3月26日

- 【新增】引入seata分布式事务

- 【修改】调整数据库datatime为timestamp,否则seata序列化无法支持

### 2022年3月25日

- 【新增】网关统一入口mall-gateway-server
- 【新增】网关统一访问后端微服务的doc.html 接口文档
  - http://localhost:10000/mall-seckill/doc.html
  - http://localhost:10000/mall-product/doc.html
  - http://localhost:10000/mall-order/doc.html
  - http://localhost:10000/mall-search/doc.html
  - http://localhost:10000/mall-front/doc.html
  - http://localhost:10000/mall-search/doc.html
  - http://localhost:10000/mall-ams/doc.html
  - http://localhost:10000/mall-ums/doc.html
- 【新增】秒杀添加sentinel限流和降级服务

### 2022年3月22日
- 【修改】 所有配置,都指向云服务器地址,不在占用本地虚拟机
- 【说明】 下载之后直接启动,按如下顺序
    1. leaf>product>ams>ums>sso>order>front>search>seckill
    启动9个服务
    2. 接口文档地址
    http://localhost:10002/doc.html
    后续端口 10003-10008
### 2022年3月21日
- 【新增】ums注册功能
- 【新增】ums注册的校验用户名,手机,邮箱功能
- 【新增】sso 中使用token获取userinfo接口 后台用户,前台用户获取返回结构一致
### 2022年3月20日
- 【修改】doc/sql:mall_ums登录密码修改成BCryptPasswordEncoder加密密码
- 【修改】doc/sql:mall_ams登录密码修改成BCryptPasswordEncoder加密密码
### 2022年3月17日
- 【新增】 前台系统 front 订单系统 order 搜索系统search 秒杀系统 seckill 用户系统ums