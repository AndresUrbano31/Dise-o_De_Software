package com.biblioteca.repository;

import com.biblioteca.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

     // Al extender MongoRepository, Spring genera automáticamente estos métodos:
     //
     // Usuario save(Usuario usuario)          → Crear o actualizar un documento
     // Optional<Usuario> findById(String id) → Buscar un documento por su ID
     // List<Usuario> findAll()            → Obtener todos los documentos
     // void deleteById(String id)       → Eliminar un documento por su ID
     // boolean existsById(String id)   → Verificar si un documento existe
     // long count()                     → Contar el total de documentos
}
