package com.example

import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.model.formatDisplayToIsoDate
import com.example.b2capp.core.model.formatIsoToDisplayDate
import com.example.b2capp.core.model.toDomain
import com.example.b2capp.core.model.toUpdateRequest
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.UserApiService
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UploadResponseDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import com.example.b2capp.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class UserProfileApiTest {

    @Test
    fun testUpdateProfileRequestDtoSerialization() {
        val moshi = ApiClient.moshi
        val request = UpdateProfileRequestDto(
            fullName = "Nguyễn Văn A",
            phone = "0987654321",
            avatarUrl = "https://example.com/avatar.jpg",
            gender = "MALE",
            dateOfBirth = "1998-10-24"
        )
        val adapter = moshi.adapter(UpdateProfileRequestDto::class.java)
        val json = adapter.toJson(request)

        assertTrue(json.contains("\"fullName\":\"Nguyễn Văn A\""))
        assertTrue(json.contains("\"phone\":\"0987654321\""))
        assertTrue(json.contains("\"gender\":\"MALE\""))
        assertTrue(json.contains("\"dateOfBirth\":\"1998-10-24\""))
        assertTrue(json.contains("\"avatarUrl\":\"https://example.com/avatar.jpg\""))
    }

    @Test
    fun testAvatarUploadResponseDtoDeserialization() {
        val json = """
            {
                "avatarUrl": "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                "avatarPublicId": "users/10/avatar/abc123",
                "format": "jpg",
                "bytes": 102400
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(AvatarUploadResponseDto::class.java)
        val response = adapter.fromJson(json)

        assertNotNull(response)
        assertEquals("https://res.cloudinary.com/demo/image/upload/sample.jpg", response?.avatarUrl)
        assertEquals("users/10/avatar/abc123", response?.avatarPublicId)
        assertEquals("jpg", response?.format)
        assertEquals(102400L, response?.bytes)
    }

    @Test
    fun testChangePasswordRequestDtoSerialization() {
        val moshi = ApiClient.moshi
        val request = ChangePasswordRequestDto(
            currentPassword = "oldPassword123",
            newPassword = "newPassword456@",
            confirmPassword = "newPassword456@"
        )
        val adapter = moshi.adapter(ChangePasswordRequestDto::class.java)
        val json = adapter.toJson(request)

        assertTrue(json.contains("\"currentPassword\":\"oldPassword123\""))
        assertTrue(json.contains("\"newPassword\":\"newPassword456@\""))
        assertTrue(json.contains("\"confirmPassword\":\"newPassword456@\""))
    }

    @Test
    fun testDateConversionFunctions() {
        // ISO to Display: 1998-10-24 -> 24/10/1998
        assertEquals("24/10/1998", formatIsoToDisplayDate("1998-10-24"))

        // Display to ISO: 24/10/1998 -> 1998-10-24
        assertEquals("1998-10-24", formatDisplayToIsoDate("24/10/1998"))
    }

    @Test
    fun testUserProfileDtoToDomainMapping() {
        val dto = UserProfileDto(
            id = 10L,
            email = "nguyenvana@gmail.com",
            phone = "0987654321",
            fullName = "Nguyễn Văn A",
            avatarUrl = "https://example.com/a.jpg",
            gender = "MALE",
            dateOfBirth = "1998-10-24",
            role = "BUYER",
            status = "ACTIVE"
        )

        val domain = dto.toDomain()

        assertEquals(10L, domain.id)
        assertEquals("Nguyễn Văn A", domain.fullName)
        assertEquals("0987654321", domain.phoneNumber)
        assertEquals("nguyenvana@gmail.com", domain.email)
        assertEquals("https://example.com/a.jpg", domain.avatarUrl)
        assertEquals("Nam", domain.gender)
        assertEquals("24/10/1998", domain.birthDate)
        assertEquals("BUYER", domain.role)
        assertEquals("ACTIVE", domain.status)
    }

    @Test
    fun testUserProfileToUpdateRequestConversion() {
        val domain = UserProfile(
            id = 10L,
            fullName = "Trần Thị B",
            phoneNumber = "+84 912 345 678",
            email = "tranthib@gmail.com",
            avatarUrl = "https://example.com/b.jpg",
            gender = "Nữ",
            birthDate = "14/02/2000",
            role = "BUYER"
        )

        val request = domain.toUpdateRequest()

        assertEquals("Trần Thị B", request.fullName)
        assertEquals("0912345678", request.phone)
        assertEquals("FEMALE", request.gender)
        assertEquals("2000-02-14", request.dateOfBirth)
        assertEquals("https://example.com/b.jpg", request.avatarUrl)
    }

    @Test
    fun testTokenManagerUserProfilePersistence() {
        val tokenManager = TokenManager.getInstance()

        val sampleProfile = UserProfile(
            id = 15L,
            fullName = "Lê Văn C",
            phoneNumber = "0933445566",
            email = "levanc@example.com",
            avatarUrl = "https://example.com/avatar_c.png",
            gender = "Nam",
            birthDate = "01/01/1992",
            role = "SELLER"
        )

        tokenManager.saveUserProfile(sampleProfile)
        val retrieved = tokenManager.getUserProfile()

        assertEquals("Lê Văn C", retrieved.fullName)
        assertEquals("0933445566", retrieved.phoneNumber)
        assertEquals("levanc@example.com", retrieved.email)
        assertEquals("https://example.com/avatar_c.png", retrieved.avatarUrl)
        assertEquals("Nam", retrieved.gender)
        assertEquals("01/01/1992", retrieved.birthDate)
        assertEquals("SELLER", retrieved.role)
    }

    @Test
    fun testUserRepositoryMethodsWithFakeApiService() = runBlocking {
        val fakeProfileDto = UserProfileDto(
            id = 10L,
            email = "test@b2c.local",
            phone = "0987654321",
            fullName = "Người Dùng Thử Nghiệm",
            avatarUrl = "https://example.com/test.jpg",
            gender = "OTHER",
            dateOfBirth = "1999-09-09",
            role = "BUYER"
        )

        val fakeService = object : UserApiService {
            override suspend fun getMyProfile(): Response<ApiResponse<UserProfileDto>> {
                return Response.success(ApiResponse(data = fakeProfileDto))
            }

            override suspend fun updateMyProfile(request: UpdateProfileRequestDto): Response<ApiResponse<UserProfileDto>> {
                val updated = fakeProfileDto.copy(
                    fullName = request.fullName ?: fakeProfileDto.fullName,
                    phone = request.phone ?: fakeProfileDto.phone,
                    gender = request.gender ?: fakeProfileDto.gender
                )
                return Response.success(ApiResponse(data = updated))
            }

            override suspend fun uploadAvatar(file: MultipartBody.Part): Response<ApiResponse<AvatarUploadResponseDto>> {
                val uploadResponse = AvatarUploadResponseDto(
                    avatarUrl = "https://res.cloudinary.com/uploaded.jpg",
                    avatarPublicId = "test_id",
                    format = "jpg",
                    bytes = 2048L
                )
                return Response.success(ApiResponse(data = uploadResponse))
            }

            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<com.example.b2capp.data.remote.dto.UploadResponseDto>> {
                val uploadResponse = com.example.b2capp.data.remote.dto.UploadResponseDto(
                    url = "https://res.cloudinary.com/uploaded_generic.jpg",
                    secureUrl = "https://res.cloudinary.com/uploaded_generic.jpg",
                    publicId = "test_upload_id",
                    format = "jpg",
                    bytes = 4096L
                )
                return Response.success(ApiResponse(data = uploadResponse))
            }

            override suspend fun changePassword(request: ChangePasswordRequestDto): Response<ApiResponse<Void>> {
                return Response.success(ApiResponse(data = null))
            }

            override suspend fun getMyAddresses() = throw NotImplementedError()
            override suspend fun createAddress(request: com.example.b2capp.data.remote.dto.AddressRequestDto) = throw NotImplementedError()
            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: com.example.b2capp.data.remote.dto.AddressRequestDto) = throw NotImplementedError()
            override suspend fun deleteAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun setDefaultAddress(addressId: Long) = throw NotImplementedError()
        }

        val repository = UserRepository(apiServiceHolder = { fakeService })

        // 1. Get profile
        val profileResult = repository.getMyProfile()
        assertTrue(profileResult.isSuccess)
        val profile = profileResult.getOrNull()
        assertEquals("Người Dùng Thử Nghiệm", profile?.fullName)
        assertEquals("OTHER", profile?.gender)

        // 2. Update profile
        val updateResult = repository.updateProfile(
            UpdateProfileRequestDto(fullName = "Tên Mới", phone = "0999888777")
        )
        assertTrue(updateResult.isSuccess)
        val updatedProfile = updateResult.getOrNull()
        assertEquals("Tên Mới", updatedProfile?.fullName)
        assertEquals("0999888777", updatedProfile?.phone)

        // 3. Upload avatar
        val avatarResult = repository.uploadAvatar(
            bytes = byteArrayOf(1, 2, 3),
            mimeType = "image/jpeg",
            fileName = "test.jpg"
        )
        assertTrue(avatarResult.isSuccess)
        assertEquals("https://res.cloudinary.com/uploaded.jpg", avatarResult.getOrNull()?.avatarUrl)

        // 4. Change password
        val changePasswordResult = repository.changePassword(
            ChangePasswordRequestDto("old", "new123456", "new123456")
        )
        assertTrue(changePasswordResult.isSuccess)

        // 5. Generic uploadImage via /api/v1/upload
        val uploadResult = repository.uploadImage(
            bytes = byteArrayOf(4, 5, 6),
            mimeType = "image/jpeg",
            fileName = "generic.jpg",
            purpose = "mobile_uploads"
        )
        assertTrue(uploadResult.isSuccess)
        assertEquals("https://res.cloudinary.com/uploaded_generic.jpg", uploadResult.getOrNull()?.secureUrl)
    }

    @Test
    fun testUploadResponseDtoDeserialization() {
        val json = """
            {
                "url": "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                "secureUrl": "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                "publicId": "b2c/uploads/abc123",
                "resourceType": "image",
                "format": "jpg",
                "originalFilename": "avatar.jpg",
                "bytes": 524288
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(com.example.b2capp.data.remote.dto.UploadResponseDto::class.java)
        val response = adapter.fromJson(json)

        assertNotNull(response)
        assertEquals("https://res.cloudinary.com/demo/image/upload/sample.jpg", response?.secureUrl)
        assertEquals("b2c/uploads/abc123", response?.publicId)
        assertEquals("image", response?.resourceType)
        assertEquals(524288L, response?.bytes)
    }

    @Test
    fun testUserRepositoryErrorHandling() = runBlocking {
        val errorJson = """
            {
                "code": 400,
                "message": "Validation failed",
                "errors": [
                    { "field": "phone", "message": "Phone number already exists" }
                ]
            }
        """.trimIndent()

        val failingService = object : UserApiService {
            override suspend fun getMyProfile(): Response<ApiResponse<UserProfileDto>> {
                return Response.error(401, "{\"message\":\"Unauthorized\"}".toResponseBody())
            }

            override suspend fun updateMyProfile(request: UpdateProfileRequestDto): Response<ApiResponse<UserProfileDto>> {
                return Response.error(400, errorJson.toResponseBody())
            }

            override suspend fun uploadAvatar(file: MultipartBody.Part): Response<ApiResponse<AvatarUploadResponseDto>> {
                return Response.error(400, "{\"message\":\"File too large\"}".toResponseBody())
            }

            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<UploadResponseDto>> {
                return Response.error(400, "{\"message\":\"Upload failed\"}".toResponseBody())
            }

            override suspend fun changePassword(request: ChangePasswordRequestDto): Response<ApiResponse<Void>> {
                return Response.error(400, "{\"message\":\"Wrong current password\"}".toResponseBody())
            }

            override suspend fun getMyAddresses() = throw NotImplementedError()
            override suspend fun createAddress(request: com.example.b2capp.data.remote.dto.AddressRequestDto) = throw NotImplementedError()
            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: com.example.b2capp.data.remote.dto.AddressRequestDto) = throw NotImplementedError()
            override suspend fun deleteAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun setDefaultAddress(addressId: Long) = throw NotImplementedError()
        }

        val repository = UserRepository(apiServiceHolder = { failingService })

        val updateResult = repository.updateProfile(UpdateProfileRequestDto(phone = "0900000001"))
        assertTrue(updateResult.isFailure)
        assertTrue(updateResult.exceptionOrNull()?.message?.contains("Phone number already exists") == true)

        val passResult = repository.changePassword(ChangePasswordRequestDto("wrong", "new123456", "new123456"))
        assertTrue(passResult.isFailure)
        assertTrue(passResult.exceptionOrNull()?.message?.contains("Wrong current password") == true)
    }
}

