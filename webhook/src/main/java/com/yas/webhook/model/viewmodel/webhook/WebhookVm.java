package com.yas.webhook.model.viewmodel.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookVm {
    Long id;
    String payloadUrl;
    String contentType;
    Boolean isActive;
}
