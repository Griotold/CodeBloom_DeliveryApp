package com.sparta.project.service;

import com.sparta.project.client.PgClient;
import com.sparta.project.domain.Order;
import com.sparta.project.domain.Payment;
import com.sparta.project.domain.User;
import com.sparta.project.domain.enums.OrderType;
import com.sparta.project.domain.enums.Role;
import com.sparta.project.dto.payment.PaymentResponse;
import com.sparta.project.dto.payment.PaymentRequest;
import com.sparta.project.repository.OrderRepository;
import com.sparta.project.repository.PaymentRepository;
import com.sparta.project.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    PgClient pgClient;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createUser("griotold");
        userRepository.save(testUser);
        testOrder = createOrder(testUser); // Order 객체 생성 로직 추가
        orderRepository.save(testOrder);
    }

    @Test
    void createPayment_Success() {
        // given
        String orderId;
        String type;
        int paymentPrice;
        String pgName;

        PaymentRequest paymentRequest = new PaymentRequest(
                orderId = testOrder.getOrderId(),
                type = "CARD",
                paymentPrice = 10000,
                pgName = "TOSS"
        );



        when(pgClient.requestPayment(any(Payment.class))).thenReturn(true);

        // when
        PaymentResponse response = paymentService.createPayment(paymentRequest, testUser.getUserId());

        // then
        assertThat(response).isNotNull();

        Optional<Payment> savedPayment = paymentRepository.findById(response.paymentId());
        assertThat(savedPayment.isPresent());
        assertThat(testOrder.getOrderId()).isEqualTo(savedPayment.get().getOrder().getOrderId());
    }

    private User createUser(String username) {
        return User.create(username, "1234", "닉네임", Role.CUSTOMER);
    }

    private Order createOrder(User user) {
        return Order.create(user, null, null, OrderType.ONLINE, 10_000, "요구사항");
    }
}