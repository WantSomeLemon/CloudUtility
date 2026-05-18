# Cloud Utility Service - Hệ Thống Chuyển Đổi Định Dạng Tài Liệu Tự Động

Đồ án Điện toán đám mây xây dựng hệ thống chuyển đổi định dạng tài liệu văn phòng tự động dựa trên kiến trúc Microservices (Producer-Consumer). Hệ thống tách biệt hoàn toàn giữa tầng tiếp nhận API (`file-api`) và tầng xử lý ngầm (`file-worker`) thông qua hàng đợi thông điệp Message Queue.

---

## 🚀 Tính Năng Chính
* **Word to PDF:** Chuyển đổi tệp tin Microsoft Word (`.docx`) sang định dạng tài liệu di động (`.pdf`).
* **PDF to Word:** Chuyển đổi ngược lại từ tệp tin `*.pdf` sang định dạng cấu trúc `*.docx`.

---

## 🛠️ Công Nghệ Sử Dụng & Phiên Bản

Hệ thống được đồng bộ hóa phiên bản nghiêm ngặt trên cả môi trường Local và môi trường Docker:

* **Ngôn ngữ lập trình:** Java 22
* **Framework chính:** Spring Boot 3.x (Spring Web, Spring Data MongoDB, Spring Data Redis)
* **Công cụ build:** Maven 3.x
* **Cơ sở dữ liệu (Metadata):** MongoDB 7.0 (Quản lý thông tin cấu trúc tệp và trạng thái xử lý)
* **Hàng đợi thông điệp & Cache:** Redis 7 (Alpine-based) (Điều phối hàng đợi tác vụ giữa API và Worker)
* **Công cụ đóng gói & Triển khai:** Docker & Docker Compose v2.x
* **Base OS Image:** `eclipse-temurin:22-jre-jammy` (Môi trường chạy Java 22 tối ưu, bảo mật)

---

## 🔄 Luồng Hoạt Động Của Hệ Thống (Workflow)

1. **Client:** Gửi yêu cầu qua HTTP POST gửi kèm file cần convert lên `file-api` (Cổng 8080).
2. **file-api:** * Lưu tệp tin gốc vào thư mục chia sẻ chung (Mount Volume).
   * Khởi tạo bản ghi quản lý tệp trên `mongodb` với trạng thái `PENDING`.
   * Đẩy một thông điệp chứa thông tin tác vụ vào hàng đợi `redis`.
3. **file-worker:** * Lắng nghe liên tục từ `redis`, nhặt thông điệp tác vụ về xử lý ngầm.
   * Cập nhật trạng thái tệp sang `PROCESSING` trong `mongodb`.
   * Sử dụng thư viện Java chuyên dụng đọc file gốc từ Shared Volume và xuất ra file định dạng mới (PDF hoặc Word).
   * Lưu tệp đích vào thư mục đầu ra và cập nhật trạng thái trong `mongodb` thành `SUCCESS`.
4. **Kết quả:** Người dùng gọi API kiểm tra trạng thái thành công sẽ nhận được liên kết tải xuống tệp tin đã chuyển đổi từ `file-api`.

---

## 📁 Cấu Trúc Thư Mục Dự Án

```text
cloud-utility/
|   .dockerignore
|   .gitignore
|   compose.yaml
|   Dockerfile
|   pom.xml
|   README.md
|   
+---.idea
|       .gitignore
|       compiler.xml
|       encodings.xml
|       jarRepositories.xml
|       misc.xml
|       workspace.xml
|       
+---file-api
|   |   pom.xml
|   |   README.Docker.md
|   |   
|   \---src
|       \---main
|           \---java
|               \---com
|                   \---Sem2
|                       \---DTDM
|                               Main.java
|                               
\---file-worker
    |   pom.xml
    |   README.Docker.md
    |   
    \---src
        \---main
            \---java
                \---com
                    \---Sem2
                        \---DTDM
                                Main.java
```
---

## ⚡ Hướng Dẫn Triển Khai Chi Tiết (Setup & Run)
Do các thư mục thực thi (target/) và lưu trữ tệp chạy tạm đã được chặn bởi .gitignore nhằm tránh làm nặng Git Repository, người sử dụng khi lấy dự án về cần thao tác tuần tự theo các bước sau:

