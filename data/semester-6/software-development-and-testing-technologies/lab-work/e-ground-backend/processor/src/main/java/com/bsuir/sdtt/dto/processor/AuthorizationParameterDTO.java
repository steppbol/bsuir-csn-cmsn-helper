package com.bsuir.sdtt.dto.processor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationParameterDTO {
    @Email
    private String email;

    private String password;
}
