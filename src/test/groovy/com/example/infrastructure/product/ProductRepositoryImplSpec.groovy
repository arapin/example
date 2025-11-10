package com.example.infrastructure.product

import com.example.domain.product.Money
import com.example.domain.product.Product
import com.example.domain.product.ProductRepository
import com.example.domain.product.ProductStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * ProductRepository 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductRepositoryImplSpec extends Specification {

    @Autowired
    ProductRepository productRepository

    def "상품을 저장하고 조회할 수 있다"() {
        given:
        def product = Product.builder()
                .name("테스트 상품")
                .price(new Money(10000L))
                .description("테스트 설명")
                .stockQuantity(100)
                .build()

        when:
        def savedProduct = productRepository.save(product)
        def foundProduct = productRepository.findById(savedProduct.id)

        then:
        foundProduct.isPresent()
        foundProduct.get().name == "테스트 상품"
        foundProduct.get().price.amount == 10000G
    }

    def "상품 상태로 검색할 수 있다"() {
        given:
        def product1 = Product.builder()
                .name("판매가능상품")
                .price(new Money(10000L))
                .stockQuantity(100)
                .build()
        def product2 = Product.builder()
                .name("품절상품")
                .price(new Money(20000L))
                .stockQuantity(0)
                .build()
        product2.changeStatus(ProductStatus.OUT_OF_STOCK)

        productRepository.save(product1)
        productRepository.save(product2)

        when:
        def availableProducts = productRepository.findByStatus(ProductStatus.AVAILABLE)
        def outOfStockProducts = productRepository.findByStatus(ProductStatus.OUT_OF_STOCK)

        then:
        availableProducts.size() >= 1
        outOfStockProducts.size() >= 1
        availableProducts.every { it.status == ProductStatus.AVAILABLE }
        outOfStockProducts.every { it.status == ProductStatus.OUT_OF_STOCK }
    }

    def "상품명으로 검색할 수 있다"() {
        given:
        productRepository.save(Product.builder()
                .name("스프링 부트 책")
                .price(new Money(30000L))
                .stockQuantity(50)
                .build())
        productRepository.save(Product.builder()
                .name("자바 프로그래밍 책")
                .price(new Money(25000L))
                .stockQuantity(30)
                .build())
        productRepository.save(Product.builder()
                .name("파이썬 책")
                .price(new Money(20000L))
                .stockQuantity(20)
                .build())

        when:
        def results = productRepository.findByNameContaining("책")

        then:
        results.size() >= 3
        results.every { it.name.contains("책") }
    }

    def "판매 가능한 상품을 조회할 수 있다"() {
        given:
        // 판매 가능 상품
        productRepository.save(Product.builder()
                .name("판매가능1")
                .price(new Money(10000L))
                .stockQuantity(10)
                .build())

        // 재고 없음
        def outOfStock = Product.builder()
                .name("재고없음")
                .price(new Money(10000L))
                .stockQuantity(0)
                .build()
        productRepository.save(outOfStock)

        // 판매 중단
        def discontinued = Product.builder()
                .name("판매중단")
                .price(new Money(10000L))
                .stockQuantity(5)
                .build()
        discontinued.changeStatus(ProductStatus.DISCONTINUED)
        productRepository.save(discontinued)

        when:
        def availableProducts = productRepository.findAvailableProducts()

        then:
        availableProducts.size() >= 1
        availableProducts.every { 
            it.status == ProductStatus.AVAILABLE && it.stockQuantity > 0 
        }
    }

    def "상품을 삭제할 수 있다"() {
        given:
        def product = Product.builder()
                .name("삭제할 상품")
                .price(new Money(10000L))
                .stockQuantity(100)
                .build()
        def savedProduct = productRepository.save(product)

        when:
        productRepository.delete(savedProduct)
        def found = productRepository.findById(savedProduct.id)

        then:
        !found.isPresent()
    }

    def "상품 존재 여부를 확인할 수 있다"() {
        given:
        def product = Product.builder()
                .name("테스트 상품")
                .price(new Money(10000L))
                .stockQuantity(100)
                .build()
        def savedProduct = productRepository.save(product)

        expect:
        productRepository.existsById(savedProduct.id)
        !productRepository.existsById(99999L)
    }
}

