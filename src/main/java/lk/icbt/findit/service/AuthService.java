package lk.icbt.findit.service;

import lk.icbt.findit.dto.AuthDto;

public interface AuthService {

    public AuthDto register(AuthDto authDto);

    public AuthDto login(AuthDto authDto);
}
