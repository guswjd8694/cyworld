package com.hyunjoying.cyworld.user.dto.response;


import com.hyunjoying.cyworld.user.dto.response.schma.ProfileHistory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GetProfileResponseDto {
    private List<ProfileHistory> history;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String email;

    public GetProfileResponseDto(List<ProfileHistory> history, String name, String gender, LocalDate birthday, String email) {
        this.history = history;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
    }
}

