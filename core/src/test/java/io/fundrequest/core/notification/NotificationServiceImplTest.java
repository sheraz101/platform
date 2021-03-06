package io.fundrequest.core.notification;

import io.fundrequest.core.notification.domain.Notification;
import io.fundrequest.core.notification.domain.NotificationType;
import io.fundrequest.core.notification.domain.RequestClaimedNotification;
import io.fundrequest.core.notification.domain.RequestClaimedNotificationMother;
import io.fundrequest.core.notification.domain.RequestFundedNotification;
import io.fundrequest.core.notification.domain.RequestFundedNotificationMother;
import io.fundrequest.core.notification.dto.NotificationDto;
import io.fundrequest.core.notification.dto.RequestClaimedNotificationDto;
import io.fundrequest.core.notification.dto.RequestFundedNotificationDto;
import io.fundrequest.core.notification.infrastructure.NotificationRepository;
import io.fundrequest.core.request.RequestService;
import io.fundrequest.core.request.claim.dto.ClaimDto;
import io.fundrequest.core.request.claim.event.RequestClaimedEvent;
import io.fundrequest.core.request.fund.FundService;
import io.fundrequest.core.request.fund.dto.FundDto;
import io.fundrequest.core.request.fund.event.RequestFundedEvent;
import io.fundrequest.core.request.view.FundDtoMother;
import io.fundrequest.core.request.view.RequestDto;
import io.fundrequest.core.request.view.RequestDtoMother;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationServiceImplTest {

    public static final String TRANSACTION_ID = "0x99be7620e58d38ac63267ead0e67d5d7023754e6f6fe0113017f41a610867e44";
    private NotificationServiceImpl notificationService;
    private NotificationRepository notificationRepository;
    private ApplicationEventPublisher eventPublisher;
    private RequestService requestService;
    private FundService fundService;

    @Before
    public void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        when(notificationRepository.save(any(Notification.class))).then(returnsFirstArg());
        when(notificationRepository.saveAndFlush(any(Notification.class))).then(returnsFirstArg());
        eventPublisher = mock(ApplicationEventPublisher.class);
        requestService = mock(RequestService.class);
        fundService = mock(FundService.class);
        notificationService = new NotificationServiceImpl(
                notificationRepository,
                eventPublisher,
                requestService,
                fundService
        );
    }

    @Test
    public void onRequestCreatedClaimedNotification() {
        RequestDto requestDto = RequestDtoMother.freeCodeCampNoUserStories();


        notificationService.onClaimed(new RequestClaimedEvent(TRANSACTION_ID, requestDto, new ClaimDto(), "davyvanroy", LocalDateTime.now()));

        assertRequestClaimedNotificationSaved(requestDto);
    }

    private void assertRequestClaimedNotificationSaved(RequestDto requestDto) {
        ArgumentCaptor<RequestClaimedNotification> requestClaimedNotificationArgumentCaptor = ArgumentCaptor.forClass(RequestClaimedNotification.class);
        verify(notificationRepository).saveAndFlush(requestClaimedNotificationArgumentCaptor.capture());
        assertThat(requestClaimedNotificationArgumentCaptor.getValue().getRequestId()).isEqualTo(requestDto.getId());
        assertThat(requestClaimedNotificationArgumentCaptor.getValue().getDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(requestClaimedNotificationArgumentCaptor.getValue().getType()).isEqualTo(NotificationType.REQUEST_CLAIMED);
    }

    @Test
    public void onRequestClaimedPublishesNotification() {
        RequestDto requestDto = RequestDtoMother.freeCodeCampNoUserStories();

        notificationService.onClaimed(new RequestClaimedEvent(TRANSACTION_ID, requestDto, new ClaimDto(), "davyvanroy", LocalDateTime.now()));

        assertRequestClaimedNotificationPublished();
    }

    private void assertRequestClaimedNotificationPublished() {
        ArgumentCaptor<RequestClaimedNotificationDto> captor = ArgumentCaptor.forClass(RequestClaimedNotificationDto.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.REQUEST_CLAIMED);
    }

    @Test
    public void onRequestFundedCreatesNotification() {
        final Long requestId = 6789L;
        final FundDto fundDto = FundDtoMother.aFundDto();

        notificationService.onFunded(RequestFundedEvent.builder()
                                                       .transactionId(TRANSACTION_ID)
                                                       .fundDto(fundDto)
                                                       .requestId(requestId)
                                                       .timestamp(LocalDateTime.now())
                                                       .build());

        assertRequestFundedNotificationSaved(fundDto);
    }

    private void assertRequestFundedNotificationSaved(FundDto fundDto) {
        ArgumentCaptor<RequestFundedNotification> captor = ArgumentCaptor.forClass(RequestFundedNotification.class);
        verify(notificationRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getFundId()).isEqualTo(fundDto.getId());
        assertThat(captor.getValue().getDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.REQUEST_FUNDED);
    }

    @Test
    public void onRequestFundedPublishesNotification() {
        final Long requestId = 467L;
        final RequestDto requestDto = RequestDtoMother.fundRequestArea51();
        final FundDto fundDto = FundDtoMother.aFundDto();

        when(requestService.findRequest(requestId)).thenReturn(requestDto);

        notificationService.onFunded(RequestFundedEvent.builder()
                                                       .transactionId(TRANSACTION_ID)
                                                       .fundDto(fundDto)
                                                       .requestId(requestId)
                                                       .timestamp(LocalDateTime.now())
                                                       .build());

        assertRequestFundedNotificationPublished(fundDto, requestDto);
    }

    private void assertRequestFundedNotificationPublished(FundDto fundDto, RequestDto requestDto) {
        ArgumentCaptor<RequestFundedNotificationDto> captor = ArgumentCaptor.forClass(RequestFundedNotificationDto.class);
        verify(eventPublisher).publishEvent(captor.capture());
        final RequestFundedNotificationDto requestFundedNotificationDto = captor.getValue();
        assertThat(requestFundedNotificationDto.getFundDto()).isEqualTo(fundDto);
        assertThat(requestFundedNotificationDto.getRequestDto()).isEqualTo(requestDto);
        assertThat(requestFundedNotificationDto.getDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
        assertThat(requestFundedNotificationDto.getType()).isEqualTo(NotificationType.REQUEST_FUNDED);
    }

    @Test
    public void getLast() {
        RequestClaimedNotification requestCreatedNotification = RequestClaimedNotificationMother.aRequestClaimedNotification();
        RequestDto requestDtoForFund = RequestDtoMother.freeCodeCampNoUserStories();
        FundDto fundDto = FundDtoMother.aFundDto();
        RequestFundedNotification requestFundedNotification = RequestFundedNotificationMother.aRequestFundedNotification();
        fundDto.setId(requestFundedNotification.getFundId())
        ;
        List<Notification> notifications = Arrays.asList(
                requestCreatedNotification,
                requestFundedNotification
        );
        when(notificationRepository.findFirst10ByOrderByDateDesc()).thenReturn(notifications);
        RequestDto requestDto = RequestDtoMother.freeCodeCampNoUserStories();
        when(requestService.findAll(Collections.singleton(requestCreatedNotification.getRequestId())))
                .thenReturn(Collections.singletonList(requestDto));


        when(fundService.findAll(Collections.singleton(requestFundedNotification.getFundId())))
                .thenReturn(Collections.singletonList(fundDto));
        when(requestService.findAll(Collections.singleton(fundDto.getRequestId())))
                .thenReturn(Collections.singletonList(requestDtoForFund));

        List<NotificationDto> lastNotifications = notificationService.getLastNotifications();

        assertThat(lastNotifications).hasSize(2);
    }
}