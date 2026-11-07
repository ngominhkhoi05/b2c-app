package com.example.b2capp.data

import com.example.b2capp.core.model.ProductItem

object SampleData {
    val products = listOf(
        ProductItem(
            id = "p1",
            name = "Áo Thun Polo Nam Phối Cổ Dệt Phom Regular Co Giãn Thoáng Khí",
            imageUrl = "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500&auto=format&fit=crop&q=60",
            price = 189000L,
            originalPrice = 280000L,
            rating = 4.9f,
            soldCount = 4200,
            isFreeShipping = true,
            stock = 50,
            category = "Thời trang",
            description = "Chất vải cotton cá sấu 4 chiều mềm mịn, co giãn tốt, không xù lông và thấm hút mồ hôi tối ưu. Phù hợp đi làm, đi chơi hay thể thao dạo phố."
        ),
        ProductItem(
            id = "p2",
            name = "Giày Sneaker Nam Nữ Thể Thao Đế Cao Su Chống Trơn Trượt Siêu Nhẹ",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&auto=format&fit=crop&q=60",
            price = 349000L,
            originalPrice = 499000L,
            rating = 4.8f,
            soldCount = 8900,
            isFreeShipping = true,
            stock = 25,
            category = "Giày dép",
            description = "Thiết kế năng động cá tính, lót êm chân đàn hồi cao, đế đúc chống trượt bám đường cực tốt. Dễ phối trang phục trẻ trung."
        ),
        ProductItem(
            id = "p3",
            name = "Tai Nghe Bluetooth Không Dây Khử Ồn Chủ Động Bass Trầm Sâu Pin 30H",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60",
            price = 590000L,
            originalPrice = 850000L,
            rating = 4.9f,
            soldCount = 12400,
            isFreeShipping = true,
            stock = 15,
            category = "Điện tử",
            description = "Công nghệ Bluetooth 5.3 kết nối ổn định, micro lọc âm đàm thoại trong trẻo, thời lượng pin liên tục lên đến 30 tiếng kèm hộp sạc nhanh Type-C."
        ),
        ProductItem(
            id = "p4",
            name = "Đồng Hồ Thông Minh Màn Hình AMOLED Kháng Nước Đo Nhịp Tim SpO2",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&auto=format&fit=crop&q=60",
            price = 799000L,
            originalPrice = 1150000L,
            rating = 4.7f,
            soldCount = 3100,
            isFreeShipping = true,
            stock = 40,
            category = "Điện tử",
            description = "Theo dõi sức khỏe toàn diện 24/7, hơn 100 chế độ luyện tập thể thao, nhận thông báo cuộc gọi và tin nhắn tức thì."
        ),
        ProductItem(
            id = "p5",
            name = "Balo Thời Trang Chống Thấm Nước Ngăn Chứa Laptop 15.6 Inch Đa Năng",
            imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&auto=format&fit=crop&q=60",
            price = 229000L,
            originalPrice = 350000L,
            rating = 4.8f,
            soldCount = 6700,
            isFreeShipping = false,
            stock = 60,
            category = "Phụ kiện",
            description = "Chất liệu vải Oxford chống thấm trượt nước cao cấp, đệm lưng thoáng khí chống mỏi vai, tích hợp cổng sạc USB tiện dụng."
        ),
        ProductItem(
            id = "p6",
            name = "Kem Dưỡng Ẩm Phục Hồi Dịu Da Chiết Xuất Rau Má Centella 50ml",
            imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=500&auto=format&fit=crop&q=60",
            price = 259000L,
            originalPrice = 320000L,
            rating = 4.9f,
            soldCount = 15300,
            isFreeShipping = true,
            stock = 80,
            category = "Mỹ phẩm",
            description = "Thành phần lành tính chiết xuất rau má tự nhiên, cấp ẩm tức thì, làm dịu da nhạy cảm và củng cố hàng rào bảo vệ da khỏe mạnh."
        )
    )

    val categories = listOf("Tất cả", "Thời trang", "Giày dép", "Điện tử", "Phụ kiện", "Mỹ phẩm")
}
