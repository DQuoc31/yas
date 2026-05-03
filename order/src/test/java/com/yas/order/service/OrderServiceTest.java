package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private ProductService productService;
    private CartService cartService;
    private OrderMapper orderMapper;
    private PromotionService promotionService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        productService = mock(ProductService.class);
        cartService = mock(CartService.class);
        orderMapper = mock(OrderMapper.class);
        promotionService = mock(PromotionService.class);
        orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                productService,
                cartService,
                orderMapper,
                promotionService
        );
    }

    @Test
    void createOrder_Success() {
        OrderAddressPostVm addressPostVm = OrderAddressPostVm.builder()
                .phone("123456789")
                .contactName("Contact Name")
                .addressLine1("Address Line 1")
                .city("City")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Vietnam")
                .build();

        OrderPostVm orderPostVm = OrderPostVm.builder()
                .checkoutId("CHK123")
                .email("test@example.com")
                .note("Order Note")
                .tax(0.0f)
                .discount(0.0f)
                .numberItem(1)
                .totalPrice(BigDecimal.valueOf(100.0))
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(Collections.emptyList())
                .billingAddressPostVm(addressPostVm)
                .shippingAddressPostVm(addressPostVm)
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L); // Gán ID cho Order để các bước sau không bị NPE
            return order;
        });
        
        // Mocking cho acceptOrder (gọi bên trong createOrder)
        Order mockOrder = Order.builder()
                .id(1L)
                .email("test@example.com")
                .shippingAddressId(new OrderAddress())
                .billingAddressId(new OrderAddress())
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        OrderVm result = orderService.createOrder(orderPostVm);

        assertNotNull(result);
        assertEquals("test@example.com", result.email());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void getOrderWithItemsById_Success() {
        OrderAddress address = new OrderAddress();
        address.setId(1L);
        Order order = Order.builder()
                .id(1L)
                .shippingAddressId(address)
                .billingAddressId(address)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Collections.emptyList());

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getOrderWithItemsById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void updateOrderPaymentStatus_Success() {
        Order order = Order.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm paymentVm = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(123L)
                .paymentStatus("COMPLETED")
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(paymentVm);

        assertNotNull(result);
        assertEquals("PAID", result.orderStatus());
        assertEquals(123L, result.paymentId());
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(PaymentStatus.COMPLETED, orderCaptor.getValue().getPaymentStatus());
        assertEquals(OrderStatus.PAID, orderCaptor.getValue().getOrderStatus());
    }

    @Test
    void acceptOrder_Success() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository).save(any(Order.class));
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }

    @Test
    void rejectOrder_Success() {
        Order order = Order.builder().id(1L).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Reason");

        verify(orderRepository).save(any(Order.class));
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertEquals("Reason", order.getRejectReason());
    }

    @Test
    void getLatestOrders_Success() {
        int count = 5;
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .shippingAddressId(new OrderAddress())
                .billingAddressId(new OrderAddress())
                .build();
        
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        List<com.yas.order.viewmodel.order.OrderBriefVm> result = orderService.getLatestOrders(count);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).email());
        
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).getLatestOrders(pageableCaptor.capture());
        assertEquals(count, pageableCaptor.getValue().getPageSize());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void rejectOrder_NotFound() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.rejectOrder(orderId, "any"));
    }

    @Test
    void getAllOrder_Empty_Success() {
        org.springframework.data.util.Pair<java.time.ZonedDateTime, java.time.ZonedDateTime> timePair = org.springframework.data.util.Pair.of(null, null);
        org.springframework.data.util.Pair<String, String> billingPair = org.springframework.data.util.Pair.of(null, null);
        org.springframework.data.util.Pair<Integer, Integer> infoPage = org.springframework.data.util.Pair.of(0, 10);
        
        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        com.yas.order.viewmodel.order.OrderListVm result = orderService.getAllOrder(timePair, "", List.of(), billingPair, "", infoPage);

        assertNotNull(result);
        assertEquals(0, result.totalElements());
    }

    @Test
    void testGetOrders_Success() {
        java.time.ZonedDateTime createdFrom = java.time.ZonedDateTime.now().minusDays(1);
        java.time.ZonedDateTime createdTo = java.time.ZonedDateTime.now();
        List<OrderStatus> orderStatus = List.of(OrderStatus.PENDING);
        org.springframework.data.util.Pair<java.time.ZonedDateTime, java.time.ZonedDateTime> timePair = org.springframework.data.util.Pair.of(createdFrom, createdTo);
        org.springframework.data.util.Pair<String, String> billingPair = org.springframework.data.util.Pair.of("VN", "123");
        org.springframework.data.util.Pair<Integer, Integer> infoPage = org.springframework.data.util.Pair.of(0, 10);

        Order order = Order.builder().id(1L).build();
        Page<Order> orderPage = new org.springframework.data.domain.PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.PageRequest.class)))
                .thenReturn(orderPage);

        var result = orderService.getAllOrder(timePair, "Prod", orderStatus, billingPair, "test@test.com", infoPage);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_Success() {
        Long productId = 1L;
        // Mock Security Context
        org.springframework.security.oauth2.jwt.Jwt jwt = mock(org.springframework.security.oauth2.jwt.Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token");
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext ctx = mock(org.springframework.security.core.context.SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(ctx);

        when(productService.getProductVariations(productId)).thenReturn(List.of());
        when(orderRepository.findOne(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(Optional.of(new Order()));

        var result = orderService.isOrderCompletedWithUserIdAndProductId(productId);

        assertNotNull(result);
        assertEquals(true, result.isPresent());
    }

    @Test
    void constants_ErrorCode_Constructor() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<com.yas.order.utils.Constants.ErrorCode> constructor = com.yas.order.utils.Constants.ErrorCode.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void orderSpecification_Constructor() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<com.yas.order.specification.OrderSpecification> constructor = com.yas.order.specification.OrderSpecification.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}
