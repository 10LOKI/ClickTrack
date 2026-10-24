package com.youcode.clicktrack.service;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;
import com.youcode.clicktrack.dto.OffreResponse;
import com.youcode.clicktrack.dto.ClickResponse;
import java.util.List;

public interface LienService
{
    LienResponse creerLien(Long publisherId, CreerLienRequest request);
    List<LienResponse> listerLiens(Long publisherId);
    List<OffreResponse> listerOffres(Long publisherId);
    ClickResponse compterClic(Long lienId);
}
