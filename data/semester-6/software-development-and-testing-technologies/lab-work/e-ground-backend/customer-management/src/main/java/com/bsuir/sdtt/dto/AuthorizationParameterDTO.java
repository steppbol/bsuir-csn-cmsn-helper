package com.bsuir.sdtt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
public class AuthorizationParameterDTO {
    @Email
    private String email;

    private String password;
}
