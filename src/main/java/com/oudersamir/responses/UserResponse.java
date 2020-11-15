package com.oudersamir.responses;

import java.util.Set;



import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@XmlRootElement
public class UserResponse {
	private String firstName;
	private String userId;
	private String lastName;
	private String userName;
	private String email;
	private Set<RoleResponse> roles;
}
