package com.university.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.university.models.ConfiguracionGlobal;
import com.university.models.dto.ConfiguracionGlobalDto;
import com.university.repositories.ConfiguracionGlobalRepository;
import com.university.tools.mappers.ConfiguracionGlobalMapper;

@Service
public class ConfiguracionGlobalService extends com.university.services.Service {

    @Autowired
    private ConfiguracionGlobalRepository configuracionGlobalRepository;

    /**
     * Busca la configuracion y la devuelve
     *
     * @return
     * @throws Exception
     */
    public ConfiguracionGlobal getConfig() throws Exception {
        ConfiguracionGlobal search = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);
        if (search == null) {
            throw new Exception("Configuraciones no encontradas");
        }
        return search;
    }

    /**
     * Busca la configuracion y la devuelve
     *
     * @return
     * @throws Exception
     */
    public ConfiguracionGlobalDto getConfigDto() throws Exception {
        ConfiguracionGlobal search = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);
        if (search == null) {
            throw new Exception("Configuraciones no encontradas");
        }
        return ConfiguracionGlobalMapper.INSTANCE.configuracionGlobalToConfiguracionGlobalDto(search);
    }

    /**
     * Busca la configuracion y actualiza los dato segun la nueva
     * configuracion
     *
     * @param config
     * @return
     * @throws Exception
     */
    public ConfiguracionGlobalDto actualizarConfig(ConfiguracionGlobal config) throws Exception {
        //validamos el objeto
        this.validar(config);
        //traemos la configuracion
        ConfiguracionGlobal search = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);

        if (search == null) {
            throw new Exception("Configuraciones no encontradas");
        }

        //seteamos la id de la configuracion al objeto d eactualizacion
        config.setId(search.getId());
        config.setImagen(search.getImagen());//se evita la actualizacion de la imagen
        config.setMimeTypeImg(search.getMimeTypeImg());
        //guardamos la info
        ConfiguracionGlobal save = this.configuracionGlobalRepository.save(config);

        return ConfiguracionGlobalMapper.INSTANCE.configuracionGlobalToConfiguracionGlobalDto(save);
    }

    /**
     * Busca la configuracion y actualiza la imagen
     *
     * @param file
     * @return
     * @throws Exception
     */
    public ConfiguracionGlobalDto actualizarImagen(
            MultipartFile file) throws Exception {
        if (file == null) {
            throw new Exception("Imagen vacia.");
        }
        //traemos la configuracion
        ConfiguracionGlobal search = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);

        if (search == null) {
            throw new Exception("Configuraciones no encontradas");
        }

        search.setImagen(file.getBytes());
        search.setMimeTypeImg(file.getContentType());

        //guardamos la info
        ConfiguracionGlobal save = this.configuracionGlobalRepository.save(search);

        return ConfiguracionGlobalMapper.INSTANCE.configuracionGlobalToConfiguracionGlobalDto(save);
    }
}