**1. Build Ứng Dụng Java Bằng Maven**  
- Hệ thống sử dụng cấu trúc Maven Multi-module quản lý bởi file pom.xml ở thư mục gốc. Bạn có thể chọn một trong hai cách sau để build tạo file thực thi:

   * **Cách 1**: Chạy bằng câu lệnh Terminal
   Mở Terminal tại thư mục gốc cloud-utility và thực hiện lệnh:

         mvn clean package -DskipTests

   * **Cách 2**: Sử dụng giao diện IntelliJ IDEA

      * Ở cạnh phải màn hình, mở tab công cụ Maven.

      * Chọn module gốc dự án (ví dụ: CloudUtility).

      * Tìm đến mục Lifecycle.

      * Giữ phím Ctrl và chọn đồng thời cả hai mục clean và package.

      * Click đúp chuột hoặc bấm nút Run (biểu tượng tam giác xanh) để bắt đầu quá trình build.

## **🎉 Kết quả sau khi chạy xong**:  
   Maven tự động biên dịch mã nguồn và sinh ra 2 thư mục target chứa các file thực thi .jar như sau:

   * **file-api/target/file-api-1.0-SNAPSHOT.jar**
   
   * **file-worker/target/file-worker-1.0-SNAPSHOT.jar**

**2. Khởi Tạo Thư Mục Lưu Trữ Đệm (Shared Volume)**  
- Tạo một thư mục trống tên là tmp-storage nằm ngay tại thư mục gốc của dự án (cùng cấp với file compose.yaml).


- Vai trò: Đây là nơi lưu trữ các tệp tin văn bản thật (đầu vào và đầu ra) chia sẻ dữ liệu chung giữa máy vật lý (Host) và các Docker Container chạy ngầm.

**3. Khởi Chạy Hệ Thống Với Docker Compose**  
- Đảm bảo phần mềm Docker Desktop đã được kích hoạt thành công trên máy của bạn.


- Mở Terminal tại thư mục gốc dự án và thực thi câu lệnh:

      docker compose up --build
## ⚙️ Hệ thống sẽ tự động thực hiện các tác vụ:

- Tải các bộ Image hạ tầng mạng chuẩn về máy: mongo:7.0 và redis:7-alpine.


- Đọc cấu trúc Dockerfile, thiết lập môi trường Java 22 (eclipse-temurin:22-jre-jammy), copy các file .jar đã build ở Bước 1 vào container và tiến hành phân quyền.


- Kích hoạt đồng bộ, thiết lập kết nối toàn bộ 4 dịch vụ (file-api, file-worker, redis, mongodb) chạy trong cùng mạng nội bộ.

## 🔌 Cổng Dịch Vụ Mặc Định (Default Ports)
- File API Gateway: http://localhost:8080 — Cổng tiếp nhận các request upload và gọi API download từ Client.


- File Worker Process: Chạy ngầm trong nền, thực hiện lắng nghe và xử lý convert file tuần tự lấy từ hàng đợi Redis.


- Redis Message Queue: localhost:6379 — Cổng kết nối hàng đợi trung gian.


- MongoDB Metadata Store: localhost:2717 — Cổng truy cập database lưu trữ thông tin (Đã phân tách cổng host để tránh trùng lặp hệ thống local).

## 🔒 Bảo Mật Hệ Thống (Cloud-native Security)
- Nguyên tắc đặc quyền tối thiểu: Ứng dụng không chạy dưới quyền root. Quy trình đóng gói trong Dockerfile cấu hình ứng dụng chạy hoàn toàn dưới một user độc lập có tên appuser (UID 10001) để tránh các nguy cơ khai thác lỗ hổng hệ thống.
 

- Phân quyền phân vùng dữ liệu: Thư mục lưu trữ tài liệu tệp tin chia sẻ /app/storage bên trong container được cấp quyền sở hữu và giới hạn quyền đọc-ghi nghiêm ngặt, chỉ duy nhất appuser mới có quyền thao tác trực tiếp.