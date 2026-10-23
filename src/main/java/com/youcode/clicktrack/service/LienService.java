package com.youcode.clicktrack.service;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;
import java.util.List;

public interface LienService
{
    LienResponse creerLien(Long publisherId, CreerLienRequest request);
    List<LienResponse> listerLiens(Long publisherId);
}
