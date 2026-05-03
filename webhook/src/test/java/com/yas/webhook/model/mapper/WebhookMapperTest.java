package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class WebhookMapperTest {

    private WebhookMapper webhookMapper;

    @BeforeEach
    void setUp() {
        webhookMapper = Mappers.getMapper(WebhookMapper.class);
    }

    @Test
    void toWebhookEventVms_WhenListIsEmpty_ReturnsEmptyList() {
        List<EventVm> result = webhookMapper.toWebhookEventVms(null);
        assertThat(result).isEmpty();
        
        result = webhookMapper.toWebhookEventVms(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void toWebhookEventVms_WhenListHasData_ReturnsMappedList() {
        WebhookEvent event = new WebhookEvent();
        event.setEventId(1L);
        List<EventVm> result = webhookMapper.toWebhookEventVms(List.of(event));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void toWebhookListGetVm_ReturnsMappedVm() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        Page<Webhook> page = new PageImpl<>(List.of(webhook));
        
        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);
        
        assertThat(result.getWebhooks()).hasSize(1);
        assertThat(result.getPageNo()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
