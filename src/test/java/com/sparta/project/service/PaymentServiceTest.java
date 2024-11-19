package com.sparta.project.service;

import com.sparta.project.client.PgClient;
import com.sparta.project.domain.*;
import com.sparta.project.domain.enums.OrderType;
import com.sparta.project.domain.enums.Role;
import com.sparta.project.dto.payment.PaymentResponse;
import com.sparta.project.dto.payment.PaymentRequest;
import com.sparta.project.exception.CodeBloomException;
import com.sparta.project.repository.LocationRepository;
import com.sparta.project.repository.OrderRepository;
import com.sparta.project.repository.PaymentRepository;
import com.sparta.project.repository.StoreRepository;
import com.sparta.project.repository.address.AddressRepository;
import com.sparta.project.repository.storecategory.StoreCategoryRepository;
import com.sparta.project.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
// 결제 테스트
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

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    StoreRepository storeRepository;

    @MockBean
    PgClient pgClient;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createUser("griotold");
        userRepository.save(testUser);
        Address address = createAddress(testUser);
        addressRepository.save(address);
        StoreCategory storeCategory = StoreCategory.create("중식", "중식 상세 설명");
        storeCategoryRepository.save(storeCategory);
        Location location = Location.create("서울", "서울 상세 설명");
        locationRepository.save(location);
        Store store = createStore(testUser, storeCategory, location);
        storeRepository.save(store);
        testOrder = createOrder(testUser, address, store);
        orderRepository.save(testOrder);
    }
    @DisplayName("결제 성공")
    @Test
    void createPayment_success() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                testOrder.getOrderId(),
                "CARD",
                10000,
                "TOSS"
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

    @DisplayName("없는 주문을 결제하려고하면 결제 실패한다.")
    @Test
    void createPayment_fail_1() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                "noOrderId", // 없는 주문
                "CARD",
                10000,
                "TOSS"
        );

        when(pgClient.requestPayment(any(Payment.class))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest, testUser.getUserId()))
                .isInstanceOf(CodeBloomException.class)
                .hasMessage("일치하는 주문 정보가 존재하지 않습니다.");
    }

    @DisplayName("주문자와 결제요청자가 다르면 결제 실패한다.")
    @Test
    void createPayment_fail_2() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                testOrder.getOrderId(),
                "CARD",
                10000,
                "TOSS"
        );

        when(pgClient.requestPayment(any(Payment.class))).thenReturn(true);

        User anotherUser = createUser("anotherUser");
        userRepository.save(anotherUser);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest, anotherUser.getUserId()))
                .isInstanceOf(CodeBloomException.class)
                .hasMessage("주문자 정보가 결제자와 일치하지 않습니다.");
    }

    @DisplayName("외부 결제 시스템에서 결제 승인 실패 시 예외 발생")
    @Test
    void createPayment_fail_3() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                testOrder.getOrderId(),
                "CARD",
                10000,
                "TOSS"
        );

        // false 를 반환
        when(pgClient.requestPayment(any(Payment.class))).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest, testUser.getUserId()))
                .isInstanceOf(CodeBloomException.class)
                .hasMessage("결제가 실패하였습니다.");
    }

    @DisplayName("CARD 또는 CASH가 아니면 결제 실패")
    @Test
    void createPayment_fail_4() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                testOrder.getOrderId(),
                "strangeType", // 없는 결제 방식
                10000,
                "TOSS"
        );

        // false 를 반환
        when(pgClient.requestPayment(any(Payment.class))).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest, testUser.getUserId()))
                .isInstanceOf(CodeBloomException.class)
                .hasMessage("지원하지 않는 결제 방식입니다.");
    }

    @DisplayName("NHN, TOSS, KG가 아니면 결제 실패")
    @Test
    void createPayment_fail_5() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                testOrder.getOrderId(),
                "CARD", // 없는 결제 방식
                10000,
                "straneType"
        );

        // false 를 반환
        when(pgClient.requestPayment(any(Payment.class))).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest, testUser.getUserId()))
                .isInstanceOf(CodeBloomException.class)
                .hasMessage("지원하지 않는 PG사입니다.");
    }

    private User createUser(String username) {
        return User.create(username, "1234", "닉네임", Role.CUSTOMER);
    }

    private Order createOrder(User user, Address address, Store store) {
        return Order.create(user, address, store, OrderType.ONLINE, 10_000, "요구사항");
    }

    private Address createAddress(User user) {
        return Address.create(user, null, null, null, null, null, null);
    }

    private Store createStore(User user, StoreCategory storeCategory, Location location) {
        return Store.create("중식당1", "중식당1 상세", "주소지", user, storeCategory, location);
    }
}