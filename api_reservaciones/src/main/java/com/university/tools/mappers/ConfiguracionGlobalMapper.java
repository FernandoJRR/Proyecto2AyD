package com.university.tools.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.university.models.ConfiguracionGlobal;
import com.university.models.dto.ConfiguracionGlobalDto;

@Mapper
public interface ConfiguracionGlobalMapper {

    ConfiguracionGlobalMapper INSTANCE = Mappers.getMapper(ConfiguracionGlobalMapper.class);

    public ConfiguracionGlobalDto configuracionGlobalToConfiguracionGlobalDto(ConfiguracionGlobal configuracion);
}
