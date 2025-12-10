package com.pucetec.products.services

import com.jayway.jsonpath.Option
import com.pucetec.products.exceptions.ProductAlreadyExistsException
import com.pucetec.products.exceptions.ProductNotFoundException
import com.pucetec.products.exceptions.StockOutOfRangeException
import com.pucetec.products.mappers.ProductMapper
import com.pucetec.products.models.entities.Product
import com.pucetec.products.models.requests.ProductRequest
import com.pucetec.products.repositories.ProductRepository
import com.pucetec.products.repositories.InvoiceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductServiceTest{

    private lateinit var productRepositoryMock: ProductRepository
    private lateinit var productMapper: ProductMapper

    private lateinit var productService: ProductService


    @BeforeEach
    fun init() {
        productRepositoryMock = mock(ProductRepository::class)
        productMapper = ProductMapper()

        productService = ProductService(
            productMapper= productMapper,
            productRepository = productRepositoryMock
        )
    }
    @Test
    fun `SHOULD return a product response GIVEN a valid id`(){
        val productId = 1L

        val mockProduct = Product(
            name = 'agua',
            price = 0.5,
            stock = 10
        ).apply{
            id = productId
        }

        `when`(productRepositoryMock.findById(productId))
            .thenReturn(Optional.of(mockProduct))

        val response = productService.findById(productId)

        assertEquals(mockProduct.name,response.name)
        assertEquals(mockProduct.id,response.id)
        assertEquals(mockProduct.price,response.price)
        assertEquals(mockProduct.stock,response.stock)

    }

    @Test
    fun `SHOULD return ProductNotFoundException GIVEN a non existing product`(){
        `when`(productRepositoryMock.findById(88L))
            .thenReturn(Optional.empty())

        assertThrows<ProductNotFoundException>{
            productService.findById(88L)
        }
    }

    @Test
    fun `SHOULD save a product GIVEN a valid product request`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id =1L }

        val productToSave = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )


        //hay qye mockear el findBy Name
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(savedProduct)

        `when`(productRepositoryMock.save(productToSave))
            .thenReturn(savedProduct)
        val response = productService.save(request)

        assertEquals(savedProduct.id,response.id)
    }

    @Test
    fun `SHOULD NOT save a product GIVEN an existing product request`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id =1L }

        val productToSave = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )


        //hay qye mockear el findBy Name
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(savedProduct)

        `when`(productRepositoryMock.save(productToSave))
            .thenReturn(savedProduct)
         assertThrows<ProductAlreadyExistsException>{
             productService.save(request)
         }
    }

    @Test
    fun `SHOULD NOT save a product GIVEN a stock equal or bigger than 20`(){
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 21
        )

        assertThrows<StockOutOfRangeException>{
            productService.save(request)
        }
    }
}


