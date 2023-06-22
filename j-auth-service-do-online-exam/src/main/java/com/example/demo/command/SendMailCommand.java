package com.example.demo.command;

import java.util.List;

import com.example.demo.common.annotations.CheckListSize;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMailCommand {
    @CheckListSize(message = "Emails are mandatory")
    private List<String> emails;

    @NotEmpty(message = "Subject is mandatory")
    private String subject;

    @NotEmpty(message = "Body is mandatory")
    private String body;
}
