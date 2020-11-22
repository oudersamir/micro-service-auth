package com.oudersamir.requests;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UserRequest {
    @NotNull(message = "firstName field cannot be null")
    @NotEmpty(message = "firstName field cannot be Empty")
    @NotBlank(message = "firstName field cannot be Blank")
private String firstName;
    @NotNull(message = "lastName field cannot be null")
    @NotEmpty(message = "lastName field cannot be Empty")
    @NotBlank(message = "lastName field cannot be Blank")
private String lastName;
private String userId;
    @NotNull(message = "userName field cannot be null")
    @NotEmpty(message = "userName field cannot be Empty")
    @NotBlank(message = "userName field cannot be Blank")
private String userName;
    @NotNull(message = "email field cannot be null")
    @NotEmpty(message = "email field cannot be Empty")
    @NotBlank(message = "email field cannot be Blank")
private String email;
    @NotNull(message = "password field cannot be null")
    @NotEmpty(message = "password field cannot be Empty")
    @NotBlank(message = "password field cannot be Blank")
private String password;
    @NotNull(message = "confirmPassword field cannot be null")
    @NotEmpty(message = "confirmPassword field cannot be Empty")
    @NotBlank(message = "confirmPassword field cannot be Blank")
private String confirmPassword;
private Set<RoleRequest> roles=new HashSet<RoleRequest>();
}
