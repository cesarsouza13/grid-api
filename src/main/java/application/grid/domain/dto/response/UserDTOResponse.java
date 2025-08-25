package application.grid.domain.dto.response;


import application.grid.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOResponse {

    private UUID id;

    private String login;

    private String name;


    public static UserDTOResponse fromEntity(User user) {
        if (user == null) return null;

        UserDTOResponse dto = new UserDTOResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());

        return dto;
    }
}