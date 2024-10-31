package com.university.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Encriptador {
    /**
     * Este metodo crea un bucle de 5 iteraciones y por cada iteracion envia y
     * recibe una clave incriptada
     *
     * @param password
     * @return
     */
    public static String encriptarPassword(String password) {
        //repetimos el metodo de incriptacion 5 veces
        password = getSha(password);
        return password;
    }

    /**
     * Este metodo incripta una palabra en SHA
     *
     * @param data
     * @return
     */
    private static String getSha(String data) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(data);
    }

    /**
     * Este metodo compara una contraseña sin encriptar con una contraseña encriptada
     * @param passwordSinEncriptar
     * @param passwordEncriptada
     * @return
     */
    public static boolean compararPassword(String passwordSinEncriptar, String passwordEncriptada) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(passwordSinEncriptar, passwordEncriptada);
    }
}
