package com.youcode.clicktrack.service;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;

public interface LienService
{
    LienResponse creerLien(Long publisherId, CreerLienRequest request);
}
