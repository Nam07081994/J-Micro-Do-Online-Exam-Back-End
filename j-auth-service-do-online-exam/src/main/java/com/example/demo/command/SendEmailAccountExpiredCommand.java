package com.example.demo.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailAccountExpiredCommand {
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @NotEmpty(message = "Subject is mandatory")
    private String subject;

    @NotEmpty(message = "Body is mandatory")
    private String body;
}
