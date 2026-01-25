package com.capstone.ecommerce.order.service;

import com.capstone.ecommerce.order.client.PaymentClient;
import com.capstone.ecommerce.order.client.ProductClient;
import com.capstone.ecommerce.order.entity.Order;
import com.capstone.ecommerce.order.entity.OrderStatus;
import com.capstone.ecommerce.order.entity.PaymentMethod;
import com.capstone.ecommerce.order.exception.EmptyCartException;
import com.capstone.ecommerce.order.repository.OrderItemRepository;
import com.capstone.ecommerce.order.repository.OrderRepository;
import com.capstone.ecommerce.order.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String ORDER_PLACED_TOPIC = "order-placed";

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "orderPlacedTopic", ORDER_PLACED_TOPIC);
    }

    @Test
    void initiateOrder_Success() {
        var cart = OrderTestUtils.createCartWithItems();
        var paymentLinkResponse = OrderTestUtils.createPaymentLinkResponse();

        willDoNothing().given(productClient).validateAllProducts(cart);
        given(paymentClient.generatePayment(any(), any(), any())).willReturn(paymentLinkResponse);
        given(orderRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        
        var order = orderService.initiateOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);

        assertNotNull(order);
        assertEquals(PaymentMethod.STRIPE, order.getPaymentMethod());
        assertEquals(paymentLinkResponse.link(), order.getPaymentLink());
        assertEquals(cart.items().size(), order.getItems().size());
        then(productClient).should(times(1)).validateAllProducts(cart);
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void initiateOrder_EmptyCart_ShouldThrowException() {
        var emptyCart = OrderTestUtils.createEmptyCart();

        Exception exception = assertThrows(EmptyCartException.class, () ->
                orderService.initiateOrder(1L, 5L, PaymentMethod.STRIPE.getId(), emptyCart));

        String expectedMessage = "Cart is empty. Cannot place order.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void initiateOrder_PaymentLinkGenerationFailure_ShouldThrowException() {
        var cart = OrderTestUtils.createCartWithItems();

        // You can use Mockito to simulate payment link generation failure
         when(paymentClient.generatePayment(any(), any(), any()))
                 .thenThrow(new RuntimeException("Payment service error"));
        var orderId = UUID.randomUUID();
        given(orderRepository.save(any())).willAnswer(invocation -> {
            Order argOrder = invocation.getArgument(0);
            argOrder.setOrderId(orderId);
            return argOrder;
        });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.initiateOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);
        });

        String expectedMessage = "Payment service error";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage + " vs " + expectedMessage);
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void initiateOrder_ProductValidationFailure_ShouldThrowException() {
        var cart = OrderTestUtils.createCartWithItems();

        // You can use Mockito to simulate product validation failure
         doThrow(new RuntimeException("Product validation error"))
                 .when(productClient)
                 .validateAllProducts(any());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.initiateOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);
        });

        String expectedMessage = "Product validation error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void initiateOrder_COD_Success() {
        var userId = 1L;
        var cart = OrderTestUtils.createCartWithItems();
        var orderId = UUID.randomUUID();
        given(orderRepository.save(any())).willAnswer(invocation -> {
            Order argOrder = invocation.getArgument(0);
            argOrder.setOrderId(orderId);
            return argOrder;
        });
        var returnedOrder = orderService.initiateOrder(userId, 5L, PaymentMethod.CASH_ON_DELIVERY.getId(), cart);

        assertNotNull(returnedOrder);
        assertEquals(PaymentMethod.CASH_ON_DELIVERY, returnedOrder.getPaymentMethod());
        assertEquals(OrderStatus.PLACED, returnedOrder.getStatus());
        verify(kafkaTemplate, times(1)).send(eq(ORDER_PLACED_TOPIC), any());
    }


}