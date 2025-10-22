package com.proyectohotelsoft.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String subirImagen(MultipartFile archivo) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }

    public List<String> subirMultiplesImagenes(List<MultipartFile> archivos) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            urls.add(subirImagen(archivo));
        }
        return urls;
    }


    // ==============================
    // ELIMINAR IMAGEN POR URL
    // ==============================
    public void eliminarImagenPorUrl(String imageUrl) {
        try {
            String publicId = extraerPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar imagen de Cloudinary: " + e.getMessage(), e);
        }
    }

    // ==============================
    // ELIMINAR MÚLTIPLES IMÁGENES
    // ==============================
    public void eliminarMultiplesImagenes(List<String> urls) {
        for (String url : urls) {
            eliminarImagenPorUrl(url);
        }
    }

    private String extraerPublicId(String imageUrl) {
        try {
            // Ejemplo: https://res.cloudinary.com/dvkvps1fy/image/upload/v1761107775/tes43ox9ycvhqfuf78qv.jpg
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            // Tomamos todo después de "/upload/"
            String afterUpload = imageUrl.substring(uploadIndex + 8);

            // Quitamos la parte de versión "v1761107775/"
            int slashIndex = afterUpload.indexOf('/');
            if (slashIndex != -1 && afterUpload.startsWith("v")) {
                afterUpload = afterUpload.substring(slashIndex + 1);
            }

            // Quitamos la extensión del archivo (.jpg, .png, etc.)
            int dotIndex = afterUpload.lastIndexOf('.');
            if (dotIndex != -1) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }
}