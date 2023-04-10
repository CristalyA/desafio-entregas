package br.com.anjun.tracker.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
public class CreateTrackerRequest {

    @NotBlank(message = "Zip code is Required")
    private String zipCode;
    @NotBlank(message = "Number address is Required")
    private String numberAddress;
    @NotBlank(message = "Complement address is Required")
    private String complementAddress;
    @NotBlank(message = "Sender is Required")
    private String sender;
    @NotBlank(message = "Recipient is Required")
    private String recipient;
}

