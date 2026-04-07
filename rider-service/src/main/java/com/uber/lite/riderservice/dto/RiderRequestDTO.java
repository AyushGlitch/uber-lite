package com.uber.lite.riderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RiderRequestDTO(
   @NotNull(message = "Name cannot be null")
   @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
   String name,

   @NotNull(message = "Email cannot be null")
   @Email(message = "Email should be valid")
   String email
) {}
