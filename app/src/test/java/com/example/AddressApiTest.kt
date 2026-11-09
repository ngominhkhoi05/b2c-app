package com.example

import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.model.toDomain
import com.example.b2capp.core.model.toRequestDto
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.UserApiService
import com.example.b2capp.data.remote.dto.AddressDto
import com.example.b2capp.data.remote.dto.AddressRequestDto
import com.example.b2capp.data.remote.dto.ApiResponse
import com.example.b2capp.data.remote.dto.AvatarUploadResponseDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.UpdateProfileRequestDto
import com.example.b2capp.data.remote.dto.UploadResponseDto
import com.example.b2capp.data.remote.dto.UserProfileDto
import com.example.b2capp.data.repository.UserRepository
import com.squareup.moshi.Types
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AddressApiTest {

    @Test
    fun testAddressRequestDtoSerialization() {
        val request = AddressRequestDto(
            recipientName = "Nguyễn Văn A",
            phone = "0987654321",
            provinceCode = "TP. Hồ Chí Minh",
            districtCode = "Quận 1",
            wardCode = "Phường Bến Nghé",
            streetDetail = "Số 123 Đường Nguyễn Huệ",
            fullAddress = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
            type = "HOME",
            isDefault = true
        )

        val adapter = ApiClient.moshi.adapter(AddressRequestDto::class.java)
        val json = adapter.toJson(request)

        assertTrue(json.contains("\"recipientName\":\"Nguyễn Văn A\""))
        assertTrue(json.contains("\"phone\":\"0987654321\""))
        assertTrue(json.contains("\"type\":\"HOME\""))
        assertTrue(json.contains("\"isDefault\":true"))
        assertTrue(json.contains("\"streetDetail\":\"Số 123 Đường Nguyễn Huệ\""))
    }

    @Test
    fun testAddressDtoDeserialization() {
        val json = """
            {
                "id": 105,
                "userId": 1,
                "recipientName": "Trần Thị B",
                "phone": "0912345678",
                "provinceCode": "Thừa Thiên Huế",
                "districtCode": "TP. Huế",
                "wardCode": "Phường Vĩnh Ninh",
                "streetDetail": "45 Lê Lợi",
                "fullAddress": "45 Lê Lợi, Phường Vĩnh Ninh, TP. Huế, Thừa Thiên Huế",
                "type": "OFFICE",
                "isDefault": false
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(AddressDto::class.java)
        val dto = adapter.fromJson(json)

        assertNotNull(dto)
        assertEquals(105L, dto?.id)
        assertEquals("Trần Thị B", dto?.recipientName)
        assertEquals("0912345678", dto?.phone)
        assertEquals("OFFICE", dto?.type)
        assertEquals(false, dto?.isDefault)
    }

    @Test
    fun testAddressDtoListDeserialization() {
        val json = """
            {
                "code": 200,
                "message": "OK",
                "data": [
                    {
                        "id": 1,
                        "recipientName": "Nguyễn Văn A",
                        "phone": "0987654321",
                        "fullAddress": "Số 123 Đường Nguyễn Huệ, Quận 1, TP. HCM",
                        "streetDetail": "Số 123 Đường Nguyễn Huệ",
                        "type": "HOME",
                        "isDefault": true
                    },
                    {
                        "id": 2,
                        "recipientName": "Nguyễn Văn A",
                        "phone": "0987654321",
                        "fullAddress": "Tòa Bitexco, Q.1, TP. HCM",
                        "streetDetail": "Tòa Bitexco",
                        "type": "OFFICE",
                        "isDefault": false
                    }
                ]
            }
        """.trimIndent()

        val type = Types.newParameterizedType(
            ApiResponse::class.java,
            Types.newParameterizedType(List::class.java, AddressDto::class.java)
        )
        val adapter = ApiClient.moshi.adapter<ApiResponse<List<AddressDto>>>(type)
        val response = adapter.fromJson(json)

        assertNotNull(response)
        assertEquals(200, response?.code)
        assertEquals(2, response?.data?.size)
        assertEquals("Nguyễn Văn A", response?.data?.get(0)?.recipientName)
        assertEquals(true, response?.data?.get(0)?.isDefault)
        assertEquals("OFFICE", response?.data?.get(1)?.type)
    }

    @Test
    fun testToDomainMapping() {
        val dto = AddressDto(
            id = 42L,
            userId = 1L,
            recipientName = "Lê Hoàng Long",
            phone = "0909123456",
            provinceCode = "TP. Hồ Chí Minh",
            districtCode = "Quận 1",
            wardCode = "Phường Bến Nghé",
            streetDetail = "Tầng 18, Bitexco",
            fullAddress = "Tầng 18, Bitexco, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
            type = "OFFICE",
            isDefault = true
        )

        val domain = dto.toDomain()
        assertEquals("42", domain.id)
        assertEquals("Lê Hoàng Long", domain.recipientName)
        assertEquals("0909123456", domain.phoneNumber)
        assertEquals("Văn phòng", domain.addressType)
        assertEquals("Tầng 18, Bitexco, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh", domain.fullAddress)
        assertEquals("Tầng 18, Bitexco", domain.streetDetail)
        assertTrue(domain.isDefault)
    }

    @Test
    fun testToRequestDtoMapping() {
        val domain = UserAddress(
            id = "42",
            recipientName = "Lê Hoàng Long",
            phoneNumber = "+84 909 123 456",
            addressType = "Văn phòng",
            fullAddress = "Tầng 18 Bitexco, Q.1, TP. HCM",
            streetDetail = "Tầng 18 Bitexco",
            provinceCode = "TP. Hồ Chí Minh",
            districtCode = "Quận 1",
            wardCode = "Phường Bến Nghé",
            isDefault = true
        )

        val request = domain.toRequestDto()
        assertEquals("Lê Hoàng Long", request.recipientName)
        assertEquals("0909123456", request.phone) // Cleaned from +84
        assertEquals("OFFICE", request.type)
        assertEquals("Tầng 18 Bitexco", request.streetDetail)
        assertTrue(request.isDefault)
    }

    @Test
    fun testRepositoryGetMyAddressesSuccess() = runBlocking {
        val mockDtos = listOf(
            AddressDto(
                id = 1L,
                recipientName = "Nguyễn Văn A",
                phone = "0987654321",
                fullAddress = "Số 123 Nguyễn Huệ, Q.1, TP. HCM",
                type = "HOME",
                isDefault = true
            )
        )

        val fakeApiService = object : UserApiService {
            override suspend fun getMyProfile() = throw NotImplementedError()
            override suspend fun updateMyProfile(request: UpdateProfileRequestDto) = throw NotImplementedError()
            override suspend fun uploadAvatar(file: MultipartBody.Part) = throw NotImplementedError()
            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<UploadResponseDto>> = throw NotImplementedError()
            override suspend fun changePassword(request: ChangePasswordRequestDto) = throw NotImplementedError()

            override suspend fun getMyAddresses(): Response<ApiResponse<List<AddressDto>>> {
                return Response.success(ApiResponse(code = 200, data = mockDtos))
            }

            override suspend fun createAddress(request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun deleteAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun setDefaultAddress(addressId: Long) = throw NotImplementedError()
        }

        val repository = UserRepository(apiServiceHolder = { fakeApiService })
        val result = repository.getMyAddresses()

        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertNotNull(list)
        assertEquals(1, list?.size)
        assertEquals("Nguyễn Văn A", list?.first()?.recipientName)
        assertEquals(true, list?.first()?.isDefault)
    }

    @Test
    fun testRepositoryCreateAddressSuccess() = runBlocking {
        val expectedDto = AddressDto(
            id = 99L,
            recipientName = "Nguyễn Văn Mới",
            phone = "0987654321",
            fullAddress = "123 Đường Mới, Q.1, TP. HCM",
            type = "HOME",
            isDefault = true
        )

        val fakeApiService = object : UserApiService {
            override suspend fun getMyProfile() = throw NotImplementedError()
            override suspend fun updateMyProfile(request: UpdateProfileRequestDto) = throw NotImplementedError()
            override suspend fun uploadAvatar(file: MultipartBody.Part) = throw NotImplementedError()
            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<UploadResponseDto>> = throw NotImplementedError()
            override suspend fun changePassword(request: ChangePasswordRequestDto) = throw NotImplementedError()
            override suspend fun getMyAddresses() = throw NotImplementedError()

            override suspend fun createAddress(request: AddressRequestDto): Response<ApiResponse<AddressDto>> {
                return Response.success(ApiResponse(code = 200, data = expectedDto))
            }

            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun deleteAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun setDefaultAddress(addressId: Long) = throw NotImplementedError()
        }

        val repository = UserRepository(apiServiceHolder = { fakeApiService })
        val request = AddressRequestDto(
            recipientName = "Nguyễn Văn Mới",
            phone = "0987654321",
            streetDetail = "123 Đường Mới",
            fullAddress = "123 Đường Mới, Q.1, TP. HCM",
            type = "HOME",
            isDefault = true
        )
        val result = repository.createAddress(request)

        assertTrue(result.isSuccess)
        val created = result.getOrNull()
        assertEquals(99L, created?.id)
        assertEquals("Nguyễn Văn Mới", created?.recipientName)
    }

    @Test
    fun testRepositoryDeleteAddressSuccess() = runBlocking {
        val fakeApiService = object : UserApiService {
            override suspend fun getMyProfile() = throw NotImplementedError()
            override suspend fun updateMyProfile(request: UpdateProfileRequestDto) = throw NotImplementedError()
            override suspend fun uploadAvatar(file: MultipartBody.Part) = throw NotImplementedError()
            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<UploadResponseDto>> = throw NotImplementedError()
            override suspend fun changePassword(request: ChangePasswordRequestDto) = throw NotImplementedError()
            override suspend fun getMyAddresses() = throw NotImplementedError()
            override suspend fun createAddress(request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: AddressRequestDto) = throw NotImplementedError()

            override suspend fun deleteAddress(addressId: Long): Response<ApiResponse<Void>> {
                return Response.success(ApiResponse(code = 200, data = null))
            }

            override suspend fun setDefaultAddress(addressId: Long) = throw NotImplementedError()
        }

        val repository = UserRepository(apiServiceHolder = { fakeApiService })
        val result = repository.deleteAddress(10L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testRepositorySetDefaultAddressSuccess() = runBlocking {
        val updatedDto = AddressDto(
            id = 15L,
            recipientName = "Nguyễn Văn A",
            phone = "0987654321",
            fullAddress = "Số 15 Lê Lợi, TP. Huế",
            type = "HOME",
            isDefault = true
        )

        val fakeApiService = object : UserApiService {
            override suspend fun getMyProfile() = throw NotImplementedError()
            override suspend fun updateMyProfile(request: UpdateProfileRequestDto) = throw NotImplementedError()
            override suspend fun uploadAvatar(file: MultipartBody.Part) = throw NotImplementedError()
            override suspend fun uploadImage(file: MultipartBody.Part, purpose: String?): Response<ApiResponse<UploadResponseDto>> = throw NotImplementedError()
            override suspend fun changePassword(request: ChangePasswordRequestDto) = throw NotImplementedError()
            override suspend fun getMyAddresses() = throw NotImplementedError()
            override suspend fun createAddress(request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun getAddress(addressId: Long) = throw NotImplementedError()
            override suspend fun updateAddress(addressId: Long, request: AddressRequestDto) = throw NotImplementedError()
            override suspend fun deleteAddress(addressId: Long) = throw NotImplementedError()

            override suspend fun setDefaultAddress(addressId: Long): Response<ApiResponse<AddressDto>> {
                return Response.success(ApiResponse(code = 200, data = updatedDto))
            }
        }

        val repository = UserRepository(apiServiceHolder = { fakeApiService })
        val result = repository.setDefaultAddress(15L)

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull()?.isDefault)
    }
}
