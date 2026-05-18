# Cloud Utility Service - Hệ Thống Chuyển Đổi Định Dạng Tài Liệu Tự Động

Đồ án Điện toán đám mây xây dựng hệ thống chuyển đổi định dạng tài liệu văn phòng tự động dựa trên kiến trúc Microservices (Producer-Consumer). Hệ thống tách biệt hoàn toàn giữa tầng tiếp nhận API (`file-api`) và tầng xử lý ngầm (`file-worker`) thông qua hàng đợi thông điệp Message Queue.

## 🚀 Tính Năng Chính
* **Word to PDF:** Chuyển đổi tệp tin Microsoft Word (`.docx`) sang định dạng tài liệu di động (`.pdf`).
* **PDF to Word:** Chuyển đổi ngược lại từ tệp tin `*.pdf` sang định dạng cấu trúc `*.docx`.

## 🛠️ Công Nghệ Sử Dụng & Phiên Bản

Hệ thống được đồng bộ hóa phiên bản nghiêm ngặt trên cả môi trường Local và môi trường Docker:

* **Ngôn ngữ lập trình:** Java 22
* **Framework chính:** Spring Boot 3.x (Spring Web, Spring Data MongoDB, Spring Data Redis)
* **Công cụ build:** Maven 3.x
* **Cơ sở dữ liệu (Metadata):** MongoDB 7.0 (Quản lý thông tin cấu trúc tệp và trạng thái xử lý)
* **Hàng đợi thông điệp & Cache:** Redis 7 (Alpine-based) (Điều phối hàng đợi tác vụ chuyển đổi giữa API và Worker)
* **Công cụ đóng gói & Triển khai:** Docker & Docker Compose v2.x
* **Base OS Image:** `eclipse-temurin:22-jre-jammy` (Môi trường chạy Java 22 tối ưu, bảo mật)

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

## 📁 Cấu Trúc Thư Mục Dự Án

```text
cloud-utility/
├── file-api/               # Module Spring Boot tiếp nhận API upload/download
│   └── target/             # Nơi chứa file thực thi file-api-1.0-SNAPSHOT.jar
├── file-worker/            # Module Spring Boot xử lý convert file ngầm
│   └── target/             # Nơi chứa file thực thi file-worker-1.0-SNAPSHOT.jar
├── tmp-storage/            # Thư mục local chung được mount làm Shared Volume lưu trữ file
├── Dockerfile              # File cấu hình đóng gói gộp (Multi-stage Build)
├── compose.yaml            # File điều phối hạ tầng (API, Worker, Redis, MongoDB)
├── .dockerignore           # File chặn rác đồng bộ lên Docker Image
├── .gitignore              # File chặn rác đẩy lên Git Repository
└── README.md               # Hướng dẫn tài liệu đồ án