package com.dodibo.learncore.tenant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatformUpdateTenantRequest extends UpdateTenantRequest {

    private Boolean active;
}
