package com.oudersamir.requests;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {
private Long id;
private String firstName;
private String lastName;
private String userId;
private String userName;
private String email;
private String password;
private String confirmPassword;
private Set<RoleRequest> roles=new HashSet<RoleRequest>();
}
