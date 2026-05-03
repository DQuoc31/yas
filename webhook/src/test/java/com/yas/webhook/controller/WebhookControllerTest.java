package com.yas.webhook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WebhookController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void getPageableWebhooks_ReturnsOk() throws Exception {
        when(webhookService.getPageableWebhooks(anyInt(), anyInt())).thenReturn(new WebhookListGetVm());
        mockMvc.perform(get("/backoffice/webhooks/paging"))
            .andExpect(status().isOk());
    }

    @Test
    void listWebhooks_ReturnsOk() throws Exception {
        when(webhookService.findAllWebhooks()).thenReturn(List.of(new WebhookVm()));
        mockMvc.perform(get("/backoffice/webhooks"))
            .andExpect(status().isOk());
    }

    @Test
    void getWebhook_ReturnsOk() throws Exception {
        when(webhookService.findById(anyLong())).thenReturn(new WebhookDetailVm());
        mockMvc.perform(get("/backoffice/webhooks/1"))
            .andExpect(status().isOk());
    }

    @Test
    void createWebhook_ReturnsCreated() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://example.com");
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        
        when(webhookService.create(any(WebhookPostVm.class))).thenReturn(detailVm);
        
        mockMvc.perform(post("/backoffice/webhooks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectWriter.writeValueAsString(postVm)))
            .andExpect(status().isCreated());
    }

    @Test
    void updateWebhook_ReturnsNoContent() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://example.com");
        
        doNothing().when(webhookService).update(any(WebhookPostVm.class), anyLong());
        
        mockMvc.perform(put("/backoffice/webhooks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectWriter.writeValueAsString(postVm)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteWebhook_ReturnsNoContent() throws Exception {
        doNothing().when(webhookService).delete(anyLong());
        mockMvc.perform(delete("/backoffice/webhooks/1"))
            .andExpect(status().isNoContent());
    }
}
