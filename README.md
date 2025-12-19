# Career Coach Booking System

职业教练预约系统后端服务

## 项目简介

这是一个连接求职者与职业导师的在线辅导平台后端服务。用户可以通过Cal.com平台预约30分钟的1-on-1视频辅导课程。

## 技术栈

- **Java**: JDK 21
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis
- **数据库**: MySQL 8.0
- **构建工具**: Maven

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE career_coach_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表脚本：
```bash
mysql -u root -p career_coach_booking < src/main/resources/schema.sql
```

### 3. 配置文件

编辑 `src/main/resources/application.properties`，配置数据库连接和Cal.com信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/career_coach_booking?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password

# Cal.com配置
cal.api.url=https://api.cal.com/v1
cal.api.key=your-cal-api-key
cal.webhook.secret=your-webhook-secret
```

### 4. Cal.com设置

1. 注册Cal.com账号（免费）
2. 创建一个30分钟的事件类型（Event Type）
3. 获取API Key：
   - 登录Cal.com
   - 进入 Settings > Developers > API Keys
   - 创建新的API Key
4. 配置Webhook：
   - 进入 Settings > Webhooks
   - 添加Webhook URL: `http://your-domain/api/webhook/cal`
   - 选择事件类型：`BOOKING_CREATED`, `BOOKING_CANCELLED`, `MEETING_ENDED`

### 5. 运行项目

```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动

## API接口

### 1. 获取预约URL

**接口**: `POST /api/booking-url`

**请求体**:
```json
{
  "userId": "user123"
}
```

**响应**:
```json
{
  "bookingUrl": "https://cal.com/your-username/30min"
}
```

### 2. 查询我的预约列表

**接口**: `GET /api/bookings?userId=user123`

**响应**:
```json
[
  {
    "id": 1,
    "status": "BOOKING_CREATED",
    "coachName": "Coach Name",
    "startTime": "2024-01-01T10:00:00",
    "endTime": "2024-01-01T10:30:00"
  }
]
```

### 3. 获取取消预约链接

**接口**: `POST /api/bookings/cancel?bookingId=1`

**响应**:
```json
{
  "cancelUrl": "https://cal.com/cancellation/booking-uid"
}
```

### 4. Cal.com Webhook回调

**接口**: `POST /api/webhook/cal`

**请求体**（Cal.com自动发送）:
```json
{
  "triggerEvent": "BOOKING_CREATED",
  "payload": {
    "id": 12345,
    "title": "30min Meeting",
    "startTime": "2024-01-01T10:00:00Z",
    "endTime": "2024-01-01T10:30:00Z",
    "attendees": [
      {
        "name": "User Name",
        "email": "user@example.com"
      },
      {
        "name": "Coach Name",
        "email": "coach@example.com"
      }
    ],
    "eventTypeId": 1,
    "status": "ACCEPTED"
  }
}
```

## 预约状态说明

- `PENDING`: 初始状态
- `BOOKING_CREATED`: 支付成功且预约确认（Active），等待开课
- `BOOKING_CANCELLED`: 预约已取消
- `MEETING_ENDED`: 课程正常结束
- `NO_SHOW`: 用户或导师未出席

## 项目结构

```
src/main/java/com/careercoach/careercoachbooking/
├── config/          # 配置类
├── controller/      # 控制器层
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── mapper/         # MyBatis Mapper接口
└── service/        # 业务逻辑层

src/main/resources/
├── mapper/         # MyBatis XML映射文件
├── application.properties
└── schema.sql      # 数据库建表脚本
```

## 注意事项

1. **API无需鉴权**: 为了简化演示，所有API接口都未实现鉴权，实际生产环境需要添加认证机制。

2. **Cal.com配置**: 
   - 需要将 `CalService` 中的 `calUsername` 和 `eventTypeSlug` 替换为实际的Cal.com用户名和事件类型slug
   - 或者通过配置文件注入这些值

3. **Webhook处理**: 
   - 当前实现中，userId需要从webhook的attendees中解析
   - 可能需要根据实际业务逻辑调整导师识别逻辑

4. **测试建议**: 
   - 使用Postman或curl测试API接口
   - 使用Cal.com的测试模式进行预约测试
   - 使用ngrok等工具将本地服务暴露给Cal.com进行Webhook测试

## 开发建议

1. 在实际使用中，建议添加：
   - 用户认证和授权机制
   - 参数校验（使用Bean Validation）
   - 统一异常处理
   - 日志记录
   - 单元测试和集成测试

2. Cal.com集成优化：
   - 可以通过API动态获取事件类型列表
   - 实现更完善的Webhook事件处理
   - 添加重试机制和错误处理

## License

MIT

