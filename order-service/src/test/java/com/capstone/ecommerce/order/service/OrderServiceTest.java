package com.capstone.ecommerce.order.service;

import com.capstone.ecommerce.order.client.PaymentClient;
import com.capstone.ecommerce.order.client.ProductClient;
import com.capstone.ecommerce.order.entity.PaymentMethod;
import com.capstone.ecommerce.order.exception.EmptyCartException;
import com.capstone.ecommerce.order.repository.OrderItemRepository;
import com.capstone.ecommerce.order.repository.OrderRepository;
import com.capstone.ecommerce.order.utils.OrderTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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



    @Test
    void placeOrder_Success() {
        var cart = OrderTestUtils.createCartWithItems();
        var paymentLinkResponse = OrderTestUtils.createPaymentLinkResponse();

        willDoNothing().given(productClient).validateAllProducts(cart);
        given(paymentClient.generatePayment(any(), any(), any())).willReturn(paymentLinkResponse);
        given(orderRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        
        var order = orderService.placeOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);

        assertNotNull(order);
        assertEquals(PaymentMethod.STRIPE, order.getPaymentMethod());
        assertEquals(paymentLinkResponse.link(), order.getPaymentLink());
        assertEquals(cart.items().size(), order.getItems().size());
        then(productClient).should(times(1)).validateAllProducts(cart);

    }

    @Test
    void placeOrder_EmptyCart_ShouldThrowException() {
        var emptyCart = OrderTestUtils.createEmptyCart();

        Exception exception = assertThrows(EmptyCartException.class, () ->
                orderService.placeOrder(1L, 5L, PaymentMethod.STRIPE.getId(), emptyCart));

        String expectedMessage = "Cart is empty. Cannot place order.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void placeOrder_PaymentLinkGenerationFailure_ShouldThrowException() {
        var cart = OrderTestUtils.createCartWithItems();

        // You can use Mockito to simulate payment link generation failure
         when(paymentClient.generatePayment(any(), any(), any())).thenThrow(new RuntimeException("Payment service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);
        });

        String expectedMessage = "Payment service error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void placeOrder_ProductValidationFailure_ShouldThrowException() {
        var cart = OrderTestUtils.createCartWithItems();

        // You can use Mockito to simulate product validation failure
         doThrow(new RuntimeException("Product validation error"))
                 .when(productClient)
                 .validateAllProducts(any());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(1L, 5L, PaymentMethod.STRIPE.getId(), cart);
        });

        String expectedMessage = "Product validation error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void placeOrder_COD_Success() {
        var cart = OrderTestUtils.createCartWithItems();
        var order = orderService.placeOrder(1L, 5L, PaymentMethod.CASH_ON_DELIVERY.getId(), cart);
    }


}